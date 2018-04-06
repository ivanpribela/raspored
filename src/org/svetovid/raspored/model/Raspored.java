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
