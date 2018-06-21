/*
 * Copyright 2018 Ivan Pribela
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.svetovid.raspored.cmd;

import java.io.IOException;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.svetovid.raspored.io.Menadzer;
import org.svetovid.raspored.model.Cas;
import org.svetovid.raspored.model.Raspored;
import org.svetovid.raspored.model.Tip;
import org.svetovid.raspored.util.Filter;
import org.svetovid.raspored.util.Format;
import org.svetovid.raspored.util.Komparator;
import org.svetovid.raspored.util.Odluka;
import org.svetovid.raspored.util.Poravnanje;
import org.svetovid.raspored.util.Proveri;

public class Main {

	public static void main(String[] arguments) throws IOException {

		// Obrada argumenata
		if (arguments.length % 2 == 1) {
			String[] newArguments = new String[arguments.length + 1];
			System.arraycopy(arguments, 0, newArguments, 0, arguments.length);
			newArguments[arguments.length] = "";
			arguments = newArguments;
		}
		Opcije opcije = new Opcije();
		for (int i = 0; i < arguments.length / 2; i++) {
			String naziv = arguments[2 * i];
			String vrednost = arguments[2 * i + 1];
			argument(i, naziv, vrednost, opcije);
		}
		if (opcije.getRedosled() == null) {
			redosled("1dvpni", opcije);
		}
		if (opcije.getFormat() == null) {
			format("%+10s | %d %v | %+50p %+7t %+30n %+15l", opcije);
		}

		// Parsiranje kalendara
		Path putanja = opcije.getPutanja();
		Odluka inicijalizacija = opcije.getInicijalizacija();
		Odluka preuzimanje = opcije.getPreuzimanje();
		Menadzer menadzer = new Menadzer(putanja, inicijalizacija, preuzimanje);
		Raspored raspored = menadzer.getRaspored();

		// Obrada rasporeda
		Predicate<Cas> filter = opcije.getFilter();
		Comparator<Cas> redosled = opcije.getRedosled();
		int grupisanje = opcije.getGrupisanje();
		List<Cas> casovi = raspored.casovi()
				.filter(filter)
				.sorted(redosled)
				.collect(Collectors.toList());

		// Stampanje rasporeda
		Function<Cas, String> format = opcije.getFormat();
		Cas prethodni = null;
		for (Cas cas : casovi) {
			int delta = prethodni == null ? 0 : redosled.compare(cas, prethodni);
			if (delta != 0 && delta * delta <= grupisanje * grupisanje) {
				for (int i = 0; i < grupisanje - delta + 1; i++) {
					System.out.println();
				}
			}
			System.out.println(format.apply(cas));
			prethodni = cas;
		}
	}

	protected static void argument(int pozicija, String naziv, String vrednost, Opcije opcije) throws IllegalArgumentException {
		switch (naziv) {

			////////////
			// Opcije //
			////////////

			// "/putanja/do/foldera"
			case "--folder":
				try {
					opcije.setPutanja(Paths.get(vrednost));
				} catch (InvalidPathException e) {
					throw Proveri.argument("Putanja nije dobra", vrednost, e);
				}
				break;

			// "nikad" "po-potrebi" "uvek"
			case "--inicijalizuj":
				try {
					opcije.setInicijalizacija(Odluka.pretvoriIzOznaka(vrednost));
				} catch (IllegalArgumentException e) {
					throw Proveri.argument("Oznaka inicijalizacije nije dobra", vrednost, e);
				}
				break;

			// "nikad" "po-potrebi" "uvek"
			case "--preuzmi":
				try {
					opcije.setPreuzimanje(Odluka.pretvoriIzOznaka(vrednost));
				} catch (IllegalArgumentException e) {
					throw Proveri.argument("Oznaka preuzimanja nije dobra", vrednost, e);
				}
				break;

			///////////
			// Ispis //
			///////////

			// "%-15s %v %25p %25n %t %l %i %z %%"
			case "-f": case "--format":
				format(vrednost, opcije);
				break;

			// "1dvi" "ptdv"
			case "-r": case "--redosled": case "--sort":
				redosled(vrednost, opcije);
				break;

			/////////////
			// Filteri //
			/////////////

			// "=1-IT" "RN"
			case "-s": case "--smer":
				opcije.addFilter("S", Filter.skupStringova(vrednost, Cas::getStudenti));
				break;

			// "pon" "uto"
			case "-d": case "--dan":
				opcije.addFilter("D", Filter.stringJednak(vrednost, cas -> cas.getDan().getOznaka()));
				break;

			// "od>=12:15" "do<15.00"
			case "-v": case "--vreme":
				opcije.addFilter("V" + pozicija, Filter.termin(vrednost));
				break;

			// "=Naziv predmeta" "predm"
			case "-p": case "--predmet":
				opcije.addFilter("P", Filter.string(vrednost, Cas::getPredmet));
				break;

			// "=Pera Peric" "Pera"
			case "-n": case "--nastavnik":
				opcije.addFilter("N", Filter.string(vrednost, Cas::getNastavnik));
				break;

			// "=P" "rv"
			case "-t": case "--tip":
				opcije.addFilter("T", Filter.string(vrednost, cas -> Tip.pretvoriUOznake(cas.getTipovi())));
				break;

			// "=Sala 65" "65"
			case "-l": case "--sala": case "--lokacija":
				opcije.addFilter("L", Filter.string(vrednost, Cas::getSala));
				break;

			// "=1a2b3c4d5e6f@domen.com" "C4D5"
			case "-i": case "--id":
				opcije.addFilter("I", Filter.string(vrednost, Cas::getId));
				break;

			// "<2m" ">3d"
			case "-z": case "--izmena":
				opcije.addFilter("Z" + pozicija, Filter.datum(vrednost, Cas::getDatumIzmene));
				break;

			///////////////
			// Nepoznato //
			///////////////

			default:
				throw Proveri.argument("Nepoznata opcija", naziv);
		}
	}

	protected static void format(String vrednost, Opcije opcije) throws IllegalArgumentException {

		// Provera argumenata
		Proveri.argument(!vrednost.isEmpty(), "format", vrednost);

		// Kolone
		Pattern mustra = Pattern.compile("%([*+-]?)(\\d*)([sdvpntliz%])");
		Matcher matcher = mustra.matcher(vrednost);
		List<Function<Cas, String>> formati = new ArrayList<>();
		int pocetak = 0;
		while (matcher.find()) {

			// Tekst izmedju kolona
			String konstanta = vrednost.substring(pocetak, matcher.start());
			pocetak = matcher.end();
			if (!konstanta.isEmpty()) {
				formati.add(Format.konstanta(konstanta));
			}

			// Kolona sa osobinom
			Poravnanje poravnanje = Poravnanje.pretvoriIzOznaka(matcher.group(1));
			String s = matcher.group(2);
			int sirina = 0;
			if (!"".equals(s)) {
				try {
					sirina = Integer.parseInt(s);
				} catch (NumberFormatException e) {
					throw Proveri.argument("format", vrednost);
				}
			}
			Function<Cas, String> kolona;
			switch (matcher.group(3)) {
				case "s": case "S": kolona = Format.kolona(cas -> cas.getStudenti().toString(), sirina, poravnanje); break;
				case "d": case "D": kolona = Format.kolona(cas -> cas.getDan().getOznaka(), sirina, poravnanje); break;
				case "v": case "V": kolona = Format.kolona(cas -> cas.getVremeOd() + "-" + cas.getVremeDo(), sirina, poravnanje); break;
				case "p": case "P": kolona = Format.kolona(Cas::getPredmet, sirina, poravnanje); break;
				case "n": case "N": kolona = Format.kolona(Cas::getNastavnik, sirina, poravnanje); break;
				case "t": case "T": kolona = Format.kolona(cas -> Tip.pretvoriUOznake(cas.getTipovi()), sirina, poravnanje); break;
				case "l": case "L": kolona = Format.kolona(Cas::getSala, sirina, poravnanje); break;
				case "i": case "I": kolona = Format.kolona(Cas::getId, sirina, poravnanje); break;
				case "z": case "Z": kolona = Format.kolona(cas -> cas.getDatumIzmene().toString(), sirina, poravnanje); break;
				default: throw Proveri.argument("Nepoznata opcija formatiranja", matcher.group(3));
			}
			formati.add(kolona);

		}

		// Postavimo format
		opcije.setFormat(Format.konkatenacija(formati));

	}

	protected static void redosled(String vrednost, Opcije opcije) throws IllegalArgumentException {

		// Provera argumenata
		Proveri.argument(!vrednost.isEmpty(), "redosled", vrednost);

		// Grupisanje
		char ch0 = vrednost.charAt(0);
		if (ch0 >= '0' && ch0 <= '9') {
			opcije.setGrupisanje(Math.min(ch0 - '0', vrednost.length() - 1));
			vrednost = vrednost.substring(1);
		} else {
			opcije.setGrupisanje(0);
		}

		// Komparatori
		List<Comparator<Cas>> komparatori = new ArrayList<>();
		for (char ch : vrednost.toCharArray()) {
			Comparator<Cas> komparator;
			switch (ch) {
				case 's': case 'S': komparator = Komparator.skupStringova(Cas::getStudenti); break;
				case 'd': case 'D': komparator = Komparator.dan(Cas::getTermin); break;
				case 'v': case 'V': komparator = Komparator.vreme(Cas::getTermin); break;
				case 'p': case 'P': komparator = Komparator.string(Cas::getPredmet); break;
				case 'n': case 'N': komparator = Komparator.string(Cas::getNastavnik); break;
				case 't': case 'T': komparator = Komparator.tipovi(Cas::getTipovi); break;
				case 'l': case 'L': komparator = Komparator.string(Cas::getSala); break;
				case 'i': case 'I': komparator = Komparator.string(Cas::getId); break;
				case 'z': case 'Z': komparator = Komparator.datum(Cas::getDatumIzmene); break;
				default: throw Proveri.argument("redosled", ch);
			}
			if (Character.isUpperCase(ch)) {
				komparator = komparator.reversed();
			}
			komparatori.add(komparator);
		}

		// Postavimo redosled
		opcije.setRedosled(Komparator.visestruki(komparatori));

	}
}
