package lto.manager.web.handlers;

import java.util.HashMap;

import com.sun.net.httpserver.HttpHandler;

import lto.manager.common.Main;
import lto.manager.web.handlers.http.AdminHandler;
import lto.manager.web.handlers.http.AssetHandler;
import lto.manager.web.handlers.http.RootHandler;
import lto.manager.web.handlers.http.ajax.hostfiles.AJAXFilesListFetcher;
import lto.manager.web.handlers.http.drives.DrivesHandler;
import lto.manager.web.handlers.http.drives.ajax.GetAttachedDrivesFetcher;
import lto.manager.web.handlers.http.files.FilesAddHandler;
import lto.manager.web.handlers.http.files.FilesBrowserHandler;
import lto.manager.web.handlers.http.files.FilesHandler;
import lto.manager.web.handlers.http.jobs.JobsDetailsHandler;
import lto.manager.web.handlers.http.jobs.JobsHandler;
import lto.manager.web.handlers.http.jobs.JobsNewBackupHandler;
import lto.manager.web.handlers.http.jobs.JobsTypeHandler;
import lto.manager.web.handlers.http.sandpit.DatabaseTestHandler;
import lto.manager.web.handlers.http.sandpit.EchoGetHandler;
import lto.manager.web.handlers.http.sandpit.EchoHeaderHandler;
import lto.manager.web.handlers.http.sandpit.EchoPostHandler;
import lto.manager.web.handlers.http.sandpit.SandpitHandler;
import lto.manager.web.handlers.http.sandpit.WebsocketTestHandler;
import lto.manager.web.handlers.http.tapes.TapesCreateHandler;
import lto.manager.web.handlers.http.tapes.TapesDeleteHandler;
import lto.manager.web.handlers.http.tapes.TapesHandler;
import lto.manager.web.handlers.websockets.BaseWebsocketHandler;
import lto.manager.web.handlers.websockets.ServerTimeHandler;

public class Handlers {
	public static HashMap<String, HttpHandler> httpHandlers = getHTTPHandlers();
	public static HashMap<String, BaseWebsocketHandler> websocketHandlers = getWebsocketHandlers();

	private static HashMap<String, HttpHandler> getHTTPHandlers() {
		HashMap<String, HttpHandler> ret = new HashMap<String, HttpHandler>();
		ret.put(RootHandler.PATH, new RootHandler());
		ret.put(AssetHandler.PATH, new AssetHandler());
		ret.put(AdminHandler.PATH, new AdminHandler());

		ret.put(TapesHandler.PATH, new TapesHandler());
		ret.put(TapesCreateHandler.PATH, new TapesCreateHandler());
		ret.put(TapesDeleteHandler.PATH, new TapesDeleteHandler());

		ret.put(DrivesHandler.PATH, new DrivesHandler());
		ret.put(GetAttachedDrivesFetcher.PATH, new GetAttachedDrivesFetcher());

		ret.put(FilesHandler.PATH, new FilesHandler());
		ret.put(FilesAddHandler.PATH, new FilesAddHandler());
		ret.put(FilesBrowserHandler.PATH, new FilesBrowserHandler());

		ret.put(JobsHandler.PATH, new JobsHandler());
		ret.put(JobsTypeHandler.PATH, new JobsTypeHandler());
		ret.put(JobsNewBackupHandler.PATH, new JobsNewBackupHandler());
		ret.put(JobsDetailsHandler.PATH, new JobsDetailsHandler());

		ret.put(AJAXFilesListFetcher.PATH, new AJAXFilesListFetcher());

		if (Main.DEBUG_MODE) {
			ret.put(EchoHeaderHandler.PATH, new EchoHeaderHandler());
			ret.put(EchoGetHandler.PATH, new EchoGetHandler());
			ret.put(EchoGetHandler.PATH, new EchoPostHandler());
			ret.put(SandpitHandler.PATH, new SandpitHandler());
			ret.put(DatabaseTestHandler.PATH, new DatabaseTestHandler());
			ret.put(WebsocketTestHandler.PATH, new WebsocketTestHandler());
		}
		return ret;
	}

	private static HashMap<String, BaseWebsocketHandler> getWebsocketHandlers() {
		HashMap<String, BaseWebsocketHandler> ret = new HashMap<String, BaseWebsocketHandler>();
		ret.put(ServerTimeHandler.PATH, new ServerTimeHandler());
		return ret;
	}
}
