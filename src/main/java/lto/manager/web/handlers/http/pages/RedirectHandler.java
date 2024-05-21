package lto.manager.web.handlers.http.pages;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;

import com.sun.net.httpserver.HttpExchange;

public class RedirectHandler {

	public static void requestHandle(HttpExchange he, final String newPath) throws IOException, InterruptedException, ExecutionException {
		final String response = "";
		he.getResponseHeaders().putIfAbsent("Location", Arrays.asList(newPath));
		he.sendResponseHeaders(HttpURLConnection.HTTP_MOVED_TEMP, response.length());
		OutputStream os = he.getResponseBody();
		os.write(response.getBytes());
		os.close();
	}

}
