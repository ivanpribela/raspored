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
 * Ova klasa predstavlja jedan dan u nedelji.
 *
 * @author Ivan Pribela
 */
public enum Dan {

	PONEDELJAK("Pon", "Ponedeljak", true),
	UTORAK    ("Uto", "Utorak", true),
	SREDA     ("Sre", "Sreda", true),
	CETVRTAK  ("Čet", "Četvrtak", true),
	PETAK     ("Pet", "Petak", true),
	SUBOTA    ("Sub", "Subota", false),
	NEDELJA   ("Ned", "Nedelja", false);

	private final String oznaka;
	private final String naziv;
	private final boolean radni;

	private Dan(String oznaka, String naziv, boolean radni) {
		this.oznaka = oznaka;
		this.naziv = naziv;
		this.radni = radni;
	}

	public String getOznaka() {
		return oznaka;
	}

	public String getNaziv() {
		return naziv;
	}

	public boolean isRadni() {
		return radni;
	}

	// Prihvata englesku dvoslovnu skracenicu
	public static Dan pretvoriEngleskuDvoslovnuOznaku(String dan) throws IllegalArgumentException {
		switch (dan) {
			case "MO" : return PONEDELJAK;
			case "TU" : return UTORAK;
			case "WE" : return SREDA;
			case "TH" : return CETVRTAK;
			case "FR" : return PETAK;
			case "SA" : return SUBOTA;
			case "SU" : return NEDELJA;
			default   : throw Proveri.argument("dan", dan);
		}
	}
}
