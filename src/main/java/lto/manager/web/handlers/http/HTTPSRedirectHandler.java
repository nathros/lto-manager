package lto.manager.web.handlers.http;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;

import com.sun.net.httpserver.HttpExchange;

import lto.manager.common.database.tables.records.RecordRole.Permission;
import lto.manager.web.MainWeb;
import lto.manager.web.handlers.http.templates.models.BodyModel;

public class HTTPSRedirectHandler extends BaseHTTPHandler {

	@Override
	public void requestHandle(HttpExchange he, BodyModel bm) throws IOException {
		String originalHost = he.getRequestHeaders().getFirst("Host");
		String newHost = null;
		String response = "";

		if (null != originalHost) {
			String http = String.valueOf(MainWeb.portHTTP);
			if (originalHost.contains(http)) {
				newHost = "https://" + originalHost.replace(http, String.valueOf(MainWeb.portHTTPS)) + he.getRequestURI();
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
			response += "Please use HTTPS on port " + MainWeb.portHTTPS;
			he.sendResponseHeaders(HttpURLConnection.HTTP_OK, response.length());
		}

		OutputStream os = he.getResponseBody();
		os.write(response.getBytes());
		os.close();
	}

	@Override
	public Permission getHandlePermission() {
		return null;
	}

}
