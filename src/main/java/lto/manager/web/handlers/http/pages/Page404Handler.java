package lto.manager.web.handlers.http.pages;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import org.xmlet.htmlapifaster.Div;

import com.sun.net.httpserver.HttpExchange;

import lto.manager.web.handlers.http.BaseHTTPHandler;
import lto.manager.web.handlers.http.pages.dashboard.DashboardHandler;
import lto.manager.web.handlers.http.templates.TemplatePage.SelectedPage;
import lto.manager.web.handlers.http.templates.TemplatePage.TemplatePageModel;
import lto.manager.web.handlers.http.templates.models.BodyModel;
import lto.manager.web.handlers.http.templates.models.HeadModel;
import lto.manager.web.resource.CSS;

public class Page404Handler extends BaseHTTPHandler {
	public static final String PATH = "/";

	static Void content(Div<?> view, BodyModel model) {
		view
			.div()
				.attrStyle("display:flex;flex-direction:column;align-items:center;justify-content:center;height:100%")
				.h1().text("404 - Page not found").__()
				.p().text("The requested URL").__()
				.b().attrClass(CSS.FONT_MONOSPACE).text(model.getUrl()).__()
				.p().text("was not found").__()
				.div()
					.a().attrClass(CSS.BUTTON).attrHref(PATH).text("Home").__()
					.button().attrClass(CSS.BUTTON).attrOnclick("history.back()").text("Back").__()
				.__()
			.__(); // div
		return null;
	}

	@Override
	public void requestHandle(HttpExchange he) throws IOException, InterruptedException, ExecutionException {
		HeadModel thm = HeadModel.of("404 - Page not found");
		TemplatePageModel tpm = TemplatePageModel.of(Page404Handler::content, null, thm, SelectedPage.Missing, BodyModel.of(he, null), null);
		if (tpm.getBodyModel().getUrl().equals(PATH)) {
			RedirectHandler.requestHandle(he, DashboardHandler.PATH);
		} else {
			requestHandleCompletePage404(he, tpm);
		}
	}
}
