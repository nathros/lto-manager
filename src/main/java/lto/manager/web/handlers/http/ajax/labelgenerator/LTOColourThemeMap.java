package lto.manager.web.handlers.http.ajax.labelgenerator;

import java.io.IOException;
import java.util.HashMap;
import java.util.Set;

import lto.manager.common.Util;
import lto.manager.web.handlers.http.pages.AssetHandler;
import lto.manager.web.resource.Asset;

public record LTOColourThemeMap(HashMap<String, HashMap<String, String>> themeMapContainer) {
	public static LTOColourThemeMap of() {
		final String themeFile = Asset.IMG_LTO_LABEL + "theme.ini";
		HashMap<String, HashMap<String, String>> themeMapContainer = new HashMap<String, HashMap<String, String>>();
		try {
			final String ini = AssetHandler.getResourceAsString(themeFile);
			final String[] lines = ini.split("\n");
			HashMap<String, String> themeMap = null;
			for (final String line: lines) {
				if (line.charAt(0) == '[') { // Start of ini field
					themeMap = new HashMap<String, String>();
					final String name = line.replace("[", "").replace("]", "");
					themeMapContainer.put(name, themeMap);
				} else {
					final String[] keyValue = line.split("=");
					if ((themeMap == null) || (keyValue.length != 2)) {
						Util.logAndException(new Exception("Malformed ini LTO colour theme file"));
					}
					themeMap.put(keyValue[0], keyValue[1]);
				}
			}
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
