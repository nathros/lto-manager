package lto.manager.web.handlers.http.pages.sandpit;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import org.xmlet.htmlapifaster.Div;

import com.sun.net.httpserver.HttpExchange;

import lto.manager.web.handlers.http.BaseHTTPHandler;
import lto.manager.web.handlers.http.pages.sandpit.frontend.CheckBoxTestHandler;
import lto.manager.web.handlers.http.pages.sandpit.frontend.SwitchTestHandler;
import lto.manager.web.handlers.http.pages.sandpit.frontend.ToastTestHandler;
import lto.manager.web.handlers.http.templates.TemplatePage.BreadCrumbs;
import lto.manager.web.handlers.http.templates.TemplatePage.SelectedPage;
import lto.manager.web.handlers.http.templates.TemplatePage.TemplatePageModel;
import lto.manager.web.handlers.http.templates.models.BodyModel;
import lto.manager.web.handlers.http.templates.models.HeadModel;
import lto.manager.web.resource.Asset;
import lto.manager.web.resource.CSS;

public class SandpitHandler extends BaseHTTPHandler {
	public static final String PATH = Asset.PATH_SANDPIT_BASE;
	public static final String NAME = "Sandpit";

	static Void content(Div<?> view, BodyModel model) {
		view
			.div().attrClass(CSS.GROUP).addAttr(CSS.GROUP_ATTRIBUTE, "General Debug")
				.a().attrClass(CSS.BUTTON).attrHref(EchoHeaderHandler.PATH).text("Echo Request Header").__()
				.a().attrClass(CSS.BUTTON).attrHref(EchoGetHandler.PATH).text("Echo GET").__()
				.a().attrClass(CSS.BUTTON).attrHref(EchoPostHandler.PATH).text("Echo POST").__()
				.a().attrClass(CSS.BUTTON).attrHref(LogTestHandler.PATH).text(LogTestHandler.NAME).__()
				.a().attrClass(CSS.BUTTON).attrHref(ClearCacheHandler.PATH).text(ClearCacheHandler.NAME).__()
				.a().attrClass(CSS.BUTTON).attrHref(DatabaseTestHandler.PATH).text(DatabaseTestHandler.NAME).__()
			.__()
			.div().attrClass(CSS.GROUP).addAttr(CSS.GROUP_ATTRIBUTE, "Frontend")
				.a().attrClass(CSS.BUTTON).attrHref(ToastTestHandler.PATH).text(ToastTestHandler.NAME).__()
				.a().attrClass(CSS.BUTTON).attrHref(CheckBoxTestHandler.PATH).text(CheckBoxTestHandler.NAME).__()
				.a().attrClass(CSS.BUTTON).attrHref(SwitchTestHandler.PATH).text(SwitchTestHandler.NAME).__()
			.__()
			.div().attrClass(CSS.GROUP).addAttr(CSS.GROUP_ATTRIBUTE, "Networking")
				.a().attrClass(CSS.BUTTON).attrHref(WebsocketTestHandler.PATH).text(WebsocketTestHandler.NAME).__()
				.a().attrClass(CSS.BUTTON).attrHref(WebsocketListConnectionHandler.PATH).text(WebsocketListConnectionHandler.NAME).__()
			.__()
			.div().attrClass(CSS.GROUP).addAttr(CSS.GROUP_ATTRIBUTE, "Error Handling")
				.a().attrClass(CSS.BUTTON).attrHref(InternalErrorTesterHandler.PATH).text(InternalErrorTesterHandler.NAME).__()
				.a().attrClass(CSS.BUTTON).attrHref(InternalErrorInlineTesterHandler.PATH).text(InternalErrorInlineTesterHandler.NAME).__()
			.__();
		return null;
	}

	@Override
	public void requestHandle(HttpExchange he) throws IOException, InterruptedException, ExecutionException {
		HeadModel thm = HeadModel.of(NAME);
		BreadCrumbs crumbs = new BreadCrumbs().add(SandpitHandler.NAME, SandpitHandler.PATH);
		TemplatePageModel tpm = TemplatePageModel.of(SandpitHandler::content, null, thm, SelectedPage.Sandpit, BodyModel.of(he, null), crumbs);
		requestHandleCompletePage(he, tpm);
	}
}
