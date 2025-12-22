package lto.manager.web.handlers.http.pages.sandpit;

import java.util.Map.Entry;

import org.xmlet.htmlapifaster.Div;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import lto.manager.common.database.tables.records.RecordRole.Permission;
import lto.manager.web.handlers.Handlers;
import lto.manager.web.handlers.http.BaseHTTPHandler;
import lto.manager.web.handlers.http.partial.inlinemessage.InlineMessage;
import lto.manager.web.handlers.http.templates.TemplatePage.BreadCrumbs;
import lto.manager.web.handlers.http.templates.TemplatePage.SelectedPage;
import lto.manager.web.handlers.http.templates.TemplatePage.TemplatePageModel;
import lto.manager.web.handlers.http.templates.models.BodyModel;
import lto.manager.web.handlers.http.templates.models.HeadModel;
import lto.manager.web.handlers.websockets.BaseWebsocketHandler;
import lto.manager.web.resource.Asset;
import lto.manager.web.resource.CSS;

public class ShowAllHandlersHandler extends BaseHTTPHandler {
	public static final String PATH = Asset.PATH_SANDPIT_BASE + "show-handler/";
	public static final String NAME = "Show All Handlers";

	static Void content(Div<?> view, BodyModel model) {

		final String rootSearch = BaseHTTPHandler.class.getPackage().getName().replace(".", "/");
		final String httpClassName = BaseHTTPHandler.class.getSimpleName();
		final String wsClassName = BaseWebsocketHandler.class.getSimpleName();
		final String msg = "The package path [" + rootSearch + "] is searched at boot for classes that inherit "
				+ httpClassName + " (HTTP) or " + wsClassName + " (WebSocket) these are added to router.";

		view
			.div().of(d -> InlineMessage.contentGenericInfo(d, msg))
			.div().attrClass(CSS.GROUP).addAttr(CSS.GROUP_ATTRIBUTE, "HTTP handler (routes)")
				.of(d -> {
					for (Entry<String, HttpHandler> handler : Handlers.httpHandlers.entrySet()) {
						d.a().attrClass(CSS.BUTTON).attrHref(handler.getKey()).text(handler.getKey()).__();
					}
				})
			.__()
			.div().attrClass(CSS.GROUP).addAttr(CSS.GROUP_ATTRIBUTE, "WebSocket handler (routes)")
				.of(d -> {
					for (Entry<String, BaseWebsocketHandler> handler : Handlers.websocketHandlers.entrySet()) {
						d.a().attrClass(CSS.BUTTON).attrHref(handler.getKey()).text(handler.getKey()).__();
					}
				})
			.__();
		return null;
	}

	@Override
	public void requestHandle(HttpExchange he, BodyModel bm) throws Exception {
		HeadModel thm = HeadModel.of(NAME);
		BreadCrumbs crumbs = new BreadCrumbs().add(SandpitHandler.NAME, SandpitHandler.PATH).add(NAME, PATH);
		TemplatePageModel tpm = TemplatePageModel.of(ShowAllHandlersHandler::content, null, thm, SelectedPage.Sandpit, bm, crumbs);
		requestHandleCompletePage(he, tpm);
	}

	@Override
	public Permission getHandlePermission() {
		// TODO Auto-generated method stub
		return null;
	}

}
