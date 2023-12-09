package lto.manager.web.handlers.http.pages.drives;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import org.xmlet.htmlapifaster.Div;

import com.sun.net.httpserver.HttpExchange;

import lto.manager.web.handlers.http.BaseHTTPHandler;
import lto.manager.web.handlers.http.ajax.AJAXGetAttachedDrivesFetcher;
import lto.manager.web.handlers.http.partial.loading.OnLoad;
import lto.manager.web.handlers.http.partial.loading.OnLoadOptions;
import lto.manager.web.handlers.http.templates.TemplatePage.BreadCrumbs;
import lto.manager.web.handlers.http.templates.TemplatePage.SelectedPage;
import lto.manager.web.handlers.http.templates.TemplatePage.TemplatePageModel;
import lto.manager.web.handlers.http.templates.models.BodyModel;
import lto.manager.web.handlers.http.templates.models.HeadModel;
import lto.manager.web.resource.Asset;

public class DrivesHandler extends BaseHTTPHandler {
	public static final String PATH = "/drives";
	public static final String NAME = "Drives";
	public static final String DATA_AJAX = "data-ajax";

	static Void body(Div<?> view, BodyModel model) {
		view
			.div()
				.of(div -> OnLoad.content(div, OnLoadOptions.of(AJAXGetAttachedDrivesFetcher.PATH)))
			.__(); // div
		return null;
	}

	@Override
	public void requestHandle(HttpExchange he) throws IOException, InterruptedException, ExecutionException {
		HeadModel thm = HeadModel.of(NAME);
		thm.addCSS(Asset.CSS_DRIVES);
		thm.addScript(Asset.JS_ON_LOAD_AJAX);
		BreadCrumbs crumbs = new BreadCrumbs().add(NAME, PATH);
		TemplatePageModel tpm = TemplatePageModel.of(DrivesHandler::body, thm, SelectedPage.Drives, BodyModel.of(he, null), crumbs);
		requestHandleCompletePage(he, tpm);
	}
}
