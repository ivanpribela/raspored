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
import java.util.Set;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.svetovid.raspored.model.Cas;

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

	public Parser(Iterable<Pattern> patterns) {
		for (Pattern pattern : patterns) {
			Matcher matcher = GRUPE.matcher(pattern.pattern());
			Set<String> groups = new LinkedHashSet<>();
			while(matcher.find()) {
				this.obrasci.put(pattern, groups);
				groups.add(matcher.group(1));
			}
		}
	}

	public List<Cas> parsiraj(String naziv, Path path) throws IOException {
		try (BufferedReader in = new BufferedReader(new InputStreamReader(Files.newInputStream(path)))) {
			return parsiraj(naziv, in);
		}
	}

	public List<Cas> parsiraj(String naziv, URL url) throws IOException {
		try (BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()))) {
			return parsiraj(naziv, in);
		}
	}

	public List<Cas> parsiraj(String naziv, BufferedReader in) throws IOException {
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
		List<Cas> casovi = new ArrayList<>();
		GraditeljCasova graditelj = null;
		for (String linija : linije) {
			if ("BEGIN:VEVENT".equals(linija.trim())) {
				graditelj = new GraditeljCasova();
				graditelj.studenti(naziv);
				continue;
			}
			if (graditelj == null) {
				continue;
			}
			if ("END:VEVENT".equals(linija.trim())) {
				try {
					Cas cas = graditelj.napravi();
					casovi.add(cas);
				} catch (IllegalStateException e) {
					// TODO Obraditi nepotpune casove
				}
				graditelj = null;
				continue;
			}
			for (Entry<Pattern, Set<String>> entry : obrasci.entrySet()) {
				Matcher matcher = entry.getKey().matcher(linija);
				if (matcher.matches()) {
					for (String osobina : entry.getValue()) {
						String vrednost = matcher.group(osobina);
						try {
							Method metod = graditelj.getClass().getMethod(osobina, String.class);
							metod.invoke(graditelj, vrednost);
						} catch (ReflectiveOperationException e) {
							// TODO Obraditi nepostojecu osobinu
						}
					}
				}
			}
		}
		return casovi;
	}
}
