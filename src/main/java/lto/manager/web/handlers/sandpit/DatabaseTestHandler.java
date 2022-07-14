package lto.manager.web.handlers.sandpit;

import java.io.OutputStream;
import java.net.HttpURLConnection;

import org.xmlet.htmlapifaster.EnumTypeButtonType;

import com.sun.net.httpserver.HttpExchange;

import htmlflow.DynamicHtml;
import lto.manager.web.handlers.BaseHandler;
import lto.manager.web.handlers.templates.TemplateHead.TemplateHeadModel;
import lto.manager.web.handlers.templates.TemplatePage;
import lto.manager.web.handlers.templates.TemplatePage.SelectedPage;
import lto.manager.web.handlers.templates.TemplatePage.TemplatePageModel;
import lto.manager.web.handlers.templates.models.BodyModel;

public class DatabaseTestHandler extends BaseHandler {
	public static final String PATH = "/sandpit/database";
	public static DynamicHtml<BodyModel> view = DynamicHtml.view(DatabaseTestHandler::body);

	static void body(DynamicHtml<BodyModel> view, BodyModel model) {
		final String path = model.getQueryNoNull("path");

		view
			.div()
				.form()
					.b().text("Database path: ").__()
					.input().attrName("path").dynamic(input -> input.attrValue(path)).__()
					.button().attrType(EnumTypeButtonType.SUBMIT).text("Open Database").__().br().__()
				.__()
			.__();
	}

	@Override
	public void requestHandle(HttpExchange he) throws Exception {
		TemplateHeadModel thm = TemplateHeadModel.of("Database Test");
		TemplatePageModel tepm = TemplatePageModel.of(view, thm, SelectedPage.Sandpit, BodyModel.of(he, null));
		String response = TemplatePage.view.render(tepm);

		he.sendResponseHeaders(HttpURLConnection.HTTP_OK, response.length());
		OutputStream os = he.getResponseBody();
		os.write(response.getBytes());
		os.close();
	}
}
