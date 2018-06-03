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

package org.svetovid.raspored.io;

import java.nio.file.Path;

import org.svetovid.raspored.model.Raspored;
import org.svetovid.raspored.util.Dnevnik;
import org.svetovid.raspored.util.Proveri;

public class Menadzer {

	private ListaKalendara kalendari;
	private Normalizatori normalizatori;

	public Menadzer(Path folder) {
		Proveri.argument(folder != null, "folder", folder);
		Dnevnik.podesi(folder.resolve("Dnevnik"));
		kalendari = new ListaKalendara(folder.resolve("Kalendari"));
		normalizatori = new Normalizatori(folder.resolve("Normalizatori"));
	}

	public Raspored getRaspored() {
		Raspored raspored = new Raspored();
		kalendari.preuzmi();
		Parser parser = new Parser();
		kalendari.parsiraj(parser).stream()
				.map(normalizatori::normalizuj)
				.forEach(raspored::dodaj);
		return raspored;
	}
}
