package lto.manager.common.database.tables.records;

import java.util.ArrayList;
import java.util.List;

public class RecordOptions {
	private int index;
	private OptionsDataType dataType;
	private String value;

	public static enum OptionsDataType {
		Boolean, String, Integer
	}

	public static enum OptionsSetting {
		LOG_REQUESTS("Log Requests"),
		LOG_REQUESTS_ASSETS("Log Asset Requests"),
		LOG_EXTERNAL_PROCESS("Log External Process std out/err");

		private final String text;
		OptionsSetting(final String text) { this.text = text; }
	    @Override
	    public String toString() { return text; }
	    public static OptionsSetting valueOf(int index) {
	    	if (index < OptionsSetting.values().length) {
	    		return OptionsSetting.values()[index];
	    	}
	    	return null;
	    }
	}

	public RecordOptions(int index, OptionsDataType dataType, String value) {
		this.index = index;
		this.dataType = dataType;
		this.value = value;
	}

	public static RecordOptions of(int index, OptionsDataType dataType, String value) {
		return new RecordOptions(index, dataType, value);
	}

	public static RecordOptions of(int index, String dataType, String value) {
		return new RecordOptions(index, OptionsDataType.valueOf(dataType), value);
	}

	private static RecordOptions of(int index, String value) {
		return new RecordOptions(index, null, value);
	}

	public static List<RecordOptions> ofBatch(List<String> query) {
		List<RecordOptions> list = new ArrayList<RecordOptions>();
		for (int i = 0; i < query.size(); i++) {
			list.add(RecordOptions.of(i, query.get(i)));
		}
		return list;
	}

	public int getIndex() { return index; }
	public OptionsDataType getDataType() { return dataType; }
	public String getValueAsString() { return value; }
	public boolean getValueAsBool() { return Boolean.parseBoolean(value); }
	public Integer getValueAsInteger() { return Integer.parseInt(value); }
}
