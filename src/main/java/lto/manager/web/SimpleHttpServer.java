package lto.manager.web;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.logging.Level;

import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import lto.manager.common.log.Log;
import lto.manager.web.handlers.Handlers;
import lto.manager.web.handlers.http.HTTPSRedirectHandler;

public class SimpleHttpServer {
	private HttpServer server;

	public void start(int port, boolean redirectOnly) {
		try {
			server = HttpServer.create(new InetSocketAddress(port), 10);
			Log.info("HTTP " + (redirectOnly ? "redirect " : "") + "server started at port: http://localhost:" + port);

			if (redirectOnly) {
				server.createContext("/", new HTTPSRedirectHandler());
			} else {
				for(Map.Entry<String, HttpHandler> entry : Handlers.httpHandlers.entrySet()) {
					String key = entry.getKey();
				    HttpHandler value = entry.getValue();
				    server.createContext(key, value);
				}
			}
			server.setExecutor(Executors.newCachedThreadPool());
			server.start();
		} catch (IOException e) {
			Log.log(Level.SEVERE, e.getMessage(), e);
			System.exit(-1);
		}
	}

	public void stop() {
		server.stop(0);
		Log.info("HTTP server stopped");
	}
}
