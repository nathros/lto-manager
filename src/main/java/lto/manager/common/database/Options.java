package lto.manager.common.database;

import java.sql.SQLException;
import java.util.HashMap;

import org.apache.commons.lang3.tuple.Pair;

import lto.manager.common.database.tables.records.RecordOptions.OptionsDataType;
import lto.manager.common.database.tables.records.RecordOptions.OptionsSetting;

public class Options {
	public static HashMap<OptionsSetting, Pair<OptionsDataType, String>> defaultValues = getDefaultValues();

	private static HashMap<OptionsSetting, Pair<OptionsDataType, String>> getDefaultValues() {
		var def = new HashMap<OptionsSetting, Pair<OptionsDataType, String>>();
		def.put(OptionsSetting.LOG_REQUESTS, 			Pair.of(OptionsDataType.Boolean, Boolean.toString(true)));
		def.put(OptionsSetting.LOG_REQUESTS_ASSETS, 	Pair.of(OptionsDataType.Boolean, Boolean.toString(false)));
		def.put(OptionsSetting.LOG_EXTERNAL_PROCESS, 	Pair.of(OptionsDataType.Boolean, Boolean.toString(false)));
		return def;
	}

	public static boolean getBool(OptionsSetting setting) {
		try {
			var str = Database.getOption(setting);
			return Boolean.parseBoolean(str);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public static String getStr(OptionsSetting setting) {
		try {
			return Database.getOption(setting);
		} catch (SQLException e) {
			e.printStackTrace();
			return "";
		}
	}

	public static void setBool(OptionsSetting setting, boolean value) {
		try {
			Database.updateOption(setting, String.valueOf(value));
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static void setStr(OptionsSetting setting, String value) {
		try {
			Database.updateOption(setting, value);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
