package lto.manager.web.handlers.http.ajax.labelgenerator;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Set;

import lto.manager.common.IniFileProcessor;
import lto.manager.common.Util;
import lto.manager.web.resource.Asset;

public record LTOColourThemeMap(HashMap<String, HashMap<String, String>> themeMapContainer) {
	public static LTOColourThemeMap of() {
		final String themeFile = Asset.IMG_LTO_LABEL + "theme.ini";
		LinkedHashMap<String, HashMap<String, String>> themeMapContainer = new LinkedHashMap<String, HashMap<String, String>>();
		try {
			themeMapContainer = IniFileProcessor.asMultiMap(themeFile);
		} catch (IOException e) {
			Util.logAndException(new Exception("Unable to find LTO theme file at: " + themeFile));
		}
		return new LTOColourThemeMap(themeMapContainer);
	}

	public HashMap<String, String> getTheme(final String themeName) {
		return themeMapContainer.get(themeName);
	}

	public Set<String> getThemeNames() {
		return themeMapContainer.keySet();
	}

}
