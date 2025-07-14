package lto.manager.common;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;

import lto.manager.web.handlers.http.pages.AssetHandler;

public class IniFileProcessor {

	public static LinkedHashMap<String, HashMap<String, String>> asMultiMap(final String path) throws IOException {
		final String ini = AssetHandler.getResourceAsString(path);
		LinkedHashMap<String, HashMap<String, String>> multiMap = new LinkedHashMap<String, HashMap<String, String>>();
		final String[] lines = ini.split("\n");
		HashMap<String, String> themeMap = null;
		for (final String line : lines) {
			if (line.isBlank() || line.charAt(0) == ';') {
				continue; // Empty line or comment
			}
			if (line.charAt(0) == '[') { // Start of ini field
				themeMap = new HashMap<String, String>();
				final String name = line.replace("[", "").replace("]", "");
				multiMap.put(name, themeMap);
			} else {
				final String[] keyValue = line.split(" = ");
				if ((themeMap == null) || (keyValue.length != 2)) {
					Util.logAndException(new Exception("Malformed ini file: " + path));
				}
				themeMap.put(keyValue[0], keyValue[1]);
			}
		}
		return multiMap;
	}

	public static HashMap<String, String> getSection(final String path, final String sectionName) throws IOException {
		final String ini = AssetHandler.getResourceAsString(path);
		final String[] lines = ini.split("\n");
		HashMap<String, String> items = new HashMap<String, String>();

		final String search = "[" + sectionName + "]";
		boolean found = false;
		for (final String line : lines) {
			if (found == true) {
				if (line.charAt(0) == '[') {
					break;
				} else {
					final String[] keyValue = line.split(" = ");
					if (keyValue.length != 2) {
						Util.logAndException(new Exception("Malformed ini file: " + path));
					}
					items.put(keyValue[0], keyValue[1]);
				}
			} else {
				if (line.equals(search)) {
					found = true;
					continue;
				}
			}
		}
		return items;
	}
}
