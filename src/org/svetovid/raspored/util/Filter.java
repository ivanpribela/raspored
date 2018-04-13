package org.svetovid.raspored.util;

import java.util.function.Function;
import java.util.function.Predicate;

import org.svetovid.raspored.model.Cas;

public abstract class Filter {

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
}
