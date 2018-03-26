package org.svetovid.raspored.util;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.lang.StackWalker.Option;
import java.lang.StackWalker.StackFrame;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.logging.Formatter;
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
		message = String.format(message, arguments);
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
