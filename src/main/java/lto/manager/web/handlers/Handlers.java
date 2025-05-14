package lto.manager.web.handlers;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.function.Consumer;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import com.sun.net.httpserver.HttpHandler;

import lto.manager.web.handlers.websockets.BaseWebsocketHandler;

public class Handlers {
	public final static HashMap<String, HttpHandler> httpHandlers = getHTTPHandlers();
	public final static HashMap<String, BaseWebsocketHandler> websocketHandlers = getWebsocketHandlers();

	private static Object createHandlerInstance(final String className) throws Exception {
		return Class.forName(className).getConstructor().newInstance();
	}

	private static String getHandlerInstancePath(final Object object) throws Exception {
		Field pathField = object.getClass().getDeclaredField("PATH");
		String str = null;
		return (String) pathField.get(str);
	}

	private static void processClassDirectory(Consumer<Object> add, String packagePrefix, File[] files) {
		for (File file : files) {
			try {
				if (file.isDirectory()) {
					processClassDirectory(add, packagePrefix + "." + file.getName(), file.listFiles());
				} else {
					String className = file.getName();
					if (className.endsWith(".class")) {
						className = className.substring(0, className.length() - 6);
					}
					add.accept(createHandlerInstance(packagePrefix + "." + className));
				}
			} catch (ClassCastException e) {
				// Do nothing
			} catch (Exception e) {
				// Do nothing
			}

		}
	}

	private static HashMap<String, HttpHandler> getHTTPHandlers() {
		HashMap<String, HttpHandler> ret = new HashMap<String, HttpHandler>();
		getHandlers(handler -> {
			try {
				ret.put(getHandlerInstancePath(handler), (HttpHandler) handler);
			} catch (Exception e) {}
		});
		return ret;
	}

	private static HashMap<String, BaseWebsocketHandler> getWebsocketHandlers() {
		HashMap<String, BaseWebsocketHandler> ret = new HashMap<String, BaseWebsocketHandler>();
		getHandlers(handler -> {
			try {
				ret.put(getHandlerInstancePath(handler), (BaseWebsocketHandler) handler);
			} catch (Exception e) {}
		});
		return ret;

	}

	private static void getHandlers(Consumer<Object> add) {
		// Get all handlers for HTTP and add to HashMap
		// If running in IDE then can scan through build directory to get class names to instantiate
		// If running in compiled Jar then need to scan all files to get class names to instantiate

		URL selfClassURL = Handlers.class.getResource("Handlers.class"); // Self class
		final String canonicalName = Handlers.class.getCanonicalName();
		String packageName = canonicalName.substring(0, canonicalName.length() - Handlers.class.getSimpleName().length() - 1);
		URLConnection urlConnection = null;
		try {
			urlConnection = selfClassURL.openConnection();
		} catch (IOException e) {
			e.printStackTrace(); // Should not fail
		}
		if (urlConnection instanceof JarURLConnection) {
			// Program is run in as Jar
			try (JarFile file = ((JarURLConnection) urlConnection).getJarFile()) {
				final String packageNamePath = packageName.replace('.', File.separatorChar);
				Enumeration<JarEntry> entriesJar = file.entries();
				while (entriesJar.hasMoreElements()) { // Not ideal to iterate over every file in Jar
					JarEntry entry = entriesJar.nextElement();
					if (entry.isDirectory()) {
						continue;
					}
					final String entryName = entry.getName();
					if (entryName.startsWith(packageNamePath) && entryName.endsWith(".class")) {
						try {
							String className = entryName.replace(File.separatorChar, '.'); // Path to class name
							className = className.substring(0, className.length() - 6); // Remove .class
							Object newHandler = createHandlerInstance(className);
							add.accept(newHandler);
						} catch (Exception e) {}
					}
				}
			} catch (Exception e) {}
		} else {
			// Program is run in IDE
			URL handlerURL = urlConnection.getURL();
			File handlerFile = new File(handlerURL.getPath());
			File baseFile = handlerFile.getParentFile();
			processClassDirectory(add, packageName, baseFile.listFiles());
		}
	}

}
