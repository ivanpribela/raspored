package org.svetovid.raspored.io;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.svetovid.raspored.util.Dnevnik;

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
		this.ime = ime;
	}

	public Normalizator(String ime, Collection<Pravilo> pravila) {
		this(ime);
		this.pravila.addAll(pravila);
	}

	public Normalizator(String ime, Pravilo... pravila) {
		this(ime);
		for (Pravilo pravilo : pravila) {
			this.pravila.add(pravilo);
		}
	}

	public Normalizator(String ime, Iterable<Pravilo> pravila) {
		this(ime);
		for (Pravilo pravilo : pravila) {
			this.pravila.add(pravilo);
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
			return string.replaceAll(a, b);
		}
	}
}
