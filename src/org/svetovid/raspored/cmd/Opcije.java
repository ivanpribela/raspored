package org.svetovid.raspored.cmd;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import org.svetovid.raspored.model.Cas;

public class Opcije {

	public Opcije() {
		filteri = new LinkedHashMap<>();
	}

	////////////
	// Opcije //
	////////////

	// TODO Dodati cuvanje standardnih opcija

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
