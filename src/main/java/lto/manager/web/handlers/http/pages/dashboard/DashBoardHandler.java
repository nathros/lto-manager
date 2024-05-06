package lto.manager.web.handlers.http.pages.dashboard;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import org.xmlet.htmlapifaster.Div;

import com.sun.net.httpserver.HttpExchange;

import lto.manager.web.handlers.http.BaseHTTPHandler;
import lto.manager.web.handlers.http.templates.TemplatePage.SelectedPage;
import lto.manager.web.handlers.http.templates.TemplatePage.TemplatePageModel;
import lto.manager.web.handlers.http.templates.models.BodyModel;
import lto.manager.web.handlers.http.templates.models.HeadModel;
import lto.manager.web.resource.CSS;

public class DashBoardHandler extends BaseHTTPHandler {
	public static final String PATH = "/dashboard";
	public static final String NAME = "Dashboard";

	static Void content(Div<?> view, BodyModel model) {
		view
			.div()
				.attrClass(CSS.CARD_CONTAINER)
				.div()
					.attrClass(CSS.CARD)
					.h2().text("Information").__()
					.div()

					.__()
				.__() // div
			.__(); // div
		return null;
	}

	@Override
	public void requestHandle(HttpExchange he) throws IOException, InterruptedException, ExecutionException {
		HeadModel thm = HeadModel.of(NAME);
		TemplatePageModel tpm = TemplatePageModel.of(DashBoardHandler::content, null, thm, SelectedPage.Dashboard, BodyModel.of(he, null), null);
		requestHandleCompletePage(he, tpm);
	}
}
