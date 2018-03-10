package org.svetovid.raspored.io;

import java.util.ArrayList;
import java.util.List;

import org.svetovid.raspored.model.Cas;

/**
 * Ova klasa sluzi za ucitavanje kalendara u iCalendar formatu.
 *
 * @author Ivan Pribela
 */
public final class Parser {

	public Parser() {
	}

	public List<Cas> parsiraj(String naziv, List<String> linije) {
		List<Cas> casovi = new ArrayList<>();
		for (String linija : linije) {
			// TODO Implementirati pomocu regularnih izraza
		}
		return casovi;
	}
}
