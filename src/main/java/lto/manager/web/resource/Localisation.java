package lto.manager.web.resource;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import lto.manager.common.IniFileProcessor;

public class Localisation {
	public final static HashMap<String, String> Langugage = getLanguages();
	private final static String[][] localisationData = getLocalisationData();

	private final static HashMap<String, String> getLanguages() {
		final String languagesFile = Asset.PATH_LOCALISATION + "list.ini";
		try {
			return IniFileProcessor.getSection(languagesFile, "Languages");
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	private final static String[][] getLocalisationData() {
		String[][] localData = new String[Langugage.size()][LOC.MAX.ordinal()];

		for (final String filePrefix : Langugage.keySet()) {
			final int index = Integer.parseInt(filePrefix);
			final String languagesFile = Asset.PATH_LOCALISATION + filePrefix + ".ini";

			try {
				final HashMap<String, String> kv = IniFileProcessor.getSection(languagesFile, "Text");
				for (Map.Entry<String, String> entry : kv.entrySet()) {
					final LOC loc = LOC.valueOf(entry.getKey());
					localData[index][loc.ordinal()] = entry.getValue();
				}
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}
		}

		return localData;
	}

	public static String get(LOC loc) {
		return localisationData[0][loc.ordinal()]; // FIXME only support 1 language for now
	}

	public enum LOC {
		// Ordinal values cannot be bigger than MAX

		APP_NAME(0), // The application name
		FORM_ADD(1), //
		FORM_CANCEL(2), //
		MAX(3); // None should have bigger ordinal than this

		LOC(int ordinal) {
		}
	};

}
