package lto.manager.web.handlers.http.pages.admin;

import org.xmlet.htmlapifaster.Div;
import org.xmlet.htmlapifaster.EnumTypeInputType;

import com.sun.net.httpserver.HttpExchange;

import lto.manager.web.handlers.Handlers;
import lto.manager.web.handlers.http.BaseHTTPHandler;
import lto.manager.web.handlers.http.templates.TemplatePage.BreadCrumbs;
import lto.manager.web.handlers.http.templates.TemplatePage.SelectedPage;
import lto.manager.web.handlers.http.templates.TemplatePage.TemplatePageModel;
import lto.manager.web.handlers.http.templates.models.BodyModel;
import lto.manager.web.handlers.http.templates.models.HeadModel;
import lto.manager.web.resource.Asset;
import lto.manager.web.resource.CSS;
import lto.manager.web.resource.HTML;

public class WebsocketTestAdminHandler extends BaseHTTPHandler {
	public static final String PATH = AdminHandler.PATH + "/websocket";
	public static final String NAME = "Websocket Tester";

	static Void content(Div<?> view, BodyModel model) {
		view
			.div().attrStyle("display:flex;gap:1rem")
				.select()
					.attrOnchange("document.getElementById('ws-path').value = this.value")
					.option().of(o -> HTML.option(o, true, true)).text("Choose a path").__()
					.of(s -> {
						final var paths = Handlers.websocketHandlers.keySet();
						for (final String p : paths) {
							s.option().attrValue(p).text(p).__();
						}
					})
				.__()
				.input()
					.attrType(EnumTypeInputType.TEXT)
					.attrStyle("flex:1")
					.attrId("ws-path")
					.attrPlaceholder("Websocket path")
				.__()
				.button()
					.attrClass(CSS.BUTTON)
					.attrOnclick("testWS(this)")
					.text("Connect")
				.__()
			.__()
			.div()
				.attrClass(CSS.GROUP)
				.addAttr(CSS.GROUP_ATTRIBUTE, "Send Message")
				.attrStyle("display:flex;gap:1rem")
				.textarea()
					.attrClass(CSS.FONT_MONOSPACE)
					.attrStyle("flex:1;resize:vertical;height:9rem")
					.attrId("ws-tx")
				.__()
				.button()
					.attrClass(CSS.BUTTON)
					.attrOnclick("testWS.ws === undefined ? alert('Not connected') : testWS.ws.send(document.getElementById('ws-tx').value)")
					.text("Send")
				.__()
			.__()
			.div()
				.attrClass(CSS.GROUP)
				.addAttr(CSS.GROUP_ATTRIBUTE, "Received Message")
				.attrStyle("display:flex;gap:1rem")
				.textarea()
					.of(t -> HTML.textArea(t, true))
					.attrClass(CSS.FONT_MONOSPACE)
					.attrStyle("flex:1;resize:vertical;height:20rem")
					.attrId("ws-rx")
				.__()
				.button()
					.attrClass(CSS.BUTTON)
					.attrOnclick("document.getElementById('ws-rx').value=''")
					.text("Clear")
				.__()
			.__()
			.div()
				.attrClass(CSS.GROUP)
				.addAttr(CSS.GROUP_ATTRIBUTE, "Event Log")
				.attrStyle("display:flex;gap:1rem")
				.textarea()
					.of(t -> HTML.textArea(t, true))
					.attrClass(CSS.FONT_MONOSPACE)
					.attrStyle("flex:1;resize:vertical;height:8rem")
					.attrId("ws-event")
				.__()
				.button()
					.attrClass(CSS.BUTTON)
					.attrOnclick("document.getElementById('ws-event').value=''")
					.text("Clear")
				.__()
			.__();
		return null;
	}

	@Override
	public void requestHandle(HttpExchange he) throws Exception {
		HeadModel thm = HeadModel.of(NAME);
		thm.addScript(Asset.JS_WEBSOCKET).addScript(Asset.JS_TEST_WEBSOCKET);
		BreadCrumbs crumbs = new BreadCrumbs().add(AdminHandler.NAME, AdminHandler.PATH).add(NAME, PATH);
		TemplatePageModel tpm = TemplatePageModel.of(WebsocketTestAdminHandler::content, null, thm, SelectedPage.Admin, BodyModel.of(he, null), crumbs);
		requestHandleCompletePage(he, tpm);
	}

}
