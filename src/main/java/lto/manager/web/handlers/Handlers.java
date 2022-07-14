package lto.manager.web.handlers;

import java.util.HashMap;

import com.sun.net.httpserver.HttpHandler;

import lto.manager.web.handlers.sandpit.DatabaseTestHandler;
import lto.manager.web.handlers.sandpit.SandpitHandler;
import lto.manager.web.handlers.tapes.TapesCreateHandler;
import lto.manager.web.handlers.tapes.TapesHandler;

public class Handlers {
	public static HashMap<String, HttpHandler> handlers = getHandlers();

	private static HashMap<String, HttpHandler> getHandlers() {
		HashMap<String, HttpHandler> ret = new HashMap<String, HttpHandler>();
		ret.put(RootHandler.PATH, new RootHandler());
		ret.put(AssetHandler.PATH, new AssetHandler());
		ret.put(AdminHandler.PATH, new AdminHandler());


		ret.put(TapesHandler.PATH, new TapesHandler());
		ret.put(TapesCreateHandler.PATH, new TapesCreateHandler());

		ret.put(FilesHandler.PATH, new FilesHandler());

		ret.put("/echoHeader", new EchoHeaderHandler());
		ret.put("/echoGet", new EchoGetHandler());
		ret.put("/echoPost", new EchoPostHandler());

		ret.put(SandpitHandler.PATH, new SandpitHandler());
		ret.put(DatabaseTestHandler.PATH, new DatabaseTestHandler());
		return ret;
	}

}
