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

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * Ova klasa predstavlja raspored casova. Raspored sadrzi listu casova
 * skupljenih iz jednog ili vise kalendara.
 *
 * @author Ivan Pribela
 */
public class Raspored {

	private List<Cas> casovi = new ArrayList<>();

	public Raspored() {
	}

	public void obrisiSve() {
		casovi.clear();
	}

	public void dodaj(Cas noviCas) {
		int pozicija = casovi.indexOf(noviCas);
		if (pozicija == -1) {
			casovi.add(noviCas);
		} else {
			Cas stariCas = casovi.get(pozicija);
			noviCas = stariCas.spojiSa(noviCas);
			casovi.set(pozicija, noviCas);
		}
	}

	public void dodajSve(List<Cas> noviCasovi) {
		for (Cas cas : noviCasovi) {
			dodaj(cas);
		}
	}

	public Stream<Cas> casovi() {
		return casovi.parallelStream();
	}
}
