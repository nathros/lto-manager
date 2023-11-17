package lto.manager.web.handlers;

import java.util.HashMap;

import com.sun.net.httpserver.HttpHandler;

import lto.manager.common.Main;
import lto.manager.web.handlers.http.ajax.AJAXGetAttachedDrivesFetcher;
import lto.manager.web.handlers.http.ajax.AJAXGetOnloadError;
import lto.manager.web.handlers.http.ajax.filelist.AJAXFilesListFetcher;
import lto.manager.web.handlers.http.ajax.filelist.AJAXIconListFetcher;
import lto.manager.web.handlers.http.api.APIVirtualDir;
import lto.manager.web.handlers.http.pages.AssetHandler;
import lto.manager.web.handlers.http.pages.RootHandler;
import lto.manager.web.handlers.http.pages.admin.AdminHandler;
import lto.manager.web.handlers.http.pages.admin.UpdateOptionsHandler;
import lto.manager.web.handlers.http.pages.admin.externalprocess.ExternalProcessHandler;
import lto.manager.web.handlers.http.pages.admin.externalprocess.ExternalProcessViewerHandler;
import lto.manager.web.handlers.http.pages.drives.DrivesHandler;
import lto.manager.web.handlers.http.pages.files.FilesBrowserHandler;
import lto.manager.web.handlers.http.pages.files.FilesBrowserHandler2;
import lto.manager.web.handlers.http.pages.files.FilesHandler;
import lto.manager.web.handlers.http.pages.jobs.JobsDetailsHandler;
import lto.manager.web.handlers.http.pages.jobs.JobsHandler;
import lto.manager.web.handlers.http.pages.jobs.JobsNewBackupHandler;
import lto.manager.web.handlers.http.pages.jobs.JobsTypeHandler;
import lto.manager.web.handlers.http.pages.library.LibraryCreateHandler;
import lto.manager.web.handlers.http.pages.library.LibraryDeleteHandler;
import lto.manager.web.handlers.http.pages.library.LibraryHandler;
import lto.manager.web.handlers.http.pages.sandpit.DatabaseTestHandler;
import lto.manager.web.handlers.http.pages.sandpit.EchoGetHandler;
import lto.manager.web.handlers.http.pages.sandpit.EchoHeaderHandler;
import lto.manager.web.handlers.http.pages.sandpit.EchoPostHandler;
import lto.manager.web.handlers.http.pages.sandpit.SandpitHandler;
import lto.manager.web.handlers.http.pages.sandpit.WebsocketTestHandler;
import lto.manager.web.handlers.websockets.BaseWebsocketHandler;
import lto.manager.web.handlers.websockets.ServerTimeHandler;

public class Handlers {
	public final static HashMap<String, HttpHandler> httpHandlers = getHTTPHandlers();
	public final static HashMap<String, BaseWebsocketHandler> websocketHandlers = getWebsocketHandlers();

	private static HashMap<String, HttpHandler> getHTTPHandlers() {
		HashMap<String, HttpHandler> ret = new HashMap<String, HttpHandler>();
		ret.put(RootHandler.PATH, new RootHandler());
		ret.put(AssetHandler.PATH, new AssetHandler());

		ret.put(AdminHandler.PATH, new AdminHandler());
		ret.put(UpdateOptionsHandler.PATH, new UpdateOptionsHandler());
		ret.put(ExternalProcessHandler.PATH, new ExternalProcessHandler());
		ret.put(ExternalProcessViewerHandler.PATH, new ExternalProcessViewerHandler());

		ret.put(LibraryHandler.PATH, new LibraryHandler());
		ret.put(LibraryCreateHandler.PATH, new LibraryCreateHandler());
		ret.put(LibraryDeleteHandler.PATH, new LibraryDeleteHandler());

		ret.put(DrivesHandler.PATH, new DrivesHandler());
		ret.put(AJAXGetAttachedDrivesFetcher.PATH, new AJAXGetAttachedDrivesFetcher());

		ret.put(FilesHandler.PATH, new FilesHandler());
		ret.put(FilesBrowserHandler.PATH, new FilesBrowserHandler());
		ret.put(FilesBrowserHandler2.PATH, new FilesBrowserHandler2());

		ret.put(JobsHandler.PATH, new JobsHandler());
		ret.put(JobsTypeHandler.PATH, new JobsTypeHandler());
		ret.put(JobsNewBackupHandler.PATH, new JobsNewBackupHandler());
		ret.put(JobsDetailsHandler.PATH, new JobsDetailsHandler());

		ret.put(AJAXFilesListFetcher.PATH, new AJAXFilesListFetcher());
		ret.put(AJAXIconListFetcher.PATH, new AJAXIconListFetcher());
		ret.put(AJAXGetOnloadError.PATH, new AJAXGetOnloadError());

		ret.put(APIVirtualDir.PATH, new APIVirtualDir());

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
