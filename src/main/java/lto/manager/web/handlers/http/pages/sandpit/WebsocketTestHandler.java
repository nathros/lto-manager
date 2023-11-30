package lto.manager.web.handlers.http.pages.sandpit;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.util.concurrent.CompletableFuture;

import org.xmlet.htmlapifaster.Div;
import org.xmlet.htmlapifaster.EnumTypeButtonType;
import org.xmlet.htmlapifaster.EnumTypeInputType;

import com.sun.net.httpserver.HttpExchange;

import lto.manager.web.handlers.http.BaseHTTPHandler;
import lto.manager.web.handlers.http.templates.TemplatePage;
import lto.manager.web.handlers.http.templates.TemplatePage.SelectedPage;
import lto.manager.web.handlers.http.templates.TemplatePage.TemplatePageModel;
import lto.manager.web.handlers.http.templates.models.BodyModel;
import lto.manager.web.handlers.http.templates.models.HeadModel;
import lto.manager.web.resource.Asset;

public class WebsocketTestHandler extends BaseHTTPHandler {
	public static final String PATH = "/sandpit/websocket";

	static void body(Div<?> view, BodyModel model) {
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
		TemplatePageModel tepm = TemplatePageModel.of(null, thm, SelectedPage.Sandpit, BodyModel.of(he, null), null);
		CompletableFuture<String> future = TemplatePage.view.renderAsync(tepm);
		String response = future.get();
		he.sendResponseHeaders(HttpURLConnection.HTTP_OK, response.length());
		OutputStream os = he.getResponseBody();
		os.write(response.getBytes());
		os.close();
	}

}
