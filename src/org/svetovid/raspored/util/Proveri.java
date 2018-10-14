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

import java.util.Collection;
import java.util.function.Function;

/**
 * Pomocna klasa koja sluzi za proveru argumenata u metodima.
 *
 * @author Ivan Pribela
 */
public class Proveri {

	public static <T> T argument(boolean test, String poruka, T vrednost) throws IllegalArgumentException {
		if (!test) {
			throw new IllegalArgumentException(poruka + ": " + vrednost);
		}
		return vrednost;
	}

	public static <T, V extends Iterable<T>> V elemente(Function<T, Boolean> test, String poruka, V kolekcija) throws IllegalArgumentException {
		int indeks = 0;
		for (T element : kolekcija) {
			argument(test.apply(element), poruka + "[" + indeks + "]", element);
			indeks++;
		}
		return kolekcija;
	}

	public static <T> T[] elemente(Function<T, Boolean> test, String poruka, T[] kolekcija) throws IllegalArgumentException {
		int indeks = 0;
		for (T element : kolekcija) {
			argument(test.apply(element), poruka + "[" + indeks + "]", element);
			indeks++;
		}
		return kolekcija;
	}

	public static IllegalArgumentException argument(String poruka, Object vrednost) {
		return new IllegalArgumentException(poruka + ": " + vrednost);
	}

	public static IllegalArgumentException argument(String poruka, Object vrednost, Throwable thrown) {
		return new IllegalArgumentException(poruka + ": " + vrednost, thrown);
	}
}
