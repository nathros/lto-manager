package lto.manager.web.handlers.http.sandpit;

import java.io.OutputStream;
import java.net.HttpURLConnection;

import org.xmlet.htmlapifaster.EnumTypeButtonType;
import org.xmlet.htmlapifaster.EnumTypeInputType;

import com.sun.net.httpserver.HttpExchange;

import htmlflow.DynamicHtml;
import lto.manager.web.handlers.http.BaseHTTPHandler;
import lto.manager.web.handlers.http.templates.TemplatePage;
import lto.manager.web.handlers.http.templates.TemplatePage.SelectedPage;
import lto.manager.web.handlers.http.templates.TemplatePage.TemplatePageModel;
import lto.manager.web.handlers.http.templates.models.BodyModel;
import lto.manager.web.handlers.http.templates.models.HeadModel;
import lto.manager.web.resource.Asset;

public class WebsocketTestHandler extends BaseHTTPHandler {

	public static final String PATH = "/sandpit/websocket";
	public static DynamicHtml<BodyModel> view = DynamicHtml.view(WebsocketTestHandler::body);

	static void body(DynamicHtml<BodyModel> view, BodyModel model) {
		final String start = model.getQueryNoNull("start");

		if (!start.equals("")) {

		}

		view
			.div()
				.button().attrType(EnumTypeButtonType.BUTTON).attrOnclick("test()").text("test()").__().br().__()
				.form()
					.b().text("Websocket message test: " + (start.equals("") ? "stopped" : "started")).__()
					.input().attrType(EnumTypeInputType.HIDDEN).attrName("start").attrValue("start").__()
					.button().attrType(EnumTypeButtonType.SUBMIT).text("Start").__().br().__()
				.__()
				.table().attrId("list")
					.tr()
						.th().text("Message").__()
					.__()
				.__()
			.__();
	}

	@Override
	public void requestHandle(HttpExchange he) throws Exception {
		HeadModel thm = HeadModel.of("Websocket Test");
		thm.AddScript(Asset.JS_WEBSOCKET);
		TemplatePageModel tepm = TemplatePageModel.of(view, thm, SelectedPage.Sandpit, BodyModel.of(he, null));
		String response = TemplatePage.view.render(tepm);

		he.sendResponseHeaders(HttpURLConnection.HTTP_OK, response.length());
		OutputStream os = he.getResponseBody();
		os.write(response.getBytes());
		os.close();
	}

}
