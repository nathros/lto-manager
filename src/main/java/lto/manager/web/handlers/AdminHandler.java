package lto.manager.web.handlers;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;

import com.sun.net.httpserver.HttpExchange;

import htmlflow.DynamicHtml;
import lto.manager.web.handlers.templates.TemplateHead.TemplateHeadModel;
import lto.manager.web.handlers.templates.TemplatePage;
import lto.manager.web.handlers.templates.TemplatePage.SelectedPage;
import lto.manager.web.handlers.templates.TemplatePage.TemplatePageModel;
import lto.manager.web.handlers.templates.models.BodyModel;

public class AdminHandler extends BaseHandler {
	public static final String PATH = "/admin";

	public static DynamicHtml<BodyModel> view = DynamicHtml.view(AdminHandler::body);

	static void body(DynamicHtml<BodyModel> view, BodyModel model) {
		view
			.div()
				.p().text("Admin page").__()
			.__(); // div
	}

	@Override
	public void requestHandle(HttpExchange he) throws IOException {
		TemplateHeadModel thm = TemplateHeadModel.of("Admin");
		TemplatePageModel tepm = TemplatePageModel.of(view, thm, SelectedPage.Admin, BodyModel.of(he, null));
		String response = TemplatePage.view.render(tepm);

		he.sendResponseHeaders(HttpURLConnection.HTTP_OK, response.length());
		OutputStream os = he.getResponseBody();
		os.write(response.getBytes());
		os.close();
	}
}
