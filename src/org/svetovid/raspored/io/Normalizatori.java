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

package org.svetovid.raspored.io;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

import org.svetovid.raspored.model.Cas;
import org.svetovid.raspored.util.Dnevnik;
import org.svetovid.raspored.util.Odluka;
import org.svetovid.raspored.util.Proveri;

/**
 * Ova klasa sluzi za cuvanje pravila normalizacije podataka za sve osobine
 * casa.
 *
 * @author Ivan Pribela
 */
public final class Normalizatori {

	private final Normalizator normalizatorSmerova;
	private final Normalizator normalizatorPredmeta;
	private final Normalizator normalizatorNastavnika;
	private final Normalizator normalizatorSala;

	private final Path folder;

	private final Path putanjaZaSmerove;
	private final Path putanjaZaPredmete;
	private final Path putanjaZaNastavnike;
	private final Path putanjaZaSale;

	public Normalizatori(Path folder, Odluka inicijalizacija) {
		Proveri.argument(folder != null, "folder", folder);
		this.folder = folder;
		napraviFolderAkoNePostoji(folder);
		putanjaZaSmerove = folder.resolve("Smerovi.txt");
		putanjaZaPredmete = folder.resolve("Predmeti.txt");
		putanjaZaNastavnike = folder.resolve("Nastavnici.txt");
		putanjaZaSale = folder.resolve("Sale.txt");
		normalizatorSmerova = inicijalizuj("Smer", putanjaZaSmerove, "/Smerovi.txt", inicijalizacija);
		normalizatorPredmeta = inicijalizuj("Predmet", putanjaZaPredmete, "/Predmeti.txt", inicijalizacija);
		normalizatorNastavnika = inicijalizuj("Nastavnik", putanjaZaNastavnike, "/Nastavnici.txt", inicijalizacija);
		normalizatorSala = inicijalizuj("Sala", putanjaZaSale, "/Sale.txt", inicijalizacija);
	}

	public Path getFolder() {
		return folder;
	}

	private boolean napraviFolderAkoNePostoji(Path folder) {
		try {
			Files.createDirectories(folder);
			return true;
		} catch (IOException e) {
			Dnevnik.upozorenje("Nije moguće napraviti folder za normalizatore: \"%s\"", e, folder);
			return false;
		}
	}

	protected Normalizator inicijalizuj(String ime, Path putanja, String resurs, Odluka inicijalizacija) {
		Proveri.argument(ime != null, "ime", ime);
		Proveri.argument(putanja != null, "putanja", putanja);
		Proveri.argument(resurs != null, "resurs", resurs);
		Proveri.argument(inicijalizacija != null, "inicijalizacija", inicijalizacija);
		Normalizator normalizator = new Normalizator(ime);
		try {
			normalizator.inicijalizuj(resurs, putanja, inicijalizacija);
		} catch (IOException e) {
			// Nista, poruka o gresci je vec zapisana
		}
		try {
			normalizator.ucitaj(putanja);
		} catch (IOException e) {
			// Nista, poruka o gresci je vec zapisana
		}
		return normalizator;
	}

	public Cas normalizuj(Cas cas) {
		Proveri.argument(cas != null, "cas", cas);
		Dnevnik.trag("Normalizacija časa %s", cas.getId());
		Set<String> normalizovaniStudenti = new HashSet<>();
		for (String smer : cas.getStudenti()) {
			normalizovaniStudenti.add(normalizatorSmerova.normalizuj(smer));
		}
		String predmet = cas.getPredmet();
		String normalizovaniPredmet = normalizatorPredmeta.normalizuj(predmet);
		String nastavnik = cas.getNastavnik();
		String normalizovaniNastavnik = normalizatorNastavnika.normalizuj(nastavnik);
		String sala = cas.getSala();
		String normalizovanaSala = normalizatorSala.normalizuj(sala);
		return new Cas(normalizovaniStudenti, cas.getTermin(), normalizovaniPredmet, normalizovaniNastavnik, cas.getTipovi(), normalizovanaSala, cas.getId(), cas.getDatumIzmene());
	}
}
