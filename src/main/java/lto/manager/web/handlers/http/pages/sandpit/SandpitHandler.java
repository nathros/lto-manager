package lto.manager.web.handlers.http.pages.sandpit;

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

public class SandpitHandler extends BaseHTTPHandler {
	public static final String PATH = "/sandpit";

	static Void content(Div<?> view, BodyModel model) {
		view
			.div().attrStyle("text-align:center")
				.p().a().attrClass(CSS.BUTTON).attrHref(EchoHeaderHandler.PATH).text("Echo Header").__().__()
				.p().a().attrClass(CSS.BUTTON).attrHref(EchoGetHandler.PATH).text("Echo GET").__().__()
				.p().a().attrClass(CSS.BUTTON).attrHref(EchoPostHandler.PATH).text("Echo POST").__().__()
				.p().a().attrClass(CSS.BUTTON).attrHref(DatabaseTestHandler.PATH).text("Database test").__().__()
				.p().a().attrClass(CSS.BUTTON).attrHref(WebsocketTestHandler.PATH).text("Websocket").__().__()
			.__(); //  div
		return null;
	}

	@Override
	public void requestHandle(HttpExchange he) throws IOException, InterruptedException, ExecutionException {
		HeadModel thm = HeadModel.of("Sandpit");
		TemplatePageModel tpm = TemplatePageModel.of(SandpitHandler::content, thm, SelectedPage.Sandpit, BodyModel.of(he, null), null);
		requestHandleCompletePage(he, tpm);
	}
}
