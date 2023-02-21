package lto.manager.web.handlers.http.sandpit;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;

import com.sun.net.httpserver.HttpExchange;

import htmlflow.DynamicHtml;
import lto.manager.web.handlers.http.BaseHTTPHandler;
import lto.manager.web.handlers.http.templates.TemplatePage;
import lto.manager.web.handlers.http.templates.TemplatePage.SelectedPage;
import lto.manager.web.handlers.http.templates.TemplatePage.TemplatePageModel;
import lto.manager.web.handlers.http.templates.models.BodyModel;
import lto.manager.web.handlers.http.templates.models.HeadModel;
import lto.manager.web.resource.CSS;

public class SandpitHandler extends BaseHTTPHandler {
	public static final String PATH = "/sandpit";
	public static DynamicHtml<BodyModel> view = DynamicHtml.view(SandpitHandler::body);

	static void body(DynamicHtml<BodyModel> view, BodyModel model) {
		view
			.div().attrStyle("text-align:center")
				.p().a().attrClass(CSS.BUTTON).attrHref(EchoHeaderHandler.PATH).text("Echo Header").__().__()
				.p().a().attrClass(CSS.BUTTON).attrHref(EchoGetHandler.PATH).text("Echo GET").__().__()
				.p().a().attrClass(CSS.BUTTON).attrHref(EchoPostHandler.PATH).text("Echo POST").__().__()
				.p().a().attrClass(CSS.BUTTON).attrHref(DatabaseTestHandler.PATH).text("Database test").__().__()
				.p().a().attrClass(CSS.BUTTON).attrHref(WebsocketTestHandler.PATH).text("Websocket").__().__()
			.__(); //  div
	}

	@Override
	public void requestHandle(HttpExchange he) throws IOException {
		HeadModel thm = HeadModel.of("Sandpit");
		TemplatePageModel tepm = TemplatePageModel.of(view, thm, SelectedPage.Sandpit, BodyModel.of(he, null));
		String response = TemplatePage.view.render(tepm);

		he.sendResponseHeaders(HttpURLConnection.HTTP_OK, response.length());
		OutputStream os = he.getResponseBody();
		os.write(response.getBytes());
		os.close();
	}
}
