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
