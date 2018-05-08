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
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.svetovid.raspored.io.Parser;
import org.svetovid.raspored.model.Cas;
import org.svetovid.raspored.model.Dan;
import org.svetovid.raspored.model.Tip;
import org.svetovid.raspored.util.Filter;
import org.svetovid.raspored.util.Proveri;

public class Main {

	public static void main(String[] arguments) throws IOException {

		// Obrada argumenata
		Opcije opcije = new Opcije();
		for (int i = 0; i < arguments.length / 2; i++) {
			String naziv = arguments[2 * i];
			String vrednost = arguments[2 * i + 1];
			argument(i, naziv, vrednost, opcije);
		}

		// Parsiranje kalendara
		Parser parser = new Parser();
		List<Cas> casovi = parser.parsiraj(arguments[0], Paths.get(arguments[1]));
		Collections.sort(casovi, (cas1, cas2) -> cas1.getTermin().compareTo(cas2.getTermin()));

		// Obrada rasporeda
		Predicate<Cas> filter = opcije.getFilter();
		Comparator<Cas> redosled = opcije.getRedosled();
		int grupisanje = opcije.getGrupisanje();
		casovi = casovi.stream()
				.filter(filter)
				.sorted(redosled)
				.collect(Collectors.toList());

		// Stampanje rasporeda
		Dan dan = null;
		for (Cas cas : casovi) {
			if (dan != cas.getDan()) {
					System.out.println();
				}
			System.out.printf("%10s   %s %s-%s   %-50s %-8s %-30s %-15s %s   %s%n", cas.getStudenti(), cas.getDan().getOznaka(), cas.getVremeOd(), cas.getVremeDo(), cas.getPredmet(), Tip.pretvoriUOznake(cas.getTipovi()), cas.getNastavnik(), cas.getSala(), cas.getId(), cas.getDatumIzmene());
			dan = cas.getDan();
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

			// "uvek" "nikad" "kadfali"
			case "--preuzmi":
				// TODO
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
		// TODO implementirati
	}

	protected static void redosled(String vrednost, Opcije opcije) throws IllegalArgumentException {
		// TODO implementirati
	}
}
