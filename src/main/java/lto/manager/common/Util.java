package lto.manager.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.jar.Attributes;
import java.util.jar.JarInputStream;
import java.util.jar.Manifest;

import lto.manager.common.log.Log;

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

	public static String getVersionNumberFromJar(final String jarPath) throws IOException {
		JarInputStream jarStream = new JarInputStream(new FileInputStream(jarPath));
		Manifest manifest = jarStream.getManifest();
		try {
			final Attributes mainAttributes = manifest.getMainAttributes();
			final String versionNumber = mainAttributes.getValue("Version");
			jarStream.close();
			if (versionNumber == null) {
				throw new IllegalArgumentException("No version number found in Jar");
			}
			return versionNumber;
		} catch (Exception e) {
			jarStream.close();
			throw new IllegalArgumentException("No manifest found in Jar");
		}
	}

	public static String virtualDirSeperatorsAdd(String input) {
		if (input.charAt(0) != '/')
			input = "/" + input;
		if (input.charAt(input.length() - 1) != '/')
			input = input.concat("/");
		return input; // Must start and end with /
	}

	public static String replaceLast(String text, String regex, String replacement) {
		return text.replaceFirst("(?s)" + regex + "(?!.*?" + regex + ")", replacement);
	}

	@SuppressWarnings("unchecked")
	private static <T extends Throwable> void throwException(Throwable exception, Object dummy) throws T {
		throw (T) exception;
	}

	// Allows an exception to be thrown without adding adding throws function
	// declaration
	public static void throwException(Throwable exception) {
		Util.<RuntimeException>throwException(exception, null);
	}

	public static void logAndException(final Throwable exception) {
		// Calling Log.severe() will show incorrect caller, need parent of caller
		Log.customStackIndexSevere(exception.getMessage(), Log.STACK_INDEX_PARENT);
		throwException(exception);
	}

	public static ContainerType runningInContainer() {
		if (new File("/.dockerenv").exists() == true) {
			return ContainerType.Docker;
		} else {
			try {
				List<String> list = Files.readAllLines(new File("/proc/1/cgroup").toPath(), Charset.defaultCharset());
				for (int index = list.size() - 1; index >= 0; index--) {
					final String line = list.get(index);
					if (line.startsWith("0:")) {
						if (line.contains("/docker")) {
							return ContainerType.Docker;
						} else if (line.equals("0::/")) {
							return ContainerType.LXC;
						}
					}
				}
			} catch (IOException e) {
				Log.severe("Failed to check if running in container: " + e.getMessage());
			}
		}
		return ContainerType.None;
	}

	public enum ContainerType {
		None, Docker, LXC
	}
}
