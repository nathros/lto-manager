package lto.manager.web.handlers;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;

import com.sun.net.httpserver.HttpExchange;

import lto.manager.web.Main;

public class HTTPSRedirectHandler extends BaseHandler {

	@Override
	public void handle(HttpExchange he) throws IOException {
		super.handle(he);
		String originalHost = he.getRequestHeaders().getFirst("Host");
		String newHost = null;
		String response = "";

		if (null != originalHost) {
			String http = String.valueOf(Main.portHTTP);
			if (originalHost.contains(http)) {
				newHost = "https://" + originalHost.replace(http, String.valueOf(Main.portHTTPS)) + he.getRequestURI();
			} else {
				String port = "NULL";
				int index = originalHost.indexOf(':');
				if (index > 0) {
					port = originalHost.substring(index, originalHost.length() - 1);
				}
				response += "Error: port " + http + " was not found in URL got " + port + "<br>";
			}
		}

		if (newHost != null) {
			response += "Redirect to HTTPS";
			he.getResponseHeaders().set("Location", newHost);
			he.sendResponseHeaders(HttpURLConnection.HTTP_MOVED_PERM, response.length());
		} else {
			response += "Please use HTTPS on port " + Main.portHTTPS;
			he.sendResponseHeaders(HttpURLConnection.HTTP_OK, response.length());
		}

		OutputStream os = he.getResponseBody();
		os.write(response.getBytes());
		os.close();
	}
}
