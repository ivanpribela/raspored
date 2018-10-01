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
 * Ova klasa predstavlja jedan semestar.
 *
 * @author Ivan Pribela
 */
public enum Semestar {

	ZIMSKI("Z", "Zimski"),
	LETNJI("L", "Letnji");

	private final String oznaka;
	private final String naziv;

	private Semestar(String oznaka, String naziv) {
		this.oznaka = oznaka;
		this.naziv = naziv;
	}

	public String getOznaka() {
		return oznaka;
	}

	public String getNaziv() {
		return naziv;
	}

	// Prihvata jednoslovnu skracenicu
	public static Semestar pretvoriOznaku(String semestar) throws IllegalArgumentException {
		switch (semestar) {
			case "Z" : case "z" : return ZIMSKI;
			case "L" : case "l" : return LETNJI;
			default  : throw Proveri.argument("semestar", semestar);
		}
	}
}
