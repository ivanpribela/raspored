package org.svetovid.raspored.util;

/**
 * Pomocna klasa koja sluzi za proveru argumenata u metodima.
 *
 * @author Ivan Pribela
 */
public class Proveri {

	public static <T> T argument(boolean test, String poruka, T vrednost) throws IllegalArgumentException {
		if (!test) {
			throw new IllegalArgumentException(poruka + ": " + vrednost);
		}
		return vrednost;
	}

	public static IllegalArgumentException argument(String poruka, Object vrednost) {
		return new IllegalArgumentException(poruka + ": " + vrednost);
	}

	public static IllegalArgumentException argument(String poruka, Object vrednost, Throwable thrown) {
		return new IllegalArgumentException(poruka + ": " + vrednost, thrown);
	}
}
