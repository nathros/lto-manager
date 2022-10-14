package lto.manager.web;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map.Entry;

import lto.manager.common.database.Database;
import lto.manager.common.database.tables.TableOptions;

public class Options {
	public static HashMap<Integer, String> defaultValues = getDefault();
	private static HashMap<Integer, String> values = new HashMap<Integer, String>();

	private static HashMap<Integer, String> getDefault() {
		HashMap<Integer, String> map = new HashMap<Integer, String>();
		map.put(TableOptions.INDEX_ENABLE_LOG_REQUESTS, "true");
		map.put(TableOptions.INDEX_ENABLE_LOG_EXTERNAL_PROCESS, "false");
		return map;
	}

	public static void writeAll() throws SQLException {
		for (Entry<Integer, String> entry : Options.defaultValues.entrySet()) {
			Integer key = entry.getKey();
		    String value = values.get(key);
			Database.updateOption(key, value);
		}
	}

	public static void readAll() {
		HashMap<Integer, String> map = new HashMap<Integer, String>();
		for (Entry<Integer, String> entry : Options.defaultValues.entrySet()) {
			Integer key = entry.getKey();
		    try {
				String value = Database.getOption(key);
				if (null == value) value = defaultValues.get(key);
				map.put(key, value);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		values = map;
	}

	public static boolean getBool(int index) {
		String tmp = values.get(index);
		if ("true".equals(tmp)) return true;
		else return false;
	}

	public static void setBool(int index, boolean value) {
		values.put(index, String.valueOf(value));
	}
}
