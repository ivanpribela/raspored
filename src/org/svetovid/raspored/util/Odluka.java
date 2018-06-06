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

package org.svetovid.raspored.util;

import java.util.LinkedHashSet;
import java.util.Set;

public enum Odluka {

	NIKAD("nikad", "n") {
		@Override
		public boolean odluci(boolean uslov) {
			return false;
		}
	},
	
	PO_POTREBI("po-potrebi", "po potrebi", "popotrebi", "p") {
		@Override
		public boolean odluci(boolean uslov) {
			return uslov;
		}
	},
	
	UVEK("uvek", "u") {
		@Override
		public boolean odluci(boolean uslov) {
			return true;
		}
	};

	private final Set<String> oznake = new LinkedHashSet<>();

	private Odluka(String... oznake) {
		for (String oznaka : oznake) {
			this.oznake.add(oznaka);
		}
	}

	public Set<String> getOznake() {
		return oznake;
	}

	public abstract boolean odluci(boolean uslov);

	public static Odluka pretvoriIzOznaka(String oznaka) throws IllegalArgumentException {
		for (Odluka odluka : values()) {
			for (String o: odluka.oznake) {
				if (o.equalsIgnoreCase(oznaka)) {
					return odluka;
				}
			}
		}
		throw Proveri.argument("oznaka", oznaka);
	}
}
