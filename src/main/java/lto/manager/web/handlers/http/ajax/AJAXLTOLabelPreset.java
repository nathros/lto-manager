package lto.manager.web.handlers.http.ajax;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import org.xmlet.htmlapifaster.Div;

import com.sun.net.httpserver.HttpExchange;

import lto.manager.common.database.tables.records.RecordRole.Permission;
import lto.manager.web.handlers.http.BaseHTTPHandler;
import lto.manager.web.handlers.http.pages.library.LibraryGenerateBarcodeHandler;
import lto.manager.web.handlers.http.templates.TemplateAJAX.TemplateFetcherModel;
import lto.manager.web.handlers.http.templates.models.BodyModel;
import lto.manager.web.resource.Asset;

public class AJAXLTOLabelPreset extends BaseHTTPHandler {
	public static final String PATH = Asset.PATH_AJAX_BASE + "ltolabelpreset/";

	static Void content(Div<?> view, BodyModel model) {
		LibraryGenerateBarcodeHandler.header(view, model);
		return null;
	}

	@Override
	public void requestHandle(HttpExchange he, BodyModel bm) throws IOException, InterruptedException, ExecutionException {
		requestHandleCompleteFetcher(he, new TemplateFetcherModel(AJAXLTOLabelPreset::content, bm));
	}

	@Override
	public Permission getHandlePermission() {
		// TODO Auto-generated method stub
		return null;
	}
}
