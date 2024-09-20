package lto.manager.web.handlers.http.pages.sandpit;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;

import lto.manager.common.database.tables.records.RecordRole.Permission;
import lto.manager.web.handlers.http.BaseHTTPHandler;
import lto.manager.web.handlers.http.templates.models.BodyModel;

public class EchoHeaderHandler extends BaseHTTPHandler {

	public static final String PATH = "/echoheader/";

	@Override
	public void requestHandle(HttpExchange he, BodyModel bm) throws IOException {
		Headers headers = he.getRequestHeaders();
		Set<Map.Entry<String, List<String>>> entries = headers.entrySet();
		String response = "";
		for (Map.Entry<String, List<String>> entry : entries)
			response += entry.toString() + "\n";
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
