package lto.manager.web.handlers.http;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.sun.net.httpserver.HttpExchange;

import lto.manager.web.resource.Asset;

public class AssetHandler extends BaseHTTPHandler {
	public static final String PATH = "/assets";
	private static final String path = "lto/manager/web";
	private static ClassLoader loader = new Asset().getClass().getClassLoader();

	@Override
	public void requestHandle(HttpExchange he) throws IOException {
		URI requestedFile = he.getRequestURI();
		String resource = path + requestedFile;
		InputStream is = null;
		if (!resource.contains("..")) { // Traversal attack check
			try {
				is = loader.getResourceAsStream(resource);
			} catch (Exception e) {}

			String extension = requestedFile.toString();
			int index = extension.lastIndexOf('.');
			if (index > 0) {
				extension = extension.substring(index + 1);
				final String contentType = "Content-Type";
				switch (extension) {
					case "css": { he.getResponseHeaders().set(contentType, "text/css"); break; }
					case "js": { he.getResponseHeaders().set(contentType, "text/javascript"); break; }
					case "apng": { he.getResponseHeaders().set(contentType, "image/apng"); break; }
					case "avif": { he.getResponseHeaders().set(contentType, "image/avif"); break; }
					case "gif": { he.getResponseHeaders().set(contentType, "image/gif"); break; }
					case "jpg": { he.getResponseHeaders().set(contentType, "image/jpeg"); break; }
					case "png": { he.getResponseHeaders().set(contentType, "image/png"); break; }
					case "svg": { he.getResponseHeaders().set(contentType, "image/svg+xml"); break; }
					case "webp": { he.getResponseHeaders().set(contentType, "image/webp"); break; }
				}
			}
		}

		if (is != null) {
			byte[] data = is.readAllBytes();
			he.sendResponseHeaders(HttpURLConnection.HTTP_OK, data.length);
			OutputStream os = he.getResponseBody();
			os.write(data);
			os.close();
		} else { // File not found
			String response = "404 (Not Found)\n";
			he.sendResponseHeaders(HttpURLConnection.HTTP_NOT_FOUND, response.length());
			OutputStream os = he.getResponseBody();
			os.write(response.getBytes());
			os.close();
		}
		if (is != null) is.close();
	}

	public static List<String> getCachedFileListInDir(String dir) {
		try {
			final String res = path + dir;
			URL url = loader.getResource(res);
			File file = null;
			try {
				file = new File(url.toURI()); // Resources in Jar
			} catch (Exception e) {
				file = new File(url.getPath()); // Resources in file
		    }
		    File[] listOfFiles = file.listFiles();
		    List<String> filesNamesList = new ArrayList<String>();
		    for (File f : listOfFiles) {
			    filesNamesList.add(f.getName());
		    }
		    return filesNamesList;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new ArrayList<String>(); // No resources found
	}
}
