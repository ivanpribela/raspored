package org.svetovid.raspored.io;

import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.util.Set;

import org.svetovid.raspored.model.Cas;
import org.svetovid.raspored.model.Dan;
import org.svetovid.raspored.model.Termin;
import org.svetovid.raspored.model.Tip;
import org.svetovid.raspored.model.Vreme;
import org.svetovid.raspored.util.Proveri;

/**
 * Ova klasa sluzi za pravljenje objekata koji predstavljaju casove. U njoj se
 * polako skupljaju sve potrebne informacije dok se podaci ucitavaju iz fajla.
 * Moguce je napraviti samo uredno popunjen cas.
 *
 * @author Ivan Pribela
 */
public final class GraditeljCasova {

	private String studenti;
	private Dan dan;
	private Vreme vremeOd;
	private Vreme vremeDo;
	private String predmet;
	private String nastavnik;
	private Set<Tip> tipovi;
	private String sala;
	private String id;
	private LocalDateTime datumIzmene;

	public String getStudenti() {
		return studenti;
	}

	public GraditeljCasova studenti(String novaVrednost) {
		studenti = novaVrednost;
		return this;
	}

	public Dan getDan() {
		return dan;
	}

	public GraditeljCasova dan(String novaVrednost) throws IllegalArgumentException {
		if (novaVrednost == null) {
			dan = null;
		} else {
			dan = Dan.pretvoriEngleskuDvoslovnuOznaku(novaVrednost);
		}
		return this;
	}

	public Vreme getVremeOd() {
		return vremeOd;
	}

	public GraditeljCasova vremeOd(String novaVrednost) throws IllegalArgumentException {
		if (novaVrednost == null) {
			vremeOd = null;
		} else {
			vremeOd = Vreme.pretvoriISO(novaVrednost);
		}
		return this;
	}

	public Vreme getVremeDo() {
		return vremeDo;
	}

	public GraditeljCasova vremeDo(String novaVrednost) throws IllegalArgumentException {
		if (novaVrednost == null) {
			vremeDo = null;
		} else {
			vremeDo = Vreme.pretvoriISO(novaVrednost);
		}
		return this;
	}

	public String getPredmet() {
		return predmet;
	}

	public GraditeljCasova predmet(String novaVrednost) {
		predmet = novaVrednost;
		return this;
	}

	public String getNastavnik() {
		return nastavnik;
	}

	public GraditeljCasova nastavnik(String novaVrednost) {
		nastavnik = novaVrednost;
		return this;
	}

	public Set<Tip> getTipovi() {
		return tipovi;
	}

	public GraditeljCasova tipovi(String novaVrednost) throws IllegalArgumentException {
		if (novaVrednost == null) {
			tipovi = null;
		} else {
			tipovi = Tip.pretvoriIzOznaka(novaVrednost);
		}
		return this;
	}

	public String getSala() {
		return sala;
	}

	public GraditeljCasova sala(String novaVrednost) {
		sala = novaVrednost;
		return this;
	}

	public String getId() {
		return id;
	}

	public GraditeljCasova id(String novaVrednost) {
		id = novaVrednost;
		return this;
	}

	public LocalDateTime datumIzmene() {
		return datumIzmene;
	}

	public GraditeljCasova setDatumIzmene(String novaVrednost) throws IllegalArgumentException {
		if (novaVrednost == null) {
			datumIzmene = null;
			return this;
		}
		try {
			int godina = Integer.parseInt(novaVrednost.substring(0, 4));
			int mesec = Integer.parseInt(novaVrednost.substring(4, 6));
			int dan = Integer.parseInt(novaVrednost.substring(6, 8));
			int sat = Integer.parseInt(novaVrednost.substring(9, 11));
			int minut = Integer.parseInt(novaVrednost.substring(11, 13));
			int sekunda = Integer.parseInt(novaVrednost.substring(13, 15));
			datumIzmene = LocalDateTime.of(godina, mesec, dan, sat, minut, sekunda);
			return this;
		} catch (NumberFormatException | DateTimeException e) {
			throw Proveri.argument("datumIzmene", novaVrednost);
		}
	}

	public Cas napravi() throws IllegalStateException {
		if (studenti == null) {
			throw new IllegalStateException("studenti");
		}
		if (dan == null) {
			throw new IllegalStateException("dan");
		}
		if (vremeOd == null) {
			throw new IllegalStateException("vremeOd");
		}
		if (vremeDo == null) {
			throw new IllegalStateException("vremeDo");
		}
		if (predmet == null) {
			throw new IllegalStateException("predmet");
		}
		if (nastavnik == null) {
			throw new IllegalStateException("nastavnik");
		}
		if (tipovi == null) {
			throw new IllegalStateException("tipovi");
		}
		if (sala == null) {
			throw new IllegalStateException("sala");
		}
		if (id == null) {
			throw new IllegalStateException("id");
		}
		if (datumIzmene == null) {
			throw new IllegalStateException("datumIzmene");
		}
		Termin termin = new Termin(dan, vremeOd, vremeDo);
		return new Cas(Set.of(studenti), termin, predmet, nastavnik, tipovi, sala, id, datumIzmene);
	}
}
