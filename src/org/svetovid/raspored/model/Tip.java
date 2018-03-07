package org.svetovid.raspored.model;

import java.util.Set;
import java.util.stream.Collectors;

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

	// Pretvara skup tipova casova u string oblika P+V
	public static String pretvoriUOznake(Set<Tip> tipovi) {
		StringBuilder s = new StringBuilder();
		for (Tip tip : Tip.values()) {
			if (tipovi.contains(tip)) {
				s.append(tip.oznaka);
				s.append("+");
			}
		}
		if (s.length() > 0) {
			s.delete(s.length() - 1, s.length());
		}
		return s.toString();
	}

	// {} < {P} < {P, V} < {P, V, RV} < {P, RV} < {V} < {V, RV} < {RV}
	public static int compare(Set<Tip> tipovi1, Set<Tip> tipovi2) {
		String s1 = tipovi1.stream().map(Tip::ordinal).sorted().map(Object::toString).collect(Collectors.joining());
		String s2 = tipovi2.stream().map(Tip::ordinal).sorted().map(Object::toString).collect(Collectors.joining());
		return s1.compareTo(s2);
	}
}
