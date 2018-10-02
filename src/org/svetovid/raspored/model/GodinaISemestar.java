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

import java.time.LocalDate;

import org.svetovid.raspored.util.Proveri;

/**
 * Ova klasa predstavlja jedan semestar odredjene skolske godine.
 *
 * @author Ivan Pribela
 */
public final class GodinaISemestar implements Comparable<GodinaISemestar> {

	private final int godina;
	private final Semestar semestar;

	public GodinaISemestar(int godina, Semestar semestar) throws IllegalArgumentException {
		Proveri.argument(semestar != null, "semestar", semestar);
		this.godina = godina;
		this.semestar = semestar;
	}

	public GodinaISemestar(int godina, int mesec) throws IllegalArgumentException {
		Proveri.argument(mesec >= 1, "mesec", mesec);
		Proveri.argument(mesec <= 12, "mesec", mesec);
		int korekcija = 0;
		if (mesec == 1) {
			this.semestar = Semestar.ZIMSKI;
			korekcija = 1;
		} else if (mesec < 8) {
			this.semestar = Semestar.LETNJI;
			korekcija = 1;
		} else {
			this.semestar = Semestar.ZIMSKI;
		}
		this.godina = godina - korekcija;
	}

	public int getGodina() {
		return godina;
	}

	public Semestar getSemestar() {
		return semestar;
	}

	@Override
	public int hashCode() {
		int result = godina;
		result = 10 * result + semestar.hashCode();
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
		GodinaISemestar that = (GodinaISemestar) obj;
		if (this.godina != that.godina) {
			return false;
		}
		if (this.semestar != that.semestar) {
			return false;
		}
		return true;
	}

	@Override
	public int compareTo(GodinaISemestar that) {
		int razlika = Integer.compare(this.godina, that.godina);
		if (razlika == 0) {
			razlika = this.semestar.compareTo(that.semestar);
		}
		return razlika;
	}

	@Override
	public String toString() {
		String godina = this.godina + "/";
		if (this.godina % 100 == 99) {
			godina = godina + (this.godina + 1);
		} else {
			godina = godina + String.format("%02d", (this.godina + 1) % 100);
		}
		return godina + semestar.getOznaka();
	}

	public static GodinaISemestar pretvoriIzISO(String iso) {
		return new GodinaISemestar(Integer.parseInt(iso.substring(0, 4)), Integer.parseInt(iso.substring(4, 6)));
	}

	public static GodinaISemestar pretvoriIzOznake(String oznaka) {
		return new GodinaISemestar(Integer.parseInt(oznaka.substring(0, 4)), Semestar.pretvoriOznaku(oznaka.substring(oznaka.length() - 1)));
	}

	public static GodinaISemestar pretvoriIzDatuma(LocalDate datum) throws IllegalArgumentException {
		Proveri.argument(datum != null, "datum", datum);
		return new GodinaISemestar(datum.getYear(), datum.getMonthValue());
	}
}
