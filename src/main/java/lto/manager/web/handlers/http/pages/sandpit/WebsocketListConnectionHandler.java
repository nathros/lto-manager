package lto.manager.web.handlers.http.pages.sandpit;

import java.util.List;

import org.java_websocket.WebSocket;
import org.xmlet.htmlapifaster.Div;

import com.sun.net.httpserver.HttpExchange;

import lto.manager.web.handlers.Handlers;
import lto.manager.web.handlers.http.BaseHTTPHandler;
import lto.manager.web.handlers.http.templates.TemplatePage.BreadCrumbs;
import lto.manager.web.handlers.http.templates.TemplatePage.SelectedPage;
import lto.manager.web.handlers.http.templates.TemplatePage.TemplatePageModel;
import lto.manager.web.handlers.http.templates.models.BodyModel;
import lto.manager.web.handlers.http.templates.models.HeadModel;
import lto.manager.web.handlers.websockets.BaseWebsocketHandler;
import lto.manager.web.resource.Asset;
import lto.manager.web.resource.CSS;

public class WebsocketListConnectionHandler extends BaseHTTPHandler {
	public static final String PATH = Asset.PATH_SANDPIT_BASE + "websocket-con";
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
	public void requestHandle(HttpExchange he) throws Exception {
		HeadModel thm = HeadModel.of(NAME);
		BreadCrumbs crumbs = new BreadCrumbs().add(SandpitHandler.NAME, SandpitHandler.PATH).add(NAME, PATH);
		TemplatePageModel tpm = TemplatePageModel.of(WebsocketListConnectionHandler::content, null, thm,
				SelectedPage.Sandpit, BodyModel.of(he, null), crumbs);
		requestHandleCompletePage(he, tpm);
	}

}
