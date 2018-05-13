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

import java.util.Comparator;
import java.util.function.Function;

import org.svetovid.raspored.model.Cas;

public class Komparator {

	public static Comparator<Cas> string(Function<Cas, String> funkcija) {
		Proveri.argument(funkcija != null, "funkcija", funkcija);
		return Comparator.comparing(funkcija, String.CASE_INSENSITIVE_ORDER);
	}
}
