package lto.manager.common.log;

import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class Log {
	public static Logger l = getLogger();

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

}
