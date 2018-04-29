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

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.svetovid.raspored.model.Cas;

public class Format {

	public static Function<Cas, String> konstanta(String konstanta) {
		Proveri.argument(konstanta != null, "konstanta", konstanta);
		return (Cas cas) -> konstanta;
	}

	public static Function<Cas, String> kolona(Function<Cas, String> funkcija, int sirina, Poravnanje poravnanje) {
		Proveri.argument(funkcija != null, "funkcija", funkcija);
		Proveri.argument(sirina >= 0, "sirina", sirina);
		Proveri.argument(poravnanje != null, "poravnanje", poravnanje);
		return (Cas cas) -> poravnanje.poravnaj(funkcija.apply(cas), sirina);
	}

	public static Function<Cas, String> konkatenacija(Iterable<Function<Cas, String>> formati) {
		Proveri.argument(formati != null, "formati", formati);
		List<Function<Cas, String>> listaFormata = StreamSupport.stream(formati.spliterator(), false)
				.filter(filter -> filter != null)
				.collect(Collectors.toList());
		return (Cas cas) -> {
			StringBuilder rezultat = new StringBuilder();
			for (Function<Cas, String> format : listaFormata) {
				rezultat.append(format.apply(cas));
			}
			return rezultat.toString();
		};
	}
}
