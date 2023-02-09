package lto.manager.web.handlers;

import java.util.HashMap;

import com.sun.net.httpserver.HttpHandler;

import lto.manager.web.handlers.files.FilesAddHandler;
import lto.manager.web.handlers.files.FilesBrowserHandler;
import lto.manager.web.handlers.files.FilesHandler;
import lto.manager.web.handlers.jobs.JobsHandler;
import lto.manager.web.handlers.sandpit.DatabaseTestHandler;
import lto.manager.web.handlers.sandpit.EchoGetHandler;
import lto.manager.web.handlers.sandpit.EchoHeaderHandler;
import lto.manager.web.handlers.sandpit.EchoPostHandler;
import lto.manager.web.handlers.sandpit.SandpitHandler;
import lto.manager.web.handlers.tapes.TapesCreateHandler;
import lto.manager.web.handlers.tapes.TapesDeleteHandler;
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
		ret.put(TapesDeleteHandler.PATH, new TapesDeleteHandler());

		ret.put(FilesHandler.PATH, new FilesHandler());
		ret.put(FilesAddHandler.PATH, new FilesAddHandler());
		ret.put(FilesBrowserHandler.PATH, new FilesBrowserHandler());

		ret.put(JobsHandler.PATH, new JobsHandler());

		ret.put(EchoHeaderHandler.PATH, new EchoHeaderHandler());
		ret.put(EchoGetHandler.PATH, new EchoGetHandler());
		ret.put(EchoGetHandler.PATH, new EchoPostHandler());

		ret.put(SandpitHandler.PATH, new SandpitHandler());
		ret.put(DatabaseTestHandler.PATH, new DatabaseTestHandler());
		return ret;
	}

}
