package lto.manager.web.handlers;

import java.util.HashMap;

import com.sun.net.httpserver.HttpHandler;

import lto.manager.web.handlers.sandpit.DatabaseTestHandler;
import lto.manager.web.handlers.sandpit.SandpitHandler;

public class Handlers {
	public static HashMap<String, HttpHandler> handlers = getHandlers();

	private static HashMap<String, HttpHandler> getHandlers() {
		HashMap<String, HttpHandler> ret = new HashMap<String, HttpHandler>();
		ret.put("/", new RootHandler());
		ret.put("/assets", new AssetHandler());

		ret.put("/echoHeader", new EchoHeaderHandler());
		ret.put("/echoGet", new EchoGetHandler());
		ret.put("/echoPost", new EchoPostHandler());

		ret.put("/sandpit", new SandpitHandler());
		ret.put("/sandpit/database", new DatabaseTestHandler());
		return ret;
	}

}
