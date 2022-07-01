package lto.manager.web.handlers;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;

import com.sun.net.httpserver.HttpExchange;

import lto.manager.web.Asset;

public class AssetHandler extends BaseHandler {

	private static String path = Asset.class.getPackageName().replace('.', '/');
	private static ClassLoader loader = new Asset().getClass().getClassLoader();

	@Override
	public void handle(HttpExchange he) throws IOException {
		super.handle(he);
		URI requestedFile = he.getRequestURI();
		String resource = path + requestedFile;
		//URL rawPath = loader.getResource(res);

		// TODO traversal attack
		InputStream is = loader.getResourceAsStream(resource);
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
			he.sendResponseHeaders(404, response.length());
			OutputStream os = he.getResponseBody();
			os.write(response.getBytes());
			os.close();
		}
	}
}