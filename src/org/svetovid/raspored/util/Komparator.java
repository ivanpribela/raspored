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

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.svetovid.raspored.model.Cas;
import org.svetovid.raspored.model.GodinaISemestar;
import org.svetovid.raspored.model.Termin;
import org.svetovid.raspored.model.Tip;

public class Komparator {

	public static Comparator<Cas> string(Function<Cas, String> funkcija) {
		Proveri.argument(funkcija != null, "funkcija", funkcija);
		return Comparator.comparing(funkcija, String.CASE_INSENSITIVE_ORDER);
	}

	public static Comparator<Cas> skupStringova(Function<Cas, Set<String>> funkcija) {
		Proveri.argument(funkcija != null, "funkcija", funkcija);
		return Comparator.comparing(funkcija, Comparator.comparing(skup -> skup.stream().sorted().map(Object::toString).collect(Collectors.joining())));
	}

	public static Comparator<Cas> tipovi(Function<Cas, Set<Tip>> funkcija) {
		Proveri.argument(funkcija != null, "funkcija", funkcija);
		return Comparator.comparing(funkcija, Tip::compare);
	}

	public static Comparator<Cas> datum(Function<Cas, LocalDateTime> funkcija) {
		Proveri.argument(funkcija != null, "funkcija", funkcija);
		return Comparator.comparing(funkcija);
	}

	public static Comparator<Cas> dan(Function<Cas, Termin> funkcija) {
		Proveri.argument(funkcija != null, "funkcija", funkcija);
		return Comparator.comparing(funkcija, Comparator.comparing(Termin::getDan));
	}

	public static Comparator<Cas> vreme(Function<Cas, Termin> funkcija) {
		Proveri.argument(funkcija != null, "funkcija", funkcija);
		return Comparator.comparing(funkcija, Comparator.comparing(Termin::getVremeOd).thenComparing(Termin::getVremeDo));
	}

	public static Comparator<Cas> semestar(Function<Cas, GodinaISemestar> funkcija) {
		Proveri.argument(funkcija != null, "funkcija", funkcija);
		return Comparator.comparing(funkcija);
	}

	public static Comparator<Cas> visestruki(Iterable<Comparator<Cas>> komparatori) {
		Proveri.argument(komparatori != null, "komparatori", komparatori);
		List<Comparator<Cas>> listaKomparatora = StreamSupport.stream(komparatori.spliterator(), false)
				.filter(filter -> filter != null)
				.collect(Collectors.toList());
		return (Cas cas1, Cas cas2) -> {
			int i = 1;
			for (Comparator<Cas> komparator : listaKomparatora) {
				int rezultat = komparator.compare(cas1, cas2);
				if (rezultat > 0) {
					return i;
				}
				if (rezultat < 0) {
					return -i;
				}
				i++;
			}
			return 0;
		};
	}
}
