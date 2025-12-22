package lto.manager.common.log;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.logging.StreamHandler;

import lto.manager.web.handlers.Handlers;
import lto.manager.web.handlers.websockets.admin.LoggingWebsocketHandler;

public class Log {
	public static enum LogFile {
		Main, Requests
	}

	private static List<String> logPaths = Arrays.asList("config/logfile.log", "config/requests.log");
	private static SimpleFormatter mainFormatter = getSimpleFormatter();
	private static Logger l = getMainLogger(LogFile.Main, mainFormatter);
	private static Logger request = getRequestLogger(LogFile.Requests, mainFormatter);

	private static Integer STACK_INDEX = 2;
	public static Integer STACK_INDEX_PARENT = STACK_INDEX + 1;

	private static SimpleFormatter getSimpleFormatter() {
		LogManager.getLogManager().reset();
		System.setProperty("java.util.logging.SimpleFormatter.format",
				"%1$tY-%1$tm-%1$td %1$tH:%1$tM:%1$tS.%1$tL %4$-6s %2$s | %5$s%6$s%n");
		return new SimpleFormatter();
	}

	// Logs to console and file
	private static Logger getMainLogger(LogFile file, SimpleFormatter formatter) {
		Logger logger = Logger.getLogger("log");
		final Level level = Level.ALL;
		try {
			logger.setLevel(level);
			logger.addHandler(createConsoleHandler(level, formatter));
			logger.addHandler(createFileHandler(level, logPaths.get(file.ordinal()), formatter));
			logger.addHandler(WebSocketHandler(level, formatter, file));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return logger;
	}

	private static Logger getRequestLogger(LogFile file, SimpleFormatter formatter) {
		Logger logger = Logger.getLogger("request");
		final Level level = Level.ALL;
		try {
			logger.setLevel(level);
			logger.addHandler(createFileHandler(level, logPaths.get(file.ordinal()), formatter));
			logger.addHandler(WebSocketHandler(level, formatter, file));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return logger;
	}

	private static ConsoleHandler createConsoleHandler(final Level level, SimpleFormatter formatter)
			throws SecurityException, IOException {
		ConsoleHandler consoleHandler = new ConsoleHandler();
		consoleHandler.setLevel(level);
		consoleHandler.setFormatter(formatter);
		return consoleHandler;
	}

	private static FileHandler createFileHandler(final Level level, final String filePath, SimpleFormatter formatter)
			throws SecurityException, IOException {
		FileHandler fileHandler = new FileHandler(filePath, 1024 * 1024 * 10, 8, true);
		fileHandler.setLevel(level);
		fileHandler.setFormatter(formatter);
		fileHandler.setLevel(level);
		return fileHandler;
	}

	private static WebSocketHandler WebSocketHandler(final Level level, SimpleFormatter formatter, LogFile file)
			throws SecurityException, IOException {
		WebSocketHandler webSocketHandler = new WebSocketHandler(file);
		webSocketHandler.setLevel(level);
		webSocketHandler.setFormatter(formatter);
		return webSocketHandler;
	}

	public static Handler[] getHandlers() {
		return l.getHandlers();
	}

	public static String getLogFilePath(LogFile file) {
		return logPaths.get(file.ordinal());
	}

	public static String generateLogMessageAsString(final Level level, final String message) {
		return mainFormatter.format(new LogRecord(level, message));
	}

	public static boolean changeLogFilePath(final String newPath) throws SecurityException, IOException {
		for (final var handler : l.getHandlers()) {
			if (handler instanceof FileHandler) {
				final Level level = handler.getLevel();
				final SimpleFormatter formatter = (SimpleFormatter) handler.getFormatter();
				l.removeHandler(handler);
				l.addHandler(createFileHandler(level, newPath, formatter));
				return true;
			}
		}
		return false;
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

	public static void logRequest(final String message) {
		request.info(message);
	}

	// This is used to send log messages to clients connected to the correct
	// WebSocket handler
	public static class WebSocketHandler extends StreamHandler {
		private final LogFile file;
		private final LoggingWebsocketHandler wsLogger = (LoggingWebsocketHandler) Handlers.websocketHandlers
				.get(LoggingWebsocketHandler.PATH);

		public WebSocketHandler(LogFile file) {
			super();
			this.file = file;
		}

		@Override
		public void publish(LogRecord record) {
			if (wsLogger.hasClient()) {
				wsLogger.publishNewMessage(getFormatter().format(record), file);
			}
		}
	}
}
