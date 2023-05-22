package lto.manager.web.handlers.http.drives;

import java.io.IOException;

import org.xmlet.htmlapifaster.Div;

import com.sun.net.httpserver.HttpExchange;

import lto.manager.web.handlers.http.BaseHTTPHandler;
import lto.manager.web.handlers.http.drives.ajax.GetAttachedDrivesFetcher;
import lto.manager.web.handlers.http.tapes.TapesCreateHandler;
import lto.manager.web.handlers.http.templates.TemplatePage.SelectedPage;
import lto.manager.web.handlers.http.templates.TemplatePage.TemplatePageModel;
import lto.manager.web.handlers.http.templates.models.BodyModel;
import lto.manager.web.handlers.http.templates.models.HeadModel;
import lto.manager.web.resource.Asset;
import lto.manager.web.resource.CSS;

public class DrivesHandler extends BaseHTTPHandler {
	public static final String PATH = "/drives";
	public static final String DATA_AJAX = "data-ajax";

	static Void body(Div<?> view, BodyModel model) {
		view
			.div()
				.a().attrClass(CSS.BUTTON).attrHref(TapesCreateHandler.PATH).text("drives").__()
				.div()
					.addAttr(DATA_AJAX, GetAttachedDrivesFetcher.PATH)
					.text("Getting info")
				.__()
			.__(); // div
		return null;
	}

	@Override
	public void requestHandle(HttpExchange he) throws IOException {
		HeadModel thm = HeadModel.of("Drives");
		thm.AddCSS(Asset.CSS_DRIVES);
		thm.AddScript(Asset.JS_ON_LOAD_AJAX);
		TemplatePageModel tpm = TemplatePageModel.of(DrivesHandler::body, thm, SelectedPage.Drives, BodyModel.of(he, null));
		requestHandleCompletePage(he, tpm);
	}
}
