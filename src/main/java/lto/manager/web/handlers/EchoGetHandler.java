package lto.manager.web.handlers;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import com.sun.net.httpserver.HttpExchange;

public class EchoGetHandler extends BaseHandler {

	@Override
	public void requestHandle(HttpExchange he) throws IOException {
		// parse request
		Map<String, Object> parameters = new HashMap<String, Object>();
		URI requestedUri = he.getRequestURI();
		//String query = requestedUri.getRawQuery();
		//parseQuery(query, parameters);
		// send response
		String response = "";
		for (String key : parameters.keySet())
			response += key + " = " + parameters.get(key) + "\n";
		he.sendResponseHeaders(HttpURLConnection.HTTP_OK, response.length());
		OutputStream os = he.getResponseBody();
		os.write(response.toString().getBytes());
		os.close();
	}

}