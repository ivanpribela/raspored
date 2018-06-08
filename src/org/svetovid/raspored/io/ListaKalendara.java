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
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
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

	private final List<Kalendar> kalendari;

	private final Path folder;

	public ListaKalendara(Path folder) {
		Proveri.argument(folder != null, "folder", folder);
		this.folder = folder;
		napraviFolderAkoNePostoji(folder);
		kalendari = ucitajListuKalendara();
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

	private static final Pattern pattern = Pattern.compile("(?<ime>.+)\\s+(?<url>.+)");

	protected List<Kalendar> ucitajListuKalendara() {
		List<Kalendar> kalendari = new ArrayList<>();
		try (BufferedReader in = new BufferedReader(new InputStreamReader(Files.newInputStream(folder.resolve("kalendari.txt")), "UTF-8"))) {
			String linija = in.readLine();
			while (linija != null) {
				Matcher matcher = pattern.matcher(linija);
				if (matcher.matches()) {
					dodajKalendar(kalendari, matcher.group("ime"), matcher.group("url"));
				} else {
					Dnevnik.upozorenje("Greška pri učitavanju kalendara u liniji " + linija);
				}
				linija = in.readLine();
			}
		} catch (IOException e) {
			Dnevnik.upozorenje("Greška pri učitavanju liste kalendara", e);
		}
		if (kalendari.isEmpty()) {
			Dnevnik.upozorenje("Koriste se podrazumevani kalendari");
			dodajKalendar(kalendari, "1IT", "https://calendar.google.com/calendar/ical/g3khre7jrsih1idp5b5ahgm1f8%40group.calendar.google.com/public/basic.ics");
			dodajKalendar(kalendari, "1RN", "https://calendar.google.com/calendar/ical/dkkfm5u41nijcqnfcpai4tvko4%40group.calendar.google.com/public/basic.ics");
			dodajKalendar(kalendari, "2IT", "https://calendar.google.com/calendar/ical/hu93vkklcv692mikqvm17scnv4%40group.calendar.google.com/public/basic.ics");
			dodajKalendar(kalendari, "2RN", "https://calendar.google.com/calendar/ical/4m68kac06oau937i0kfsglv4vo%40group.calendar.google.com/public/basic.ics");
			dodajKalendar(kalendari, "3IT", "https://calendar.google.com/calendar/ical/6ovsf0fqb19q10s271b9e59ttc%40group.calendar.google.com/public/basic.ics");
			dodajKalendar(kalendari, "3RN", "https://calendar.google.com/calendar/ical/0rm1s1krr0e83m5dn7jfdjtbg8%40group.calendar.google.com/public/basic.ics");
			dodajKalendar(kalendari, "4",   "https://calendar.google.com/calendar/ical/a730slcmbr9c6dii94j9pbdir4%40group.calendar.google.com/public/basic.ics");
			dodajKalendar(kalendari, "M",   "https://calendar.google.com/calendar/ical/slmamsigaoclkjvvroj1t280v8%40group.calendar.google.com/public/basic.ics");
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
