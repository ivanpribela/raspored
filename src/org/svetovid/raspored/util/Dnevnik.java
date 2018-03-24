package org.svetovid.raspored.util;

import java.lang.StackWalker.Option;
import java.lang.StackWalker.StackFrame;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

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
}
