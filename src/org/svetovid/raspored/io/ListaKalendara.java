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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import org.svetovid.raspored.model.Cas;
import org.svetovid.raspored.util.Dnevnik;
import org.svetovid.raspored.util.Odluka;
import org.svetovid.raspored.util.Proveri;

/**
 * Ova klasa sluzi za cuvanje liste vise kalendara iz kojih se uzimaju podaci za
 * raspored.
 *
 * @author Ivan Pribela
 */
public final class ListaKalendara {

	private final List<Kalendar> kalendari = new ArrayList<>();

	private final Path folder;

	public ListaKalendara(Path folder, Odluka inicijalizacija) {
		Proveri.argument(folder != null, "folder", folder);
		this.folder = folder;
		napraviFolderAkoNePostoji(folder);
		Path putanja = folder.resolve("Kalendari.txt");
		try {
			inicijalizuj(putanja, "/Kalendari.txt", inicijalizacija);
		} catch (IOException e) {
			// Nista, poruka o gresci je vec zapisana
		}
		try {
			kalendari.addAll(ucitaj(putanja));
		} catch (IOException e) {
			// Nista, poruka o gresci je vec zapisana
		}
	}

	public Path getFolder() {
		return folder;
	}

	public Stream<Kalendar> kalendari() {
		return kalendari.stream();
	}

	private boolean napraviFolderAkoNePostoji(Path folder) {
		try {
			Files.createDirectories(folder);
			return true;
		} catch (IOException e) {
			Dnevnik.upozorenje("Nije moguće napraviti folder za kalendare: \"%s\"", e, folder);
			return false;
		}
	}

	public void preuzmi(Odluka preuzimanje) {
		napraviFolderAkoNePostoji(folder);
		for (Kalendar kalendar : kalendari) {
			try {
				kalendar.preuzmi(folder, preuzimanje);
			} catch (IOException e) {
				// Nista, poruka o gresci je vec zapisana
			}
		}
	}

	public List<Cas> parsiraj(Parser parser) {
		List<Cas> casovi = new ArrayList<>();
		for (Kalendar kalendar : kalendari) {
			try {
				casovi.addAll(kalendar.parsiraj(folder, parser));
			} catch (IOException e) {
				// Nista, poruka o gresci je vec zapisana
			}
		}
		return casovi;
	}

	public void inicijalizuj(Path putanja, String resurs, Odluka inicijalizacija) throws IOException {
		Proveri.argument(putanja != null, "putanja", putanja);
		Proveri.argument(resurs != null, "resurs", resurs);
		Proveri.argument(inicijalizacija != null, "inicijalizacija", inicijalizacija);
		URL url = this.getClass().getResource(resurs);
		boolean inicijalizuj = inicijalizacija.odluci(Files.notExists(putanja));
		if (!inicijalizuj) {
			Dnevnik.trag("Nije potrebna inicijalizacija liste kalendara u fajl \"%s\" sa adrese \"%s\"", putanja, url);
			return;
		}
		Dnevnik.trag("Inicijalizacija liste kalendara u fajl \"%s\" sa adrese \"%s\"", putanja, url);
		try {
			try (InputStream in = url.openStream()) {
				Files.copy(in, putanja, StandardCopyOption.REPLACE_EXISTING);
			}
			Dnevnik.info("Lista kalendara je inicijalizovana");
		} catch (NullPointerException e) {
			Dnevnik.upozorenje("Lista kalendara nije inicijalizovana", e);
			throw new IOException(e);
		} catch (IOException e) {
			Dnevnik.upozorenje("Lista kalendara nije inicijalizovana", e);
			throw e;
		}
	}

	public List<Kalendar> ucitaj(Path putanja) throws IOException {
		Proveri.argument(putanja != null, "putanja", putanja);
		Dnevnik.trag("Učitavanje liste kalendara");
		try (BufferedReader in = new BufferedReader(new InputStreamReader(Files.newInputStream(putanja), "UTF-8"))) {
			List<Kalendar> kalendari = ucitaj(in);
			Dnevnik.info("Lista je učitana sa %d kalendara", kalendari.size());
			return kalendari;
		} catch (IOException e) {
			Dnevnik.info("Lista nije učitana", e);
			throw e;
		}
	}

	public List<Kalendar> ucitaj(URL url) throws IOException {
		Proveri.argument(url != null, "url", url);
		Dnevnik.trag("Učitavanje liste kalendara");
		try (BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream(), "UTF-8"))) {
			List<Kalendar> kalendari = ucitaj(in);
			Dnevnik.info("Lista je učitana sa %d kalendara", kalendari.size());
			return kalendari;
		} catch (IOException e) {
			Dnevnik.info("Lista nije učitana", e);
			throw e;
		}
	}

	private static final Pattern pattern = Pattern.compile("(?<ime>.+)\\s+(?<url>.+)");

	protected List<Kalendar> ucitaj(BufferedReader in) throws IOException {
		Proveri.argument(in != null, "in", in);
		List<Kalendar> kalendari = new ArrayList<>();
		try {
			String linija = in.readLine();
			int brLinije = 1;
			while (linija != null) {
				Matcher matcher = pattern.matcher(linija);
				if (matcher.matches()) {
					dodajKalendar(kalendari, matcher.group("ime"), matcher.group("url"));
				} else {
					Dnevnik.trag3("Linija %3d nije dobro formatirana: %s", brLinije, linija);
				}
				linija = in.readLine();
				brLinije++;
			}
		} catch (IOException e) {
			Dnevnik.upozorenje("Greška prilikom učitavanja liste kalendara", e);
		}
		return kalendari;
	}

	private void dodajKalendar(List<Kalendar> kalendari, String ime, String url) {
		try {
			Kalendar kalendar = new Kalendar(ime, new URL(url));
			kalendari.add(kalendar);
		} catch (MalformedURLException e) {
			Dnevnik.upozorenje("Loša adresa do kalendara: \"%s\"", e, url);
		}
	}
}
