package lto.manager.common.database;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

import lto.manager.common.database.tables.records.RecordOptions;
import lto.manager.common.database.tables.records.RecordOptions.OptionsSetting;
import lto.manager.common.log.Log;

public class Options {
	public static HashMap<OptionsSetting, Object> defaultValues = getDefaultValues();
	private static HashMap<OptionsSetting, Object> cache = getCache();

	private static HashMap<OptionsSetting, Object> getDefaultValues() {
		var def = new HashMap<OptionsSetting, Object>();
		def.put(OptionsSetting.LOG_REQUESTS, Boolean.TRUE);
		def.put(OptionsSetting.LOG_REQUESTS_ASSETS, Boolean.FALSE);
		def.put(OptionsSetting.LOG_EXCEPTION_STACKTRACE, Boolean.FALSE);
		def.put(OptionsSetting.PLACEHOLDER_2, Integer.MIN_VALUE);
		def.put(OptionsSetting.PLACEHOLDER_3, Integer.MIN_VALUE);
		def.put(OptionsSetting.PLACEHOLDER_4, Integer.MIN_VALUE);
		def.put(OptionsSetting.PLACEHOLDER_5, Integer.MIN_VALUE);
		def.put(OptionsSetting.PLACEHOLDER_6, Integer.MIN_VALUE);
		def.put(OptionsSetting.PLACEHOLDER_7, Integer.MIN_VALUE);
		def.put(OptionsSetting.LOG_EXTERNAL_PROCESS, Boolean.FALSE);
		def.put(OptionsSetting.BACKGROUND_CLEANUP_TIMER, Integer.valueOf(15));
		def.put(OptionsSetting.STALE_EXTERNAL_PROCESS_TIME, Integer.valueOf(15));
		def.put(OptionsSetting.PLACEHOLDER_C, Integer.MIN_VALUE);
		def.put(OptionsSetting.PLACEHOLDER_D, Integer.MIN_VALUE);
		def.put(OptionsSetting.PLACEHOLDER_E, Integer.MIN_VALUE);
		def.put(OptionsSetting.PLACEHOLDER_F, Integer.MIN_VALUE);
		def.put(OptionsSetting.PLACEHOLDER_H, Integer.MIN_VALUE);
		def.put(OptionsSetting.PLACEHOLDER_J, Integer.MIN_VALUE);
		def.put(OptionsSetting.PLACEHOLDER_K, Integer.MIN_VALUE);
		def.put(OptionsSetting.PLACEHOLDER_L, Integer.MIN_VALUE);
		def.put(OptionsSetting.PLACEHOLDER_M, Integer.MIN_VALUE);
		def.put(OptionsSetting.ENABLE_LOGIN, Boolean.TRUE);
		def.put(OptionsSetting.ENABLE_ASSET_CACHE, Boolean.TRUE);
		def.put(OptionsSetting.PLACEHOLDER_O, Integer.MIN_VALUE);
		def.put(OptionsSetting.PLACEHOLDER_P, Integer.MIN_VALUE);
		def.put(OptionsSetting.PLACEHOLDER_Q, Integer.MIN_VALUE);
		def.put(OptionsSetting.PLACEHOLDER_R, Integer.MIN_VALUE);
		def.put(OptionsSetting.PLACEHOLDER_S, Integer.MIN_VALUE);
		def.put(OptionsSetting.PLACEHOLDER_T, Integer.MIN_VALUE);
		def.put(OptionsSetting.PLACEHOLDER_U, Integer.MIN_VALUE);
		def.put(OptionsSetting.PLACEHOLDER_V, Integer.MIN_VALUE);
		def.put(OptionsSetting.PLACEHOLDER_W, Integer.MIN_VALUE);
		def.put(OptionsSetting.PLACEHOLDER_X, Integer.MIN_VALUE);
		def.put(OptionsSetting.PLACEHOLDER_Y, Integer.MIN_VALUE);
		def.put(OptionsSetting.PLACEHOLDER_Z, Integer.MIN_VALUE);
		return def;
	}

	private static HashMap<OptionsSetting, Object> getCache() {
		var ret = new HashMap<OptionsSetting, Object>();
		try {
			List<RecordOptions> all = Database.getAllOptions();
			for (RecordOptions item : all) {
				OptionsSetting os = OptionsSetting.valueOf(item.getIndex());
				switch (item.getDataType()) {
				case Boolean: {
					ret.put(os, item.getValueAsBool());
					break;
				}
				case String: {
					ret.put(os, item.getValueAsString());
					break;
				}
				case Integer: {
					ret.put(os, item.getValueAsInteger());
					break;
				}
				}
			}
		} catch (SQLException e) {
			Log.severe("Failed to get options cache");
		}

		return ret;
	}

	public static void refreshCache() {
		cache = getCache();
	}

	public static <T> T getData(OptionsSetting setting) {
		@SuppressWarnings("unchecked")
		T opt = (T) cache.get(setting);
		return opt;
	}

	public static <T> void setData(OptionsSetting setting, T value) {
		try {
			var defaultOption = defaultValues.get(setting);
			if (defaultOption.getClass().equals(value.getClass())) {
				Database.updateOption(setting, String.valueOf(value));
				cache.put(setting, value);
			} else {
				Log.severe("Attempt to set user option " + setting.toString() + " which is "
						+ defaultOption.getClass().getTypeName() + " as " + value.getClass().getTypeName());
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static void setBatch(List<RecordOptions> batch) {
		for (RecordOptions item : batch) {
			OptionsSetting setting = OptionsSetting.valueOf(item.getIndex());
			Class<? extends Object> clazz = defaultValues.get(setting).getClass();
			if (clazz.equals(Boolean.class)) {
				setData(setting, item.getValueAsBool());
			} else if (clazz.equals(String.class)) {
				setData(setting, item.getValueAsString());
			} else if (clazz.equals(Integer.class)) {
				setData(setting, item.getValueAsInteger());
			}
		}
	}

	public static void resetToDefault() {
		try {
			Database.resetOptions();
			refreshCache();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
