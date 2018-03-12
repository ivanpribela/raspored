package org.svetovid.raspored.model;

import org.svetovid.raspored.util.Proveri;

/**
 * Ova klasa predstavlja vreme kao sat i minut.
 *
 * @author Ivan Pribela
 */
public final class Vreme implements Comparable<Vreme> {

	private final int sat;
	private final int minut;

	public Vreme(int sat, int minut) throws IllegalArgumentException {
		Proveri.argument(sat >= 0 || sat <= 23, "sat", sat);
		Proveri.argument(minut >= 0 || minut <= 59, "minut", minut);
		this.sat = sat;
		this.minut = minut;
	}

	public int getSat() {
		return sat;
	}

	public int getMinut() {
		return minut;
	}

	@Override
	public int hashCode() {
		return 60 * sat + minut;
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}
		if (object == null) {
			return false;
		}
		if (getClass() != object.getClass()) {
			return false;
		}
		Vreme that = (Vreme) object;
		if (this.sat != that.sat) {
			return false;
		}
		if (this.minut != that.minut) {
			return false;
		}
		return true;
	}

	@Override
	public int compareTo(Vreme that) {
		return 60 * this.sat + this.minut - 60 * that.sat - that.minut;
	}

	@Override
	public String toString() {
		return (sat < 10 ? "0" : "") + sat + "." + (minut < 10 ? "0" : "") + minut;
	}

	// Vreme oblika SSMMXX... gde je SS sat, MM minut a XX... sekunde, milisekunde, itd. koje se zanemaruju
	public static Vreme pretvoriISO(String vreme) throws IllegalArgumentException {
		try {
			int sat = Integer.parseInt(vreme.substring(0, 2));
			int minut = Integer.parseInt(vreme.substring(2, 4));
			return new Vreme(sat, minut);
		} catch (IllegalArgumentException e) {
			throw Proveri.argument("vreme", vreme, e);
		}
	}
}
