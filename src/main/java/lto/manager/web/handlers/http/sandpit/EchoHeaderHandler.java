package lto.manager.web.handlers.http.sandpit;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;

import lto.manager.web.handlers.http.BaseHTTPHandler;

public class EchoHeaderHandler extends BaseHTTPHandler {

	public static final String PATH = "/echoHeader";

	@Override
	public void requestHandle(HttpExchange he) throws IOException {
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

}
