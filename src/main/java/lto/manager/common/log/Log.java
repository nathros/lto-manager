package lto.manager.common.log;

import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class Log {
	private static Logger l = getLogger();
	private static Integer STACK_INDEX = 2;
	public static Integer STACK_INDEX_PARENT = STACK_INDEX + 1;

	// Logs to console and file
	private static Logger getLogger() {
		System.setProperty("java.util.logging.SimpleFormatter.format", "%1$tY-%1$tm-%1$td %1$tH:%1$tM:%1$tS %4$-6s %2$s | %5$s%6$s%n");
		Logger logger = Logger.getLogger("log");
		FileHandler fh;
		final Level level = Level.ALL;
		try {
			fh = new FileHandler("config/logfile.log", 1000000, 3, true);
			fh.setLevel(level);
			logger.addHandler(fh);
			logger.setLevel(level);
			SimpleFormatter formatter = new SimpleFormatter();
	        fh.setFormatter(formatter);
	        fh.setLevel(level);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return logger;
	}

	public static void info(final String message) {
		final StackTraceElement stackTrace = Thread.currentThread().getStackTrace()[STACK_INDEX];
		l.logp(Level.INFO, stackTrace.getClassName(), stackTrace.getMethodName(), message);
	}

	public static void fine(final String message) {
		final StackTraceElement stackTrace = Thread.currentThread().getStackTrace()[STACK_INDEX];
		l.logp(Level.FINE, stackTrace.getClassName(), stackTrace.getMethodName(), message);
	}

	public static void finer(final String message) {
		final StackTraceElement stackTrace = Thread.currentThread().getStackTrace()[STACK_INDEX];
		l.logp(Level.FINER, stackTrace.getClassName(), stackTrace.getMethodName(), message);
	}

	public static void warning(final String message) {
		final StackTraceElement stackTrace = Thread.currentThread().getStackTrace()[STACK_INDEX];
		l.logp(Level.WARNING, stackTrace.getClassName(), stackTrace.getMethodName(), message);
	}

	public static void severe(final String message) {
		final StackTraceElement stackTrace = Thread.currentThread().getStackTrace()[STACK_INDEX];
		l.logp(Level.SEVERE, stackTrace.getClassName(), stackTrace.getMethodName(), message);
	}

	public static void customStackIndexSevere(final String message, final int customStackIndex) {
		final StackTraceElement stackTrace = Thread.currentThread().getStackTrace()[customStackIndex];
		l.logp(Level.SEVERE, stackTrace.getClassName(), stackTrace.getMethodName(), message);
	}

	public static void log(final Level level, final String message, final Throwable throwable) {
		final StackTraceElement stackTrace = Thread.currentThread().getStackTrace()[STACK_INDEX];
		l.logp(level, stackTrace.getClassName(), stackTrace.getMethodName(), message, throwable);
	}
}
