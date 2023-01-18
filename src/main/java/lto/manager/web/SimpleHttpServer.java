package lto.manager.web;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Map;
import java.util.logging.Level;

import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import lto.manager.common.log.Log;
import lto.manager.web.handlers.HTTPSRedirectHandler;
import lto.manager.web.handlers.Handlers;

public class SimpleHttpServer {
	private HttpServer server;

	public void Start(int port, boolean redirectOnly) {
		try {
			server = HttpServer.create(new InetSocketAddress(port), 0);
			Log.l.info("HTTP " + (redirectOnly ? "redirect " : "") + "server started at port: http://localhost:" + port);

			if (redirectOnly) {
				server.createContext("/", new HTTPSRedirectHandler());
			} else {
				for(Map.Entry<String, HttpHandler> entry : Handlers.handlers.entrySet()) {
					String key = entry.getKey();
				    HttpHandler value = entry.getValue();
				    server.createContext(key, value);
				}
			}
			server.setExecutor(null);
			server.start();
		} catch (IOException e) {
			Log.l.log(Level.SEVERE, e.getMessage(), e);
		}
	}

	public void Stop() {
		server.stop(0);
		Log.l.info("HTTP server stopped");
	}
}
