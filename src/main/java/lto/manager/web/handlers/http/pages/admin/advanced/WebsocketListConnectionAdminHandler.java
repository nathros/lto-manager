package lto.manager.web.handlers.http.pages.admin.advanced;

import java.util.List;

import org.java_websocket.WebSocket;
import org.xmlet.htmlapifaster.Div;

import com.sun.net.httpserver.HttpExchange;

import lto.manager.common.database.tables.records.RecordRole.Permission;
import lto.manager.web.handlers.Handlers;
import lto.manager.web.handlers.http.BaseHTTPHandler;
import lto.manager.web.handlers.http.pages.admin.AdminHandler;
import lto.manager.web.handlers.http.templates.TemplatePage.BreadCrumbs;
import lto.manager.web.handlers.http.templates.TemplatePage.SelectedPage;
import lto.manager.web.handlers.http.templates.TemplatePage.TemplatePageModel;
import lto.manager.web.handlers.http.templates.models.BodyModel;
import lto.manager.web.handlers.http.templates.models.HeadModel;
import lto.manager.web.handlers.websockets.BaseWebsocketHandler;
import lto.manager.web.resource.CSS;

public class WebsocketListConnectionAdminHandler extends BaseHTTPHandler {
	public static final String PATH = AdminHandler.PATH + "/websocket-con/";
	public static final Permission PERMISSION = Permission.ADVANCED_WEBSOCKET_CONNECTION_VIEWER;
	public static final String NAME = "Websocket Connections";

	static Void content(Div<?> view, BodyModel model) {
		view.div()
		.of(div -> {
			for (final String path : Handlers.websocketHandlers.keySet()) {
				final BaseWebsocketHandler ws = Handlers.websocketHandlers.get(path);
				final List<WebSocket> list = ws.getConnections();
				div
					.h3().text(path).__()
					.of(d -> {
						for (final var con : list) {
							final var remote = con.getRemoteSocketAddress();
							d.span()
								.attrClass(CSS.FONT_MONOSPACE)
								.text("Host/IP: " + remote.getAddress().toString() + ", port: " + remote.getPort() + " status: " + con.getReadyState().name())
							.__()
							.br().__();
						}
						if (list.size() == 0) {
							d.span()
								.attrClass(CSS.FONT_MONOSPACE)
								.text("No active connections")
							.__();
						}
					})
					.hr().__();
			}
		})

		.__();
		return null;
	}

	@Override
	public void requestHandle(HttpExchange he, BodyModel bm) throws Exception {
		HeadModel thm = HeadModel.of(NAME);
		BreadCrumbs crumbs = new BreadCrumbs().add(AdminHandler.NAME, AdminHandler.PATH).add(NAME, PATH);
		TemplatePageModel tpm = TemplatePageModel.of(WebsocketListConnectionAdminHandler::content, null, thm, SelectedPage.Admin, bm, crumbs);
		requestHandleCompletePage(he, tpm);
	}

	@Override
	public Permission getHandlePermission() {
		return PERMISSION;
	}

}
