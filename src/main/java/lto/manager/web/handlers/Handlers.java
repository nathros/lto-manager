package lto.manager.web.handlers;

import java.util.HashMap;

import com.sun.net.httpserver.HttpHandler;

import lto.manager.common.Main;
import lto.manager.web.handlers.http.ajax.AJAXGetAttachedDrivesFetcher;
import lto.manager.web.handlers.http.ajax.AJAXGetOnloadError;
import lto.manager.web.handlers.http.ajax.filelist.AJAXFilesListFetcher;
import lto.manager.web.handlers.http.ajax.filelist.AJAXIconListFetcher;
import lto.manager.web.handlers.http.ajax.labelgenerator.AJAXGenerateLTOLabelHTML;
import lto.manager.web.handlers.http.ajax.labelgenerator.AJAXGenerateLTOLabelPDF;
import lto.manager.web.handlers.http.ajax.labelgenerator.AJAXGenerateLTOLabelSVG;
import lto.manager.web.handlers.http.api.APIVirtualDir;
import lto.manager.web.handlers.http.pages.AssetHandler;
import lto.manager.web.handlers.http.pages.LogInHandler;
import lto.manager.web.handlers.http.pages.LogOutHandler;
import lto.manager.web.handlers.http.pages.RootHandler;
import lto.manager.web.handlers.http.pages.ShutdownHandler;
import lto.manager.web.handlers.http.pages.admin.AdminHandler;
import lto.manager.web.handlers.http.pages.admin.AppUpdateHandler;
import lto.manager.web.handlers.http.pages.admin.LoggingHandler;
import lto.manager.web.handlers.http.pages.admin.SessionViewerHandler;
import lto.manager.web.handlers.http.pages.admin.UpdateOptionsHandler;
import lto.manager.web.handlers.http.pages.admin.externalprocess.ExternalProcessHandler;
import lto.manager.web.handlers.http.pages.admin.externalprocess.ExternalProcessViewerHandler;
import lto.manager.web.handlers.http.pages.dashboard.DashBoardHandler;
import lto.manager.web.handlers.http.pages.drives.DrivesHandler;
import lto.manager.web.handlers.http.pages.files.FilesBrowserHandler2;
import lto.manager.web.handlers.http.pages.files.FilesHandler;
import lto.manager.web.handlers.http.pages.jobs.JobsDetailsHandler;
import lto.manager.web.handlers.http.pages.jobs.JobsHandler;
import lto.manager.web.handlers.http.pages.jobs.JobsNewBackupHandler;
import lto.manager.web.handlers.http.pages.jobs.JobsTypeHandler;
import lto.manager.web.handlers.http.pages.library.LibraryCreateHandler;
import lto.manager.web.handlers.http.pages.library.LibraryDeleteHandler;
import lto.manager.web.handlers.http.pages.library.LibraryGenerateBarcodeHTMLHandler;
import lto.manager.web.handlers.http.pages.library.LibraryGenerateBarcodeHandler;
import lto.manager.web.handlers.http.pages.library.LibraryHandler;
import lto.manager.web.handlers.http.pages.sandpit.ClearCacheHandler;
import lto.manager.web.handlers.http.pages.sandpit.DatabaseTestHandler;
import lto.manager.web.handlers.http.pages.sandpit.EchoGetHandler;
import lto.manager.web.handlers.http.pages.sandpit.EchoHeaderHandler;
import lto.manager.web.handlers.http.pages.sandpit.EchoPostHandler;
import lto.manager.web.handlers.http.pages.sandpit.InternalErrorInlineAJAXTesterHandler;
import lto.manager.web.handlers.http.pages.sandpit.InternalErrorInlineTesterHandler;
import lto.manager.web.handlers.http.pages.sandpit.InternalErrorTesterHandler;
import lto.manager.web.handlers.http.pages.sandpit.LogTestHandler;
import lto.manager.web.handlers.http.pages.sandpit.SandpitHandler;
import lto.manager.web.handlers.http.pages.sandpit.WebsocketListConnectionHandler;
import lto.manager.web.handlers.http.pages.sandpit.WebsocketTestHandler;
import lto.manager.web.handlers.http.pages.sandpit.frontend.CheckBoxTestHandler;
import lto.manager.web.handlers.http.pages.sandpit.frontend.SwitchTestHandler;
import lto.manager.web.handlers.http.pages.sandpit.frontend.ToastTestHandler;
import lto.manager.web.handlers.websockets.BaseWebsocketHandler;
import lto.manager.web.handlers.websockets.ServerTimeHandler;
import lto.manager.web.handlers.websockets.admin.LoggingWebsocketHandler;

public class Handlers {
	public final static HashMap<String, HttpHandler> httpHandlers = getHTTPHandlers();
	public final static HashMap<String, BaseWebsocketHandler> websocketHandlers = getWebsocketHandlers();

	private static HashMap<String, HttpHandler> getHTTPHandlers() {
		HashMap<String, HttpHandler> ret = new HashMap<String, HttpHandler>();
		ret.put(RootHandler.PATH, new DashBoardHandler());
		ret.put(DashBoardHandler.PATH, new DashBoardHandler());
		ret.put(AssetHandler.PATH, new AssetHandler());
		ret.put(LogInHandler.PATH, new LogInHandler());
		ret.put(LogOutHandler.PATH, new LogOutHandler());

		ret.put(AdminHandler.PATH, new AdminHandler());
		ret.put(ShutdownHandler.PATH, new ShutdownHandler());
		ret.put(UpdateOptionsHandler.PATH, new UpdateOptionsHandler());
		ret.put(ExternalProcessHandler.PATH, new ExternalProcessHandler());
		ret.put(ExternalProcessViewerHandler.PATH, new ExternalProcessViewerHandler());
		ret.put(SessionViewerHandler.PATH, new SessionViewerHandler());
		ret.put(LoggingHandler.PATH, new LoggingHandler());
		ret.put(AppUpdateHandler.PATH, new AppUpdateHandler());

		ret.put(LibraryHandler.PATH, new LibraryHandler());
		ret.put(LibraryCreateHandler.PATH, new LibraryCreateHandler());
		ret.put(LibraryDeleteHandler.PATH, new LibraryDeleteHandler());
		ret.put(LibraryGenerateBarcodeHandler.PATH, new LibraryGenerateBarcodeHandler());
		ret.put(LibraryGenerateBarcodeHTMLHandler.PATH, new LibraryGenerateBarcodeHTMLHandler());

		ret.put(DrivesHandler.PATH, new DrivesHandler());
		ret.put(AJAXGetAttachedDrivesFetcher.PATH, new AJAXGetAttachedDrivesFetcher());

		ret.put(FilesHandler.PATH, new FilesHandler());
		ret.put(FilesBrowserHandler2.PATH, new FilesBrowserHandler2());

		ret.put(JobsHandler.PATH, new JobsHandler());
		ret.put(JobsTypeHandler.PATH, new JobsTypeHandler());
		ret.put(JobsNewBackupHandler.PATH, new JobsNewBackupHandler());
		ret.put(JobsDetailsHandler.PATH, new JobsDetailsHandler());

		ret.put(AJAXFilesListFetcher.PATH, new AJAXFilesListFetcher());
		ret.put(AJAXIconListFetcher.PATH, new AJAXIconListFetcher());
		ret.put(AJAXGetOnloadError.PATH, new AJAXGetOnloadError());
		ret.put(AJAXGenerateLTOLabelHTML.PATH, new AJAXGenerateLTOLabelHTML());
		ret.put(AJAXGenerateLTOLabelPDF.PATH, new AJAXGenerateLTOLabelPDF());
		ret.put(AJAXGenerateLTOLabelSVG.PATH, new AJAXGenerateLTOLabelSVG());

		ret.put(APIVirtualDir.PATH, new APIVirtualDir());

		if (Main.DEBUG_MODE) {
			ret.put(InternalErrorTesterHandler.PATH, new InternalErrorTesterHandler());
			ret.put(InternalErrorInlineTesterHandler.PATH, new InternalErrorInlineTesterHandler());
			ret.put(InternalErrorInlineAJAXTesterHandler.PATH, new InternalErrorInlineAJAXTesterHandler());
			ret.put(EchoHeaderHandler.PATH, new EchoHeaderHandler());
			ret.put(EchoGetHandler.PATH, new EchoGetHandler());
			ret.put(EchoGetHandler.PATH, new EchoPostHandler());
			ret.put(SandpitHandler.PATH, new SandpitHandler());
			ret.put(DatabaseTestHandler.PATH, new DatabaseTestHandler());
			ret.put(WebsocketTestHandler.PATH, new WebsocketTestHandler());
			ret.put(WebsocketListConnectionHandler.PATH, new WebsocketListConnectionHandler());
			ret.put(LogTestHandler.PATH, new LogTestHandler());
			ret.put(CheckBoxTestHandler.PATH, new CheckBoxTestHandler());
			ret.put(SwitchTestHandler.PATH, new SwitchTestHandler());
			ret.put(ClearCacheHandler.PATH, new ClearCacheHandler());
			ret.put(ToastTestHandler.PATH, new ToastTestHandler());
		}
		return ret;
	}

	private static HashMap<String, BaseWebsocketHandler> getWebsocketHandlers() {
		HashMap<String, BaseWebsocketHandler> ret = new HashMap<String, BaseWebsocketHandler>();
		ret.put(ServerTimeHandler.PATH, new ServerTimeHandler());
		ret.put(LoggingWebsocketHandler.PATH, new LoggingWebsocketHandler());
		return ret;
	}
}
