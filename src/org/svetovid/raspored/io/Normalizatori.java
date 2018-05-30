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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.svetovid.raspored.util.Dnevnik;
import org.svetovid.raspored.util.Proveri;

/**
 * Ova klasa sluzi za cuvanje pravila normalizacije podataka za sve osobine
 * casa.
 *
 * @author Ivan Pribela
 */
public final class Normalizatori {

	private final Path folder;

	public Normalizatori(Path folder) {
		Proveri.argument(folder != null, "folder", folder);
		this.folder = folder;
		napraviFolderAkoNePostoji(folder);
	}

	public Path getFolder() {
		return folder;
	}

	private boolean napraviFolderAkoNePostoji(Path folder) {
		try {
			Files.createDirectories(folder);
			return true;
		} catch (IOException e) {
			Dnevnik.upozorenje("Nije moguće napraviti folder za normalizatore: \"%s\"", e, folder);
			return false;
		}
	}
}
