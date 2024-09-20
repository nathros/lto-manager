package lto.manager.web.handlers.http.pages.sandpit;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;

import com.sun.net.httpserver.HttpExchange;

import lto.manager.common.database.tables.records.RecordRole.Permission;
import lto.manager.web.handlers.http.BaseHTTPHandler;
import lto.manager.web.handlers.http.templates.models.BodyModel;

public class EchoGetHandler extends BaseHTTPHandler {

	public static final String PATH = "/echoget/";

	@Override
	public void requestHandle(HttpExchange he, BodyModel bm) throws IOException {
		// parse request
		URI requestedUri = he.getRequestURI();
		String response = requestedUri.getRawQuery();
		he.sendResponseHeaders(HttpURLConnection.HTTP_OK, response.length());
		OutputStream os = he.getResponseBody();
		os.write(response.toString().getBytes());
		os.close();
	}

	@Override
	public Permission getHandlePermission() {
		// TODO Auto-generated method stub
		return null;
	}

}