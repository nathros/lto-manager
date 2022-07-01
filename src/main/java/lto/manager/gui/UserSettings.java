package lto.manager.gui;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;

public class UserSettings {
	public static final String HOME_DIR = System.getProperty("user.home");
	public static final String SETTING_DIR = HOME_DIR + File.separator + ".lto-manager";
	public static final String SETTING_FILE = HOME_DIR + File.separator + ".lto-manager" + File.separator + "settings.ini";
	
	public static final String S_KEY_WINDOW_WIDTH = "window-width";
	public static final String S_KEY_WINDOW_HEIGHT = "window-height";
	
	private HashMap<String, String> settingsMap = new HashMap<String, String>();
	private HashMap<String, String> settingsDefault = createDefault();
	
	public UserSettings() {
		if (!readSettings()) {
			System.out.println("Failed to read user settings");
		}
	}


	public HashMap<String, String> createDefault() {
		HashMap<String, String> map = new HashMap<String, String>();
		map.put(S_KEY_WINDOW_WIDTH, "600");
		map.put(S_KEY_WINDOW_HEIGHT, "600");
		return map;
	}
	
	public void createSettings() {
		File dir = new File(SETTING_DIR);
		Path path = Paths.get(SETTING_DIR);
		
		if (!Files.exists(path)) {
			if (!dir.mkdir()) {
				System.out.println("ERROR: failed to create settings dir " + SETTING_DIR);
				return;
			}
		}
	}

	public boolean readSettings() {
		if (!Files.exists(Paths.get(SETTING_FILE))) {
			// create defualt
		}
		
		HashMap<String, String> map = new HashMap<String, String>();
		
		try (BufferedReader br = new BufferedReader(new FileReader(SETTING_FILE))) {
		    String line;
		    while ((line = br.readLine()) != null) {
		    	String[] split = line.split("=");
		    	if (split.length == 2) {
		    		if (!map.containsKey(split[0])) {
		    			map.put(split[0], split[1]);
		    		} else {
		    			System.out.println("WARNING: duplicate settings key found " + split[0] + " existing value: " + map.get(split[0]) + " new value: " + split[1]);
		    		}
		    	} else {
		    		System.out.println("WARNING: bad settings record: " + line);
		    	}
		    }
		} catch (Exception e) {
			return false;
		}
		
		settingsMap = map;
		return true;
	}
	
	public int getRootWindowHeight() {
		String tmp = settingsMap.get(S_KEY_WINDOW_HEIGHT);
		try {
			int result = Integer.parseInt(tmp);
			return result;
		} catch (Exception e) {}
		return Integer.parseInt(settingsDefault.get(S_KEY_WINDOW_HEIGHT));
	}
	
	public int getRootWindowWidth() {
		String tmp = settingsMap.get(S_KEY_WINDOW_WIDTH);
		try {
			int result = Integer.parseInt(tmp);
			return result;
		} catch (Exception e) {}
		return Integer.parseInt(settingsDefault.get(S_KEY_WINDOW_WIDTH));
	}
	
}
