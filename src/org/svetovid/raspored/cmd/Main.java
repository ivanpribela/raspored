package org.svetovid.raspored.cmd;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;

import org.svetovid.raspored.io.Parser;
import org.svetovid.raspored.model.Cas;
import org.svetovid.raspored.model.Dan;
import org.svetovid.raspored.model.Tip;

public class Main {

	public static void main(String[] arguments) throws IOException {
		Parser parser = new Parser();
		List<Cas> casovi = parser.parsiraj(arguments[0], Paths.get(arguments[1]));
		Collections.sort(casovi, (cas1, cas2) -> cas1.getTermin().compareTo(cas2.getTermin()));
		Dan dan = null;
		for (Cas cas : casovi) {
			if (dan != cas.getDan()) {
				System.out.println();
			}
			System.out.printf("%10s   %s %s-%s   %-50s %-8s %-30s %-15s %s   %s%n", cas.getStudenti(), cas.getDan().getOznaka(), cas.getVremeOd(), cas.getVremeDo(), cas.getPredmet(), Tip.pretvoriUOznake(cas.getTipovi()), cas.getNastavnik(), cas.getSala(), cas.getId(), cas.getDatumIzmene());
			dan = cas.getDan();
		}
	}
}
