package lto.manager.web.handlers.http.ajax.labelgenerator;

public class LTOLabelEnum {
	public static enum LTOLabelColourSettings {
		All, Numbers, None
	}

	public static enum LTOLabelTypeSettings {
		Gen1, Gen2, Gen3, Gen4, Gen5, Gen6, Gen7, Gen7M, Gen8, Gen9, Gen1WORM, Gen2WORM, Gen3WORM, Gen4WORM, Gen5WORM,
		Gen6WORM, Gen7WORM, Gen8WORM, Gen9WORM, Gen1Clean, Gen2Clean, Gen3Clean, Gen4Clean, Gen5Clean, Gen6Clean,
		Gen7Clean, Gen8Clean, Gen9Clean, GenAnyClean
	}

	public static String getTypeDescription(final LTOLabelTypeSettings type) {
		if (type.ordinal() <= LTOLabelTypeSettings.Gen9.ordinal()) {
			if (LTOLabelTypeSettings.Gen7M == type) {
				return "LTO Gen 7 Type-M";
			} else {
				int generation = type.ordinal();
				if (generation > LTOLabelTypeSettings.Gen7M.ordinal()) {
					generation--;
				}
				generation++;
				return "LTO Gen " + generation;
			}
		} else if (type.ordinal() >= LTOLabelTypeSettings.Gen1WORM.ordinal()
				&& (type.ordinal() <= LTOLabelTypeSettings.Gen9WORM.ordinal())) {
			int generation = type.ordinal();
			generation = generation - LTOLabelTypeSettings.Gen9.ordinal();
			return "LTO Gen " + generation + " WORM";
		} else if (type.ordinal() >= LTOLabelTypeSettings.Gen1Clean.ordinal()
				&& (type.ordinal() <= LTOLabelTypeSettings.Gen9Clean.ordinal())) {
			int generation = type.ordinal();
			generation = generation - LTOLabelTypeSettings.Gen9WORM.ordinal();
			return "LTO Gen " + generation + " Cleaning Cartridge";
		} else if (type.ordinal() == LTOLabelTypeSettings.GenAnyClean.ordinal()) {
			return "LTO Universal Cleaning Cartridge";
		}
		return "Unknown tape type";
	}

	public static String getTypeDesignation(final LTOLabelTypeSettings type) {
		if (type.ordinal() <= LTOLabelTypeSettings.Gen9.ordinal()) {
			if (LTOLabelTypeSettings.Gen7M == type) {
				return "M8";
			} else {
				int generation = type.ordinal();
				if (generation > LTOLabelTypeSettings.Gen7M.ordinal()) {
					generation--;
				}
				generation++;
				return "L" + generation;
			}
		} else if (type.ordinal() >= LTOLabelTypeSettings.Gen1WORM.ordinal()
				&& (type.ordinal() <= LTOLabelTypeSettings.Gen9WORM.ordinal())) {
			int generation = type.ordinal();
			generation = generation - LTOLabelTypeSettings.Gen9.ordinal() - 1;
			char start = 'R';
			start = (char) (start + generation);
			return "L" + start;
		} else if (type.ordinal() >= LTOLabelTypeSettings.Gen1Clean.ordinal()
				&& (type.ordinal() <= LTOLabelTypeSettings.Gen9Clean.ordinal())) {
			int generation = type.ordinal();
			generation = generation - LTOLabelTypeSettings.Gen9WORM.ordinal();
			return "C" + generation;
		} else if (type.ordinal() == LTOLabelTypeSettings.GenAnyClean.ordinal()) {
			return "CU";
		}
		return "Unknown tape type";
	}
}
