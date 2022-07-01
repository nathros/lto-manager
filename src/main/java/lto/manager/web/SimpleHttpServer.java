package lto.manager.web;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Map;

import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import lto.manager.web.handlers.HTTPSRedirectHandler;
import lto.manager.web.handlers.Handlers;

public class SimpleHttpServer {
	private HttpServer server;

	public void Start(int port, boolean redirectOnly) {
		try {
			server = HttpServer.create(new InetSocketAddress(port), 0);
			System.out.println("HTTP " + (redirectOnly ? "redirect" : "") + " server started at port: " + port);

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
			e.printStackTrace();
		}
	}

	public void Stop() {
		server.stop(0);
		System.out.println("HTTP server stopped");
	}
}
