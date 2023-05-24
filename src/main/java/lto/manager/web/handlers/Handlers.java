package lto.manager.web.handlers;

import java.util.HashMap;

import com.sun.net.httpserver.HttpHandler;

import lto.manager.common.Main;
import lto.manager.web.handlers.http.ajax.AJAXFilesListFetcher;
import lto.manager.web.handlers.http.ajax.AJAXGetAttachedDrivesFetcher;
import lto.manager.web.handlers.http.ajax.AJAXGetOnloadError;
import lto.manager.web.handlers.http.pages.AssetHandler;
import lto.manager.web.handlers.http.pages.RootHandler;
import lto.manager.web.handlers.http.pages.admin.AdminHandler;
import lto.manager.web.handlers.http.pages.admin.UpdateOptionsHandler;
import lto.manager.web.handlers.http.pages.drives.DrivesHandler;
import lto.manager.web.handlers.http.pages.files.FilesAddHandler;
import lto.manager.web.handlers.http.pages.files.FilesBrowserHandler;
import lto.manager.web.handlers.http.pages.files.FilesHandler;
import lto.manager.web.handlers.http.pages.jobs.JobsDetailsHandler;
import lto.manager.web.handlers.http.pages.jobs.JobsHandler;
import lto.manager.web.handlers.http.pages.jobs.JobsNewBackupHandler;
import lto.manager.web.handlers.http.pages.jobs.JobsTypeHandler;
import lto.manager.web.handlers.http.pages.sandpit.DatabaseTestHandler;
import lto.manager.web.handlers.http.pages.sandpit.EchoGetHandler;
import lto.manager.web.handlers.http.pages.sandpit.EchoHeaderHandler;
import lto.manager.web.handlers.http.pages.sandpit.EchoPostHandler;
import lto.manager.web.handlers.http.pages.sandpit.SandpitHandler;
import lto.manager.web.handlers.http.pages.sandpit.WebsocketTestHandler;
import lto.manager.web.handlers.http.pages.tapes.TapesCreateHandler;
import lto.manager.web.handlers.http.pages.tapes.TapesDeleteHandler;
import lto.manager.web.handlers.http.pages.tapes.TapesHandler;
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
		ret.put(UpdateOptionsHandler.PATH, new UpdateOptionsHandler());

		ret.put(TapesHandler.PATH, new TapesHandler());
		ret.put(TapesCreateHandler.PATH, new TapesCreateHandler());
		ret.put(TapesDeleteHandler.PATH, new TapesDeleteHandler());

		ret.put(DrivesHandler.PATH, new DrivesHandler());
		ret.put(AJAXGetAttachedDrivesFetcher.PATH, new AJAXGetAttachedDrivesFetcher());

		ret.put(FilesHandler.PATH, new FilesHandler());
		ret.put(FilesAddHandler.PATH, new FilesAddHandler());
		ret.put(FilesBrowserHandler.PATH, new FilesBrowserHandler());

		ret.put(JobsHandler.PATH, new JobsHandler());
		ret.put(JobsTypeHandler.PATH, new JobsTypeHandler());
		ret.put(JobsNewBackupHandler.PATH, new JobsNewBackupHandler());
		ret.put(JobsDetailsHandler.PATH, new JobsDetailsHandler());

		ret.put(AJAXFilesListFetcher.PATH, new AJAXFilesListFetcher());
		ret.put(AJAXGetOnloadError.PATH, new AJAXGetOnloadError());

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
