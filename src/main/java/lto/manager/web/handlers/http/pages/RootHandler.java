package lto.manager.web.handlers.http.pages;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import org.xmlet.htmlapifaster.Div;

import com.sun.net.httpserver.HttpExchange;

import lto.manager.web.handlers.http.BaseHTTPHandler;
import lto.manager.web.handlers.http.pages.sandpit.DatabaseTestHandler;
import lto.manager.web.handlers.http.templates.TemplatePage.SelectedPage;
import lto.manager.web.handlers.http.templates.TemplatePage.TemplatePageModel;
import lto.manager.web.handlers.http.templates.models.BodyModel;
import lto.manager.web.handlers.http.templates.models.HeadModel;

public class RootHandler extends BaseHTTPHandler {
	public static final String PATH = "/";

	static Void content(Div<?> view, BodyModel model) {
		view
			.div()
				.p().a().attrHref(DatabaseTestHandler.PATH).text("test").__().__()
			.__(); // div
		return null;
	}

	@Override
	public void requestHandle(HttpExchange he) throws IOException, InterruptedException, ExecutionException {
		HeadModel thm = HeadModel.of("Root");
		TemplatePageModel tpm = TemplatePageModel.of(RootHandler::content, null, thm, SelectedPage.Admin, BodyModel.of(he, null), null);
		requestHandleCompletePage(he, tpm);
	}
}
