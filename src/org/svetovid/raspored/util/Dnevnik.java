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

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.lang.StackWalker.Option;
import java.lang.StackWalker.StackFrame;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.IllegalFormatException;
import java.util.Optional;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.StreamHandler;

/**
 * Pomocna klasa koja sluzi za zapisivanje poruka o graskama i drugim stvarima.
 *
 * @author Ivan Pribela
 */
public class Dnevnik {

	public static void trag3(String message, Object... arguments) {
		zapisi(Level.FINEST, message, arguments);
	}

	public static void trag3(String message, Throwable thrown, Object... arguments) {
		zapisi(Level.FINEST, message, thrown, arguments);
	}

	public static void trag2(String message, Object... arguments) {
		zapisi(Level.FINER, message, arguments);
	}

	public static void trag2(String message, Throwable thrown, Object... arguments) {
		zapisi(Level.FINER, message, thrown, arguments);
	}

	public static void trag(String message, Object... arguments) {
		zapisi(Level.FINE, message, arguments);
	}

	public static void trag(String message, Throwable thrown, Object... arguments) {
		zapisi(Level.FINE, message, thrown, arguments);
	}

	public static void konfig(String message, Object... arguments) {
		zapisi(Level.CONFIG, message, arguments);
	}

	public static void konfig(String message, Throwable thrown, Object... arguments) {
		zapisi(Level.CONFIG, message, thrown, arguments);
	}

	public static void info(String message, Object... arguments) {
		zapisi(Level.INFO, message, arguments);
	}

	public static void info(String message, Throwable thrown, Object... arguments) {
		zapisi(Level.INFO, message, thrown, arguments);
	}

	public static void upozorenje(String message, Object... arguments) {
		zapisi(Level.WARNING, message, arguments);
	}

	public static void upozorenje(String message, Throwable thrown, Object... arguments) {
		zapisi(Level.WARNING, message, thrown, arguments);
	}

	public static void greska(String message, Object... arguments) {
		zapisi(Level.SEVERE, message, arguments);
	}

	public static void greska(String message, Throwable thrown, Object... arguments) {
		zapisi(Level.SEVERE, message, thrown, arguments);
	}

	protected static void zapisi(Level level, String message, Object... arguments) {
		zapisi(level, message, null, arguments);
	}

	protected static void zapisi(Level level, String message, Throwable thrown, Object... arguments) {
		try {
			message = String.format(message, arguments);
		} catch (IllegalFormatException e) {
			// Ne diramo poruku, neka ostane u originalu
		}
		Logger logger = findLogger();
		if (thrown == null) {
			logger.log(level, message);
		} else {
			logger.log(level, message, thrown);
		}
	}

	protected static Logger findLogger() {
		return Logger.getLogger(findCallerClass());
	}

	protected static String findCallerClass() {
		StackWalker walker = StackWalker.getInstance(Option.RETAIN_CLASS_REFERENCE);
		Optional<String> callerClassName = walker.walk(stack -> stack
				.map(StackFrame::getDeclaringClass)
				.filter(type -> type != Dnevnik.class)
				.map(Class::getName)
				.findFirst()
		);
		return callerClassName.orElse("");
	}

	public static void podesi(Path folder) {

		// Glavni dnevnik
		Logger logger = Logger.getLogger("");
		logger.setLevel(Level.ALL);
		logger.setFilter(null);
		for (Handler handler : logger.getHandlers()) {
			logger.removeHandler(handler);
		}

		// Ispis na konzolu
		DnevnikConsoleHandler consoleHandler = new DnevnikConsoleHandler();
		consoleHandler.setLevel(Level.INFO);
		logger.addHandler(consoleHandler);

		// Ispis u fajl
		if (folder != null) {
			Path fajl = folder.resolve(dateTimeFormatter.format(Instant.now()) + ".log");
			try {
				Files.createDirectories(folder);
				DnevnikFileHandler fileHandler = new DnevnikFileHandler(fajl);
				fileHandler.setFormatter(new DnevnikFormatter());
				fileHandler.setLevel(Level.ALL);
				logger.addHandler(fileHandler);
			} catch (IOException e) {
				greska("Nije moguce upisivati dnevnik u fajl \"%s\"", e, fajl); 
			}
		}

	}

	protected static DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("uuuuMMdd-HHmmss").withZone(ZoneId.systemDefault());
	protected static DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss").withZone(ZoneId.systemDefault());

	protected static class DnevnikConsoleHandler extends StreamHandler {

		public DnevnikConsoleHandler() {
			super(System.err, new DnevnikFormatter());
			try {
				setEncoding("UTF-8");
			} catch (UnsupportedEncodingException e) {
				// Nista, probali smo
			}
		}

		@Override
		public void publish(LogRecord record) {
			super.publish(record);
			flush();
		}

		@Override
		public void close() {
			flush();
		}
	}

	protected static class DnevnikFileHandler extends StreamHandler {

		public DnevnikFileHandler(Path path) throws IOException {
			super(Files.newOutputStream(path, StandardOpenOption.CREATE), new DnevnikFormatter());
			try {
				setEncoding("UTF-8");
			} catch (UnsupportedEncodingException e) {
				// Nista, probali smo
			}
		}

		@Override
		public void publish(LogRecord record) {
			super.publish(record);
			flush();
		}
	}

	protected static class DnevnikFormatter extends Formatter {

		@Override
		public String format(LogRecord logRecord) {
			if (logRecord.getThrown() == null) {
				return String.format("%s | %s | %s%n", formatTimestamp(logRecord.getInstant()), formatLevel(logRecord.getLevel()), logRecord.getMessage());
			} else {
				return String.format("%s | %s | %s%n%s", formatTimestamp(logRecord.getInstant()), formatLevel(logRecord.getLevel()), logRecord.getMessage(), formatThrown(logRecord.getThrown()));
			}
		}

		private String formatTimestamp(Instant timestamp) {
			return timeFormatter.format(timestamp);
		}

		private String formatLevel(Level level) {
			if (level.intValue()  >= Level.SEVERE.intValue()) {
				return "  GRESKA  ";
			}
			if (level.intValue()  >= Level.WARNING.intValue()) {
				return "UPOZORENJE";
			}
			if (level.intValue()  >= Level.INFO.intValue()) {
				return "   INFO   ";
			}
			if (level.intValue()  >= Level.CONFIG.intValue()) {
				return "  KONFIG  ";
			}
			if (level.intValue()  >= Level.FINE.intValue()) {
				return "   TRAG   ";
			}
			if (level.intValue()  >= Level.FINER.intValue()) {
				return "  TRAG 2  ";
			}
			if (level.intValue()  >= Level.FINEST.intValue()) {
				return "  TRAG 3  ";
			}
			return "?  ?  ?  ?";
		}

		private String formatThrown(Throwable thrown) {
			StringWriter writer = new StringWriter();
			thrown.printStackTrace(new PrintWriter(writer));
			return writer.toString();
		}
	}
}
