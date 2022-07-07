package lto.manager.web.handlers.sandpit;

import java.io.OutputStream;
import java.net.HttpURLConnection;

import com.sun.net.httpserver.HttpExchange;

import htmlflow.DynamicHtml;
import lto.manager.web.handlers.BaseHandler;
import lto.manager.web.handlers.templates.TemplateEmptyPage;
import lto.manager.web.handlers.templates.TemplateEmptyPage.TemplateEmptyPageModel;
import lto.manager.web.handlers.templates.TemplateHead.TemplateHeadModel;

public class DatabaseTestHandler extends BaseHandler {
	public static class EmptyModel {
		private EmptyModel() {}

		public static EmptyModel of() {
			return new EmptyModel();
		}
	}

	public static DynamicHtml<EmptyModel> view = DynamicHtml.view(DatabaseTestHandler::body);

	static void body(DynamicHtml<EmptyModel> view, EmptyModel model) {
		view
			.div()
				.p().text("new test").__()
			.__();
	}

	@Override
	public void requestHandle(HttpExchange he) throws Exception {
		//Database d = new Database();
		//d.openDatabase("config/base.db");

		TemplateHeadModel thm = TemplateHeadModel.of("Database");
		TemplateEmptyPageModel tepm = TemplateEmptyPageModel.of(view, thm);
		String response = TemplateEmptyPage.view.render(tepm);

		he.sendResponseHeaders(HttpURLConnection.HTTP_OK, response.length());
		OutputStream os = he.getResponseBody();
		os.write(response.getBytes());
		os.close();
	}
}
