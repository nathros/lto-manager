package lto.manager.web.resource;

import java.io.IOException;
import java.util.HashMap;

import lto.manager.common.IniFileProcessor;

public class Localisation {
	public final static HashMap<String, String> Langugage = getLanguages();

	private final static HashMap<String, String> getLanguages() {
		final String languagesFile = Asset.PATH_LOCALISATION + "list.ini";
		try {
			return IniFileProcessor.getSection(languagesFile, "Languages");
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
}
