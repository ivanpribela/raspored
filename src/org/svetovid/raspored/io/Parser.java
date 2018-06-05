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
import java.lang.reflect.Method;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.svetovid.raspored.model.Cas;
import org.svetovid.raspored.model.Tip;
import org.svetovid.raspored.util.Dnevnik;
import org.svetovid.raspored.util.Proveri;

/**
 * Ova klasa sluzi za ucitavanje kalendara u iCalendar formatu.
 *
 * @author Ivan Pribela
 */
public final class Parser {

	public static final Pattern PATTERN_STUDENTI     = Pattern.compile("^ATTENDEE(;.*)*;CN=(?<studenti>.*?)(;.*)*:mailto.*$");
	public static final Pattern PATTERN_DAN          = Pattern.compile("^RRULE(;.*)*:(.*;)*FREQ=WEEKLY(;.*)*;BYDAY=(?<dan>.*?)(;.*)*$");
	public static final Pattern PATTERN_VREME_OD     = Pattern.compile("^DTSTART(;.*)*:(\\d{8})T(?<vremeOd>\\d{6})Z?$");
	public static final Pattern PATTERN_VREME_DO     = Pattern.compile("^DTEND(;.*)*:(\\d{8})T(?<vremeDo>\\d{6})Z?$");
	public static final Pattern PATTERN_PODACI       = Pattern.compile("^SUMMARY(;.*)*:\\s*(?<predmet>.+?)\\s*\\\\,\\s*(?<nastavnik>.*?)\\s*(\\\\,)?\\s*\\((?<tipovi>.+)\\)\\s*\\\\,\\s*(?<sala>.*)$");
	public static final Pattern PATTERN_ID           = Pattern.compile("^UID(;.*)*:(?<id>.*)$");
	public static final Pattern PATTERN_DATUM_IZMENE = Pattern.compile("^LAST-MODIFIED(;.*)*:(?<datumIzmene>\\d{8}T\\d{6}Z)$");

	public Parser() {
		this(Arrays.asList(
			PATTERN_DAN,
			PATTERN_VREME_OD,
			PATTERN_VREME_DO,
			PATTERN_PODACI,
			PATTERN_ID,
			PATTERN_DATUM_IZMENE
		));
	}

	private static final Pattern GRUPE = Pattern.compile("\\(\\?<(.+?)>.*?\\)");
	private final Map<Pattern, Set<String>> obrasci = new LinkedHashMap<>();

	public Parser(Iterable<Pattern> obrasci) {
		Proveri.argument(obrasci != null, "obrasci", obrasci);
		for (Pattern obrazac : obrasci) {
			Proveri.argument(obrazac != null, "obrasci", obrasci);
			Matcher matcher = GRUPE.matcher(obrazac.pattern());
			Set<String> groups = new LinkedHashSet<>();
			while(matcher.find()) {
				this.obrasci.put(obrazac, groups);
				groups.add(matcher.group(1));
			}
		}
	}

	public List<Cas> parsiraj(String naziv, Path putanja) throws IOException {
		Proveri.argument(putanja != null, "putanja", putanja);
		try (BufferedReader in = new BufferedReader(new InputStreamReader(Files.newInputStream(putanja), "UTF-8"))) {
			return parsiraj(naziv, in);
		}
	}

	public List<Cas> parsiraj(String naziv, URL url) throws IOException {
		Proveri.argument(url != null, "url", url);
		try (BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream(), "UTF-8"))) {
			return parsiraj(naziv, in);
		}
	}

	public List<Cas> parsiraj(String naziv, BufferedReader in) throws IOException {
		Proveri.argument(in != null, "in", in);
		List<String> linije = new ArrayList<String>();
		String linija = in.readLine();
		while (linija != null) {
			if (linija.startsWith(" ") || linija.startsWith("\t")) {
				linija = linija.substring(1);
				String lastLine = linije.remove(linije.size() - 1);
				linija = lastLine + linija;
			}
			linije.add(linija);
			linija = in.readLine();
		}
		return parsiraj(naziv, linije);
	}

	public List<Cas> parsiraj(String naziv, List<String> linije) {
		Proveri.argument(linije != null, "linije", linije);
		List<Cas> casovi = new ArrayList<>();
		GraditeljCasova graditelj = null;
		int brLinije = 0;
		for (String linija : linije) {
			Proveri.argument(linija != null, "linije[" + brLinije + "]", linija);
			brLinije++;
			if ("BEGIN:VEVENT".equals(linija.trim())) {
				graditelj = new GraditeljCasova();
				graditelj.studenti(naziv);
				Dnevnik.trag3("Linija %3d - Početak događaja", brLinije);
				continue;
			}
			if (graditelj == null) {
				Dnevnik.trag3("Linija %3d - Van događaja: %s", brLinije, linija);
				continue;
			}
			if ("END:VEVENT".equals(linija.trim())) {
				Dnevnik.trag3("Linija %3d - Kraj događaja", brLinije);
				try {
					Cas cas = graditelj.napravi();
					casovi.add(cas);
					Dnevnik.trag("Potpun događaj:   %s", formatirajCasZaDnevnik(cas));
				} catch (IllegalStateException e) {
					Dnevnik.trag("Nepotpun događaj: %s", formatirajCasZaDnevnik(graditelj));
				}
				graditelj = null;
				continue;
			}
			boolean zapisano = false;
			for (Entry<Pattern, Set<String>> entry : obrasci.entrySet()) {
				Matcher matcher = entry.getKey().matcher(linija);
				if (matcher.matches()) {
					for (String osobina : entry.getValue()) {
						String vrednost = matcher.group(osobina);
						try {
							Method metod = graditelj.getClass().getMethod(osobina, String.class);
							metod.invoke(graditelj, vrednost);
							if (!zapisano) {
								Dnevnik.trag3("Linija %3d - U događaju:   %s", brLinije, linija);
								zapisano = true;
							}
							Dnevnik.trag2("%s = \"%s\"", osobina, vrednost);
						} catch (ReflectiveOperationException e) {
							Dnevnik.upozorenje("Nepoznata osobina: %s = \"%s\"", e, osobina, vrednost);
						}
					}
				}
			}
			Dnevnik.trag3("Linija %3d - U događaju:   %s", brLinije, linija);
			if (!zapisano) {
				Dnevnik.trag3("Linija %3d - Ignorisana:   %s", brLinije, linija);
			}
		}
		return casovi;
	}

	private Object formatirajCasZaDnevnik(Cas cas) {
		return String.format("%s %s %s %3s %5s-%5s %s, %s (%s) %s",
				cas.getId(),
				cas.getDatumIzmene(),
				cas.getStudenti(),
				cas.getDan().getOznaka(),
				cas.getVremeOd(),
				cas.getVremeDo(),
				cas.getNastavnik(),
				cas.getPredmet(),
				Tip.pretvoriUOznake(cas.getTipovi()),
				cas.getSala()
		);
	}

	private Object formatirajCasZaDnevnik(GraditeljCasova fabrika) {
		return String.format("%s %s %s %3s %5s-%5s %s, %s (%s) %s",
				fabrika.getId() == null ? "" : fabrika.getId(),
				fabrika.getDatumIzmene() == null ? "?" : fabrika.getDatumIzmene(),
				fabrika.getStudenti() == null ? "?" : fabrika.getStudenti(),
				fabrika.getDan() == null ? " - " : fabrika.getDan().getOznaka(),
				fabrika.getVremeOd() == null ? "  ?  " : fabrika.getVremeOd(),
				fabrika.getVremeDo() == null ? "  ?  " : fabrika.getVremeDo(),
				fabrika.getNastavnik() == null ? "???" : fabrika.getNastavnik(),
				fabrika.getPredmet() == null ? "???" : fabrika.getPredmet(),
				fabrika.getTipovi() == null ? "?" : Tip.pretvoriUOznake(fabrika.getTipovi()),
				fabrika.getSala() == null ? "-" : fabrika.getSala()
		);
	}
}
