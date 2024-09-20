package lto.manager.web.handlers.http.pages.sandpit;

import org.xmlet.htmlapifaster.Div;

import com.sun.net.httpserver.HttpExchange;

import lto.manager.common.database.tables.records.RecordRole.Permission;
import lto.manager.web.handlers.http.BaseHTTPHandler;
import lto.manager.web.handlers.http.templates.TemplateAJAX.TemplateFetcherModel;
import lto.manager.web.handlers.http.templates.models.BodyModel;
import lto.manager.web.resource.Asset;

public class InternalErrorInlineAJAXTesterHandler extends BaseHTTPHandler {
	// Asset.PATH_AJAX_BASE is needed for base of path, as an exception in this path
	// will call template TemplateInternalErrorAJAX rather than
	// TemplateInternalErrorPage
	public static final String PATH = Asset.PATH_AJAX_BASE + "internalerrorinline/";

	public static Void content(Div<?> view, BodyModel model) {
		int badInt = 32 / 0; // Cause exception
		view.div().text(badInt).__();
		return null;
	}

	@Override
	public void requestHandle(HttpExchange he, BodyModel bm) throws Exception {
		requestHandleCompleteFetcher(he, new TemplateFetcherModel(InternalErrorInlineAJAXTesterHandler::content, bm));
	}

	@Override
	public Permission getHandlePermission() {
		// TODO Auto-generated method stub
		return null;
	}
}
