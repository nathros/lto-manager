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
		LOG_REQUESTS("Log requests"),
		LOG_REQUESTS_ASSETS("Log asset requests"),
		PLACEHOLDER_1("placeholder 1"),
		PLACEHOLDER_2("placeholder 2"),
		PLACEHOLDER_3("placeholder 3"),
		PLACEHOLDER_4("placeholder 4"),
		PLACEHOLDER_5("placeholder 5"),
		PLACEHOLDER_6("placeholder 6"),
		PLACEHOLDER_7("placeholder 7"),
		LOG_EXTERNAL_PROCESS("Log external process std out/err"),
		BACKGROUND_CLEANUP_TIMER("Frequency to initiate background cleanup (minutes)"),
		STALE_EXTERNAL_PROCESS_TIME("Time passed to deem completed external process stale and mark for removal (minutes)"),
		PLACEHOLDER_C("placeholder c"),
		PLACEHOLDER_D("placeholder d"),
		PLACEHOLDER_E("placeholder e"),
		PLACEHOLDER_F("placeholder f"),
		PLACEHOLDER_H("placeholder h"),
		PLACEHOLDER_J("placeholder j"),
		PLACEHOLDER_K("placeholder k"),
		PLACEHOLDER_L("placeholder l"),
		PLACEHOLDER_M("placeholder M"),
		ENABLE_LOGIN("Enable login"),
		PLACEHOLDER_N("placeholder N"),
		PLACEHOLDER_O("placeholder O"),
		PLACEHOLDER_P("placeholder P"),
		PLACEHOLDER_Q("placeholder Q"),
		PLACEHOLDER_R("placeholder R"),
		PLACEHOLDER_S("placeholder S"),
		PLACEHOLDER_T("placeholder T"),
		PLACEHOLDER_U("placeholder U"),
		PLACEHOLDER_V("placeholder V"),
		PLACEHOLDER_W("placeholder W"),
		PLACEHOLDER_X("placeholder X"),
		PLACEHOLDER_Y("placeholder Y"),
		PLACEHOLDER_Z("placeholder Z");

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
