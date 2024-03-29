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
 * Ova klasa predstavlja jedan termin za cas. On se sastoji od dana u nedelji,
 * vremena pocetka i vremena kraja.
 *
 * @author Ivan Pribela
 */
public final class Termin implements Comparable<Termin> {

	private final Dan dan;
	private final Vreme vremeOd;
	private final Vreme vremeDo;

	public Termin(Dan dan, Vreme vremeOd, Vreme vremeDo) throws IllegalArgumentException {
		Proveri.argument(dan != null, "dan", dan);
		Proveri.argument(vremeOd != null, "vremeOd", vremeOd);
		Proveri.argument(vremeDo != null, "vremeDo", vremeDo);
		this.dan = dan;
		this.vremeOd = vremeOd;
		this.vremeDo = vremeDo;
	}

	public Dan getDan() {
		return dan;
	}

	public Vreme getVremeOd() {
		return vremeOd;
	}

	public Vreme getVremeDo() {
		return vremeDo;
	}

	@Override
	public int hashCode() {
		int result = dan.hashCode();
		result = 24 * 60 * result + vremeOd.hashCode();
		result = 24 * 60 * result + vremeDo.hashCode();
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		Termin that = (Termin) obj;
		if (this.dan != that.dan) {
			return false;
		}
		if (!this.vremeOd.equals(that.vremeOd)) {
			return false;
		}
		if (!this.vremeDo.equals(that.vremeDo)) {
			return false;
		}
		return true;
	}

	@Override
	public int compareTo(Termin that) {
		int razlika = this.dan.compareTo(that.dan);
		if (razlika == 0) {
			razlika = this.vremeOd.compareTo(that.vremeOd);
		}
		if (razlika == 0) {
			razlika = this.vremeDo.compareTo(that.vremeDo);
		}
		return razlika;
	}

	@Override
	public String toString() {
		return dan.getOznaka() + " " + vremeOd + "-" + vremeDo;
	}
}
