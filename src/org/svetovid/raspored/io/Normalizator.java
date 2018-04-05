package org.svetovid.raspored.io;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.svetovid.raspored.util.Dnevnik;
import org.svetovid.raspored.util.Proveri;

/**
 * Ova klasa sluzi za cuvanje pravila normalizacije podataka za jednu osobinu
 * casa.
 * 
 * @author Ivan Pribela
 */
public final class Normalizator {

	private final String ime;
	private final List<Pravilo> pravila = new ArrayList<>();

	public Normalizator(String ime) {
		Proveri.argument(ime != null, "ime", ime);
		this.ime = ime;
	}

	public Normalizator(String ime, Collection<Pravilo> pravila) {
		this(ime);
		if (pravila != null) {
			this.pravila.addAll(pravila);
		}
	}

	public Normalizator(String ime, Pravilo... pravila) {
		this(ime);
		if (pravila != null) {
			for (Pravilo pravilo : pravila) {
				this.pravila.add(pravilo);
			}
		}
	}

	public Normalizator(String ime, Iterable<Pravilo> pravila) {
		this(ime);
		if (pravila != null) {
			for (Pravilo pravilo : pravila) {
				this.pravila.add(pravilo);
			}
		}
	}

	public String getIme() {
		return ime;
	}

	public List<Pravilo> getPravila() {
		return pravila;
	}

	public String normalizuj(String original) {
		String string = original;
		for (Pravilo pravilo : pravila) {
			if (pravilo == null) {
				continue;
			}
			while (true) {
				String zamenjeno = pravilo.primeni(string);
				if (zamenjeno.equals(string)) {
					break;
				}
				string = zamenjeno;
			}
		}
		if (string.equals(original)) {
			Dnevnik.trag2("Bez promena:   \"%s\"", original, string);
		} else {
			Dnevnik.trag2("Normalizovano: \"%s\" -> \"%s\"", original, string);
		}
		return string;
	}

	public void ucitaj(Path putanja) throws IOException {
		Proveri.argument(putanja != null, "putanja", putanja);
		Dnevnik.trag("Učitavanje pravila za normalizator osobine \"%s\"", ime);
		try (BufferedReader in = new BufferedReader(new InputStreamReader(Files.newInputStream(putanja)))) {
			ucitaj(in);
			Dnevnik.info("Normalizator osobine \"%s\" je spreman sa %d pravila", ime, pravila.size());
		} catch (IOException e) {
			Dnevnik.info("Normalizator osobine \"%s\" nije učitan", e, ime);
			throw e;
		}
	}

	public void ucitaj(URL url) throws IOException {
		Proveri.argument(url != null, "url", url);
		Dnevnik.trag("Učitavanje pravila za normalizator osobine \"%s\"", ime);
		try (BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()))) {
			ucitaj(in);
			Dnevnik.info("Normalizator osobine \"%s\" je spreman sa %d pravila", ime, pravila.size());
		} catch (IOException e) {
			Dnevnik.info("Normalizator osobine \"%s\" nije učitan", e, ime);
			throw e;
		}
	}

	private static final Pattern linijaA = Pattern.compile("(S|R)(\\s)(.*)");
	private static final Pattern linijaB = Pattern.compile("( )(\\s)(.*)");

	public void ucitaj(BufferedReader in) throws IOException {
		Proveri.argument(in != null, "in", in);
		String linija;
		int brLinije = 0;
		do {
			linija = in.readLine();
			brLinije++;
			if (linija == null) {
				continue;
			}
			Matcher matcher = linijaA.matcher(linija);
			if (!matcher.matches()) {
				Dnevnik.trag3("Linija %3d - Ignorisana:      %s", brLinije, linija);
				continue;
			}
			String tip = matcher.group(1);
			String delimiter = matcher.group(2);
			String a = matcher.group(3);
			Dnevnik.trag3("Linija %3d - Početak pravila: %s", brLinije, linija);
			linija = in.readLine();
			brLinije++;
			if (linija == null) {
				continue;
			}
			matcher = linijaB.matcher(linija);
			if (!matcher.matches()) {
				Dnevnik.trag3("Linija %3d - Ignorisana:      %s", brLinije, linija);
				continue;
			}
			Dnevnik.trag3("Linija %3d - Kraj pravila:    %s", brLinije, linija);
			if (!delimiter.equals(matcher.group(2))) {
				continue;
			}
			String b = matcher.group(3);
			if ("S".equalsIgnoreCase(tip)) {
				pravila.add(new StringPravilo(a, b));
				Dnevnik.trag("String pravilo \"%s\" -> \"%s\"", a, b);
			} else {
				pravila.add(new RegexPravilo(a, b));
				Dnevnik.trag("Regex pravilo \"%s\" -> \"%s\"", a, b);
			}
		} while (linija != null);
	}

	public void sacuvaj(Path putanja) throws IOException {
		Proveri.argument(putanja != null, "putanja", putanja);
		try (BufferedWriter out = new BufferedWriter(new OutputStreamWriter(Files.newOutputStream(putanja)))) {
			sacuvaj(out);
		}
	}

	public void sacuvaj(BufferedWriter out) throws IOException {
		Proveri.argument(out != null, "out", out);
		for (Pravilo pravilo : pravila) {
			out.newLine();
			out.write(pravilo instanceof StringPravilo ? "S\t" : "R\t");
			out.write(pravilo.a);
			out.newLine();
			out.write(" \t");
			out.write(pravilo.b);
			out.newLine();
			Dnevnik.trag("Upisano %s pravilo \"%s\" -> \"%s\"", pravilo instanceof StringPravilo ? "String" : "Regex", pravilo.a, pravilo.b);
		}
	}

	public static abstract class Pravilo {

		protected final String a;
		protected final String b;

		public Pravilo(String a, String b) {
			this.a = a;
			this.b = b;
		}

		public abstract String primeni(String string);

	}

	public static final class StringPravilo extends Pravilo {

		public StringPravilo(String a, String b) {
			super(a, b);
		}

		public String primeni(String string) {
			if (string == null) {
				return null;
			}
			if (string.equals(a)) {
				return b;
			}
			return string;
		}
	}

	public static final class RegexPravilo extends Pravilo {

		public RegexPravilo(String a, String b) {
			super(a, b);
		}

		@Override
		public String primeni(String string) {
			if (string == null) {
				return null;
			}
			return string.replaceAll(a, b);
		}
	}
}
