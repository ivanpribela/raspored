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

import java.util.EnumSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.svetovid.raspored.util.Proveri;

/**
 * Ova klasa predstavlja jedan tip casa.
 *
 * @author Ivan Pribela
 */
public enum Tip {

	PREDAVANJA("Predavanja", "P"),
	TEORIJSKE_VEZBE("Vežbe", "V"),
	PRAKTICNE_VEZBE("Praktične vežbe", "RV", "PV", "R");

	private final String naziv;
	private final String[] oznake;

	private Tip(String naziv, String... oznake) {
		this.naziv = naziv;
		Proveri.argument(oznake != null, "oznake", oznake);
		Proveri.argument(oznake.length > 0, "oznake", oznake);
		Proveri.elemente(x -> x != null, "oznake", oznake);
		this.oznake = oznake;
	}

	public String[] getOznake() {
		return oznake;
	}

	public String getNaziv() {
		return naziv;
	}

	// Pretvara string oblika P+V u skup tipova casova
	public static EnumSet<Tip> pretvoriIzOznaka(String tipovi) throws IllegalArgumentException {
		EnumSet<Tip> skup = EnumSet.noneOf(Tip.class);
		String[] oznake = tipovi.split("\\+");
		for (String oznaka : oznake) {
			boolean poznat = false;
			for (Tip tip : Tip.values()) {
				for (String o : tip.getOznake()) {
					if (o.equalsIgnoreCase(oznaka.trim())) {
						skup.add(tip);
						poznat = true;
						break;
					}
				}
			}
			Proveri.argument(poznat, "oznaka", oznaka);
		}
		return skup;
	}

	// Pretvara skup tipova casova u string oblika P+V
	public static String pretvoriUOznake(Set<Tip> tipovi) {
		StringBuilder s = new StringBuilder();
		for (Tip tip : Tip.values()) {
			if (tipovi.contains(tip)) {
				s.append(tip.oznake[0]);
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
