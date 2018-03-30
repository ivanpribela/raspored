package org.svetovid.raspored.io;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Ova klasa sluzi za cuvanje pravila normalizacije podataka za jednu osobinu
 * casa.
 * 
 * @author Ivan Pribela
 */
public final class Normalizator {

	private final String ime;
	private final List<Pravilo> pravila = new ArrayList<>();

	public Normalizator(String ime) {
		this.ime = ime;
	}

	public Normalizator(String ime, Collection<Pravilo> pravila) {
		this(ime);
		this.pravila.addAll(pravila);
	}

	public Normalizator(String ime, Pravilo... pravila) {
		this(ime);
		for (Pravilo pravilo : pravila) {
			this.pravila.add(pravilo);
		}
	}

	public Normalizator(String ime, Iterable<Pravilo> pravila) {
		this(ime);
		for (Pravilo pravilo : pravila) {
			this.pravila.add(pravilo);
		}
	}

	public String getIme() {
		return ime;
	}

	public List<Pravilo> getPravila() {
		return pravila;
	}

	public static abstract class Pravilo {

		protected final String a;
		protected final String b;

		public Pravilo(String a, String b) {
			this.a = a;
			this.b = b;
		}

		public abstract String primeni(String string);

	}
}
