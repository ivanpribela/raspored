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

package org.svetovid.raspored.model;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.svetovid.raspored.util.Proveri;

/**
 * Ova klasa predstavlja jedan cas.
 *
 * @author Ivan Pribela
 */
public final class Cas {

	private final GodinaISemestar semestar;
	private final SortedSet<String> studenti;
	private final Termin termin;
	private final String predmet;
	private final String nastavnik;
	private final Set<Tip> tipovi;
	private final String sala;
	private final String id;
	private final LocalDateTime datumIzmene;

	public Cas(GodinaISemestar semestar, Set<String> studenti, Termin termin, String predmet, String nastavnik, Set<Tip> tipovi, String sala, String id, LocalDateTime datumIzmene) throws IllegalArgumentException {
		Proveri.argument(semestar != null, "semestar", semestar);
		Proveri.argument(studenti != null, "studenti", studenti);
		Proveri.elemente(x -> x != null, "studenti", studenti);
		Proveri.argument(termin != null, "termin", termin);
		Proveri.argument(predmet != null, "predmet", predmet);
		Proveri.argument(nastavnik != null, "nastavnik", nastavnik);
		Proveri.argument(tipovi != null, "tipovi", tipovi);
		Proveri.elemente(x -> x != null, "tipovi", tipovi);
		Proveri.argument(sala != null, "sala", sala);
		Proveri.argument(id != null, "id", id);
		Proveri.argument(datumIzmene != null, "datumIzmene", datumIzmene);
		this.semestar = semestar;
		this.studenti = Collections.unmodifiableSortedSet(new TreeSet<>(studenti));
		this.termin = termin;
		this.predmet = predmet;
		this.nastavnik = nastavnik;
		this.tipovi = Collections.unmodifiableSet(EnumSet.copyOf(tipovi));
		this.sala = sala;
		this.id = id;
		this.datumIzmene = datumIzmene;
	}

	public GodinaISemestar getSemestar() {
		return semestar;
	}

	public Set<String> getStudenti() {
		return studenti;
	}

	public Termin getTermin() {
		return termin;
	}

	public Dan getDan() {
		return termin.getDan();
	}

	public Vreme getVremeOd() {
		return termin.getVremeOd();
	}

	public Vreme getVremeDo() {
		return termin.getVremeDo();
	}

	public String getPredmet() {
		return predmet;
	}

	public String getNastavnik() {
		return nastavnik;
	}

	public Set<Tip> getTipovi() {
		return tipovi;
	}

	public String getSala() {
		return sala;
	}

	public String getId() {
		return id;
	}

	public LocalDateTime getDatumIzmene() {
		return datumIzmene;
	}

	@Override
	public int hashCode() {
		return id.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		Cas that = (Cas) obj;
		if (!this.id.equals(that.id)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return id;
	}

	public Cas spojiSa(Cas that) throws IllegalArgumentException {
		Proveri.argument(this.semestar.equals(that.semestar), "cas.semestar", this.semestar + " != " + that.semestar);
		Proveri.argument(this.predmet.equals(that.predmet), "cas.predmet", this.predmet + " != " + that.predmet);
		Proveri.argument(this.nastavnik.equals(that.nastavnik), "cas.nastavnik", this.nastavnik + " != " + that.nastavnik);
		Proveri.argument(this.tipovi.equals(that.tipovi), "cas.tipovi", Tip.pretvoriUOznake(this.tipovi) + " != " + Tip.pretvoriUOznake(that.tipovi));
		Proveri.argument(this.sala.equals(that.sala), "cas.sala", this.sala + " != " + that.sala);
		Proveri.argument(this.id.equals(that.id), "cas.id", this.id + " != " + that.id);
		LocalDateTime poslenjiDatumIzmene = this.datumIzmene.isAfter(that.datumIzmene) ? this.datumIzmene : that.datumIzmene;
		Set<String> sviStudenti = new TreeSet<>();
		sviStudenti.addAll(this.studenti);
		sviStudenti.addAll(that.studenti);
		return new Cas(semestar, sviStudenti, termin, predmet, nastavnik, tipovi, sala, id, poslenjiDatumIzmene);
	}
}
