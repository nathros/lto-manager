package lto.manager.web.handlers.http;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;

import com.sun.net.httpserver.HttpExchange;

import htmlflow.DynamicHtml;
import lto.manager.web.handlers.http.sandpit.DatabaseTestHandler;
import lto.manager.web.handlers.http.templates.TemplatePage;
import lto.manager.web.handlers.http.templates.TemplatePage.SelectedPage;
import lto.manager.web.handlers.http.templates.TemplatePage.TemplatePageModel;
import lto.manager.web.handlers.http.templates.models.BodyModel;
import lto.manager.web.handlers.http.templates.models.HeadModel;

public class RootHandler extends BaseHTTPHandler {
	public static final String PATH = "/";

	public static DynamicHtml<BodyModel> view = DynamicHtml.view(RootHandler::body);

	static void body(DynamicHtml<BodyModel> view, BodyModel model) {
		view
			.div()
				.p().a().attrHref(DatabaseTestHandler.PATH).text("test").__().__()
			.__(); // div
	}

	@Override
	public void requestHandle(HttpExchange he) throws IOException {
		HeadModel thm = HeadModel.of("Root");
		TemplatePageModel tepm = TemplatePageModel.of(view, thm, SelectedPage.Admin, BodyModel.of(he, null));
		String response = TemplatePage.view.render(tepm);

		he.sendResponseHeaders(HttpURLConnection.HTTP_OK, response.length());
		OutputStream os = he.getResponseBody();
		os.write(response.getBytes());
		os.close();
	}
}
