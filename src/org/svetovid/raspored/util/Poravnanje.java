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

package org.svetovid.raspored.util;

public enum Poravnanje {

	NISTA("") {
		@Override
		public String poravnaj(String original, int sirina) {
			Proveri.argument(original != null, "original", original);
			Proveri.argument(sirina >= 0, "sirina", sirina);
			return original;
		}
	},

	LEVO("+"){
		@Override
		public String poravnaj(String original, int sirina) {
			Proveri.argument(original != null, "original", original);
			Proveri.argument(sirina >= 0, "sirina", sirina);
			int l = original.length();
			if (l == sirina) {
			return original;
		}
			if (l > sirina) {
				if (sirina == 0) {
					return "";
				}
				if (sirina == 1) {
					return ".";
				}
				if (sirina == 2) {
					return "..";
				}
				return original.substring(0, sirina - 3) + "...";
			}
			StringBuilder rezultat = new StringBuilder(sirina);
			rezultat.append(original);
			for (int i = 0; i < sirina - l; i++) {
				rezultat.append(' ');
			}
			return rezultat.toString();
		}
	},

	DESNO("-") {
		@Override
		public String poravnaj(String original, int sirina) {
			Proveri.argument(original != null, "original", original);
			Proveri.argument(sirina >= 0, "sirina", sirina);
			int l = original.length();
			if (l == sirina) {
			return original;
		}
			if (l > sirina) {
				if (sirina == 0) {
					return "";
				}
				if (sirina == 1) {
					return ".";
				}
				if (sirina == 2) {
					return "..";
				}
				return "..." + original.substring(l - sirina + 3, l);
			}
			StringBuilder rezultat = new StringBuilder(sirina);
			for (int i = 0; i < sirina - l; i++) {
				rezultat.append(' ');
			}
			rezultat.append(original);
			return rezultat.toString();
		}
	},

	CENTAR("*") {
		@Override
		public String poravnaj(String original, int sirina) {
			Proveri.argument(original != null, "original", original);
			Proveri.argument(sirina >= 0, "sirina", sirina);
			return original;
		}
	};

	private final String oznaka;

	private Poravnanje(String oznaka) {
		this.oznaka = oznaka;
	}

	public String getOznaka() {
		return oznaka;
	}

	public abstract String poravnaj(String original, int sirina);

	public static Poravnanje pretvoriIzOznaka(String oznaka) throws IllegalArgumentException {
		for (Poravnanje poravnanje : values()) {
			if (poravnanje.oznaka.equals(oznaka)) {
				return poravnanje;
			}
		}
		throw Proveri.argument("oznaka", oznaka);
	}
}
