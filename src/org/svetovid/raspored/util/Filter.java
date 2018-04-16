package org.svetovid.raspored.util;

import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;

import org.svetovid.raspored.model.Cas;

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
}
