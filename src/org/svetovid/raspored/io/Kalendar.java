package org.svetovid.raspored.io;

import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

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
}
