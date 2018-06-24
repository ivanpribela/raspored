package org.svetovid.raspored.util;

import java.util.logging.Level;

public enum Nivo {

	SVE("sve", Level.ALL),
	TRAG3("trag3", Level.FINEST),
	TRAG2("trag2", Level.FINER),
	TRAG("trag", Level.FINE),
	KONFIGURACIJA("konfig", Level.CONFIG),
	INFORMACIJA("info", Level.INFO),
	UPOZORENJE("upozorenja", Level.WARNING),
	GRESKA("greske", Level.SEVERE),  
	NISTA("nista", Level.OFF);

	private final String oznaka;
	private final Level nivo;

	private Nivo(String oznaka, Level nivo) {
		this.oznaka = oznaka;
		this.nivo = nivo;
	}

	public String getOznaka() {
		return oznaka;
	}

	public Level getNivo() {
		return nivo;
	}

	public static Nivo pretvoriIzOznake(String oznaka) throws IllegalArgumentException {
		for (Nivo nivo : values()) {
			if (nivo.oznaka.equalsIgnoreCase(oznaka)) {
				return nivo;
			}
		}
		throw Proveri.argument("oznaka", oznaka);
	}
}
