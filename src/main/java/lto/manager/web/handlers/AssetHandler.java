package lto.manager.web.handlers;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;

import com.sun.net.httpserver.HttpExchange;

import lto.manager.web.resource.Asset;

public class AssetHandler extends BaseHandler {
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

}
