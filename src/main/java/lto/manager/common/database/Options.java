package lto.manager.common.database;

import java.sql.SQLException;

import lto.manager.common.database.tables.TableOptions;

public class Options {
	public static String[] defaultValues = getDefaultValues();

	private static String[] getDefaultValues() {
		var def = new String[TableOptions.INDEX_MAX];
		def[TableOptions.INDEX_ENABLE_LOG_REQUESTS] = String.valueOf(true);
		def[TableOptions.INDEX_ENABLE_LOG_EXTERNAL_PROCESS] = String.valueOf(true);
		return def;
	}

	public static boolean getBool(int index) {
		try {
			var str = Database.getOption(index);
			return Boolean.parseBoolean(str);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public static String getStr(int index) {
		try {
			return Database.getOption(index);
		} catch (SQLException e) {
			e.printStackTrace();
			return "";
		}
	}

	public static void setBool(int index, boolean value) {
		try {
			Database.updateOption(index, String.valueOf(value));
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
