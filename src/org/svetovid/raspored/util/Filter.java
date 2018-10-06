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
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.function.IntPredicate;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.svetovid.raspored.model.Cas;
import org.svetovid.raspored.model.GodinaISemestar;
import org.svetovid.raspored.model.Vreme;

public abstract class Filter {

	public static Predicate<Cas> string(String izraz, Function<Cas, String> funkcija) throws IllegalArgumentException {
		Proveri.argument(izraz != null, "izraz", izraz);
		Proveri.argument(funkcija != null, "funkcija", funkcija);
		if (izraz.startsWith("=")) {
			return stringJednak(izraz.substring(1), funkcija);
		} else {
			return stringSadrzi(izraz, funkcija);
		}
	}

	public static Predicate<Cas> stringJednak(String izraz, Function<Cas, String> funkcija) throws IllegalArgumentException {
		Proveri.argument(izraz != null, "izraz", izraz);
		Proveri.argument(funkcija != null, "funkcija", funkcija);
		return (Cas cas) -> {
			String vrednost = funkcija.apply(cas);
			if (vrednost == null) {
				return false;
			}
			return vrednost.equalsIgnoreCase(izraz);
		};
	}

	public static Predicate<Cas> stringSadrzi(String izraz, Function<Cas, String> funkcija) throws IllegalArgumentException {
		Proveri.argument(izraz != null, "izraz", izraz);
		Proveri.argument(funkcija != null, "funkcija", funkcija);
		return (Cas cas) -> {
			String vrednost = funkcija.apply(cas);
			if (vrednost == null) {
				return false;
			}
			return vrednost.toUpperCase().contains(izraz.toUpperCase());
		};
	}

	public static Predicate<Cas> skupStringova(String izraz, Function<Cas, Set<String>> funkcija) throws IllegalArgumentException {
		Proveri.argument(izraz != null, "izraz", izraz);
		Proveri.argument(funkcija != null, "funkcija", funkcija);
		if (izraz.startsWith("=")) {
			return skupStringovaJednak(izraz.substring(1), funkcija);
		} else {
			return skupStringSadrzi(izraz, funkcija);
		}
	}

	public static Predicate<Cas> skupStringovaJednak(String izraz, Function<Cas, Set<String>> funkcija) throws IllegalArgumentException {
		Proveri.argument(izraz != null, "izraz", izraz);
		Proveri.argument(funkcija != null, "funkcija", funkcija);
		return (Cas cas) -> {
			Set<String> vrednost = funkcija.apply(cas);
			if (vrednost == null) {
				return false;
			}
			for (String v : vrednost) {
				if (v.equalsIgnoreCase(izraz)) {
					return true;
				}
			}
			return false;
		};
	}

	public static Predicate<Cas> skupStringSadrzi(String izraz, Function<Cas, Set<String>> funkcija) throws IllegalArgumentException {
		Proveri.argument(izraz != null, "izraz", izraz);
		Proveri.argument(funkcija != null, "funkcija", funkcija);
		return (Cas cas) -> {
			Set<String> vrednost = funkcija.apply(cas);
			if (vrednost == null) {
				return false;
			}
			for (String v : vrednost) {
				if (v.toUpperCase().contains(izraz.toUpperCase())) {
					return true;
				}
			}
			return false;
		};
	}

	public static Predicate<Cas> semestar(GodinaISemestar izraz, Function<Cas, GodinaISemestar> funkcija) throws IllegalArgumentException {
		Proveri.argument(funkcija != null, "funkcija", funkcija);
		return (Cas cas) -> {
			if (izraz == null) {
				return true;
			}
			GodinaISemestar vrednost = funkcija.apply(cas);
			if (vrednost == null) {
				return false;
			}
			return vrednost.equals(izraz);
		};
	}

	public static Predicate<Cas> datum(String izraz, Function<Cas, LocalDateTime> funkcija) throws IllegalArgumentException {
		Proveri.argument(izraz != null, "izraz", izraz);
		Proveri.argument(izraz.startsWith("<") || izraz.startsWith(">"), "izraz", izraz);
		Proveri.argument(funkcija != null, "funkcija", funkcija);
		int j = izraz.length() - 1;
		int kolicina = Integer.parseInt(izraz.substring(1, j));
		char c = Character.toLowerCase(izraz.charAt(j));
		TemporalUnit jedinica;
		if (c == 's' || c == 'h') {
			jedinica = ChronoUnit.HOURS;
		} else if (c == 'd') {
			jedinica = ChronoUnit.DAYS;
		} else if (c == 'n' || c == 'w') {
			jedinica = ChronoUnit.WEEKS;
		} else if (c == 'm') {
			jedinica = ChronoUnit.MONTHS;
		} else {
			throw Proveri.argument("izraz", izraz);
		}
		LocalDateTime granica = LocalDateTime.now().minus(kolicina, jedinica);
		if (izraz.startsWith("<")) {
			return datumPosle(granica, funkcija);
		} else {
			return datumPre(granica, funkcija);
		}
	}

	public static Predicate<Cas> datumPre(LocalDateTime granica, Function<Cas, LocalDateTime> funkcija) throws IllegalArgumentException {
		Proveri.argument(granica != null, "granica", granica);
		Proveri.argument(funkcija != null, "funkcija", funkcija);
		return (Cas cas) -> {
			LocalDateTime vrednost = funkcija.apply(cas);
			if (vrednost == null) {
				return false;
			}
			return vrednost.isBefore(granica);
		};
	}

	public static Predicate<Cas> datumPosle(LocalDateTime granica, Function<Cas, LocalDateTime> funkcija) throws IllegalArgumentException {
		Proveri.argument(granica != null, "granica", granica);
		Proveri.argument(funkcija != null, "funkcija", funkcija);
		return (Cas cas) -> {
			LocalDateTime vrednost = funkcija.apply(cas);
			if (vrednost == null) {
				return false;
			}
			return vrednost.isAfter(granica);
		};
	}

	protected static final Pattern mustraZaTermin = Pattern.compile("(?i)(od|do)(<|<=|>|>=|=|<>)(\\d\\d)[.:-](\\d\\d)");

	public static Predicate<Cas> termin(String izraz) throws IllegalArgumentException {
		Matcher m = mustraZaTermin.matcher(izraz);
		Proveri.argument(m.matches(), "izraz", izraz);
		try {
			int sat = Integer.parseInt(m.group(3));
			int minut = Integer.parseInt(m.group(4));
			Vreme granica = new Vreme(sat, minut);
			String relacija = m.group(2);
			IntPredicate poredjenje = "<".equals(relacija) ? x -> x <  0 :
			                         "<=".equals(relacija) ? x -> x <= 0 :
			                          ">".equals(relacija) ? x -> x >  0 :
			                         ">=".equals(relacija) ? x -> x >= 0 :
			                          "=".equals(relacija) ? x -> x == 0 :
			                                                 x -> x == 0;
			if ("od".equals(m.group(1))) {
				return termin(granica, Cas::getVremeOd, poredjenje);
			} else {
				return termin(granica, Cas::getVremeDo, poredjenje);
			}
		} catch (IllegalArgumentException e) {
			throw Proveri.argument("izraz", izraz, e);
		}
	}

	public static Predicate<Cas> termin(Vreme granica, Function<Cas, Vreme> funkcija, IntPredicate poredjenje) throws IllegalArgumentException {
		Proveri.argument(granica != null, "granica", granica);
		Proveri.argument(funkcija != null, "funkcija", funkcija);
		Proveri.argument(poredjenje != null, "poredjenje", poredjenje);
		return (Cas cas) -> {
			Vreme vrednost = funkcija.apply(cas);
			if (vrednost == null) {
				return false;
			}
			return poredjenje.test(vrednost.compareTo(granica));
		};
	}

	public static Predicate<Cas> konjunkcija(Iterable<Predicate<Cas>> filteri) throws IllegalArgumentException {
		Proveri.argument(filteri != null, "filteri", filteri);
		List<Predicate<Cas>> listaFiltera = StreamSupport.stream(filteri.spliterator(), false)
				.filter(filter -> filter != null)
				.collect(Collectors.toList());
		return (Cas cas) -> {
			for (Predicate<Cas> filter : listaFiltera) {
				if (!filter.test(cas)) {
					return false;
				}
			}
			return true;
		};
	}

	public static Predicate<Cas> disjunkcija(Iterable<Predicate<Cas>> filteri) throws IllegalArgumentException {
		Proveri.argument(filteri != null, "filteri", filteri);
		List<Predicate<Cas>> listaFiltera = StreamSupport.stream(filteri.spliterator(), false)
				.filter(filter -> filter != null)
				.collect(Collectors.toList());
		return (Cas cas) -> {
			for (Predicate<Cas> filter : listaFiltera) {
				if (filter.test(cas)) {
					return true;
				}
			}
			return false;
		};
	}
}
