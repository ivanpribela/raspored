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
