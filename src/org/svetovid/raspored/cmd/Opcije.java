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

package org.svetovid.raspored.cmd;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import org.svetovid.raspored.model.Cas;

public class Opcije {

	public Opcije() {
		putanja = null;
		filteri = new LinkedHashMap<>();
	}

	////////////
	// Opcije //
	////////////

	protected Path putanja;

	public Path getPutanja() {
		return putanja;
	}

	public void setPutanja(Path putanja) {
		this.putanja = putanja;
	}

	// TODO Dodati cuvanje ostalih standardnih opcija

	///////////
	// Ispis //
	///////////

	// TODO Dodati cuvanje opcija za ispis

	/////////////
	// Filteri //
	/////////////

	protected final Map<String, List<Predicate<Cas>>> filteri;

	public void addFilter(String kljuc, Predicate<Cas> filter) {
		List<Predicate<Cas>> lista = filteri.get(kljuc);
		if (lista == null) {
			lista = new ArrayList<>();
			filteri.put(kljuc, lista);
		}
		lista.add(filter);
	}
}
