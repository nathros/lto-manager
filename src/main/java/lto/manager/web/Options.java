package lto.manager.web;

import java.sql.SQLException;

import lto.manager.common.database.Database;
import lto.manager.common.database.tables.TableOptions;

public class Options {
	public static boolean logRequests = true;

	public static void writeAll() throws SQLException {
		Database.updateOption(TableOptions.INDEX_ENABLE_LOGGING, String.valueOf(logRequests));
	}
}
