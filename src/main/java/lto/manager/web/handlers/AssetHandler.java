package lto.manager.web.handlers;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;

import com.sun.net.httpserver.HttpExchange;

import lto.manager.web.Asset;

public class AssetHandler extends BaseHandler {

	private static String path = Asset.class.getPackageName().replace(".", File.separator);
	private static ClassLoader loader = new Asset().getClass().getClassLoader();

	@Override
	public void handle(HttpExchange he) throws IOException {
		super.handle(he);
		URI requestedFile = he.getRequestURI();
		String resource = path + requestedFile;
		InputStream is = null;
		try {
			is = loader.getResourceAsStream(resource);
		} catch (Exception e) {}

		// TODO traversal attack
		if (is != null) {
			byte[] data = is.readAllBytes();

			he.sendResponseHeaders(HttpURLConnection.HTTP_OK, data.length);
			OutputStream os = he.getResponseBody();
			os.write(data);
			os.close();
		} else { // File not found
			/*String response = "403 (Forbidden)\n";
			he.sendResponseHeaders(HttpURLConnection.HTTP_FORBIDDEN, response.length());
			OutputStream os = he.getResponseBody();
			os.write(response.getBytes());
			os.close();*/

			String response = "404 (Not Found)\n";
			he.sendResponseHeaders(HttpURLConnection.HTTP_NOT_FOUND, response.length());
			OutputStream os = he.getResponseBody();
			os.write(response.getBytes());
			os.close();
		}
		if (is != null) is.close();
	}
}
