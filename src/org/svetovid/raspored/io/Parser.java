package org.svetovid.raspored.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.svetovid.raspored.model.Cas;

/**
 * Ova klasa sluzi za ucitavanje kalendara u iCalendar formatu.
 *
 * @author Ivan Pribela
 */
public final class Parser {

	public Parser() {
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
			// TODO Implementirati pomocu regularnih izraza
		}
		return casovi;
	}
}
