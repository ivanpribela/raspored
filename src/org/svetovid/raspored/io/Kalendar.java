package org.svetovid.raspored.io;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

import org.svetovid.raspored.model.Cas;
import org.svetovid.raspored.util.Dnevnik;
import org.svetovid.raspored.util.Proveri;

/**
 * Ova klasa sluzi za predstavljanje jednog kalendara iz kojeg se uzimaju podaci
 * za raspored.
 *
 * @author Ivan Pribela
 */
public final class Kalendar {

	private final String ime;
	private final URL url;
	private final Path putanja;

	public Kalendar(String ime, URL url) {
		this.ime = ime;
		this.url = url;
		this.putanja = Paths.get(ime + ".ics");
	}

	public String getIme() {
		return ime;
	}

	public URL getUrl() {
		return url;
	}

	public Path getPutanja() {
		return putanja;
	}

	public void preuzmi(Path folder) throws IOException {
		Proveri.argument(folder != null, "folder", folder);
		Path fajl = folder.resolve(putanja);
		Dnevnik.trag("Preuzimanje kalendara \"%s\" u fajl \"%s\" sa adrese \"%s\"", ime, fajl, url);
		try {
			try (InputStream in = url.openStream()) {
				Files.copy(in, fajl, StandardCopyOption.REPLACE_EXISTING);
			}
			Dnevnik.info("Kalendar \"%s\" je preuzet", ime);
		} catch (IOException e) {
			Dnevnik.upozorenje("Kalendar \"%s\" nije je preuzet", e, ime);
			throw e;
		}
	}

	public List<Cas> parsiraj(Path folder, Parser parser) throws IOException {
		Proveri.argument(folder != null, "folder", folder);
		Proveri.argument(parser != null, "parser", parser);
		Dnevnik.trag("Parsiranje kalendara \"%s\"", ime);
		try {
			List<Cas> casovi = parser.parsiraj(ime, folder.resolve(putanja));
			Dnevnik.info("Kalendar \"%s\" je obrađen", ime);
			return casovi;
		} catch (IOException e) {
			Dnevnik.upozorenje("Kalendar \"%s\" nije obrađen", e, ime);
			throw e;
		}
	}
}
