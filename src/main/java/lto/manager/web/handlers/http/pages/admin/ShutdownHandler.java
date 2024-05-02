package lto.manager.web.handlers.http.pages.admin;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;

import com.sun.net.httpserver.HttpExchange;

import lto.manager.common.log.Log;
import lto.manager.web.MainWeb;
import lto.manager.web.handlers.http.BaseHTTPHandler;

public class ShutdownHandler extends BaseHTTPHandler {
	public static final String PATH = "/shutdown";

	@Override
	public void requestHandle(HttpExchange he) throws IOException, InterruptedException, ExecutionException {
		String response = "Shutdown";
		he.sendResponseHeaders(HttpURLConnection.HTTP_OK, response.length());
		OutputStream os = he.getResponseBody();
		os.write(response.getBytes());
		os.close();
		try {
			MainWeb.exitWait.put(MainWeb.ExitReason.NORMAL);
		} catch (InterruptedException e) {
			Log.log(Level.SEVERE, "Failed to initiate shutdown", e);
		}
	}

}
