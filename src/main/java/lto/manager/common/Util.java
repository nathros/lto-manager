package lto.manager.common;

import java.io.File;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Util {
	public static File getWorkingDir() {
		Path currentRelativePath = Paths.get("");
		String wdir = currentRelativePath.toAbsolutePath().toString();
		File f = new File(wdir);
		return f;
	}

	public static String encodeUrl(String input) {
		try {
			return URLEncoder.encode(input, "UTF-8");
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}

	public static String decodeUrl(String input) {
		try {
			return URLDecoder.decode(input, "UTF-8");
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}

	public static long getUsedMemory() {
		var runtime = Runtime.getRuntime();
		return runtime.totalMemory() - runtime.freeMemory();
	}

	public static long getJVMMaxMemory() {
		return Runtime.getRuntime().maxMemory();
	}

	public static long getJVMAllocatedMemory() {
		return Runtime.getRuntime().totalMemory();
	}
}
