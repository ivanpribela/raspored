package org.svetovid.raspored.model;

/**
 * Ova klasa predstavlja jedan tip casa.
 *
 * @author Ivan Pribela
 */
public enum Tip {

	PREDAVANJA("P", "Predavanja"),
	TEORIJSKE_VEZBE("V", "Vežbe"),
	PRAKTICNE_VEZBE("RV", "Praktične vežbe");

	private final String oznaka;
	private final String naziv;

	private Tip(String oznaka, String naziv) {
		this.oznaka = oznaka;
		this.naziv = naziv;
	}

	public String getOznaka() {
		return oznaka;
	}

	public String getNaziv() {
		return naziv;
	}
}
