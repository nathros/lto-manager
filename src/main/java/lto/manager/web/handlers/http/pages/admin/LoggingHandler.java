package lto.manager.web.handlers.http.pages.admin;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import org.xmlet.htmlapifaster.Div;

import com.sun.net.httpserver.HttpExchange;

import lto.manager.web.handlers.http.BaseHTTPHandler;
import lto.manager.web.handlers.http.pages.LogOutHandler;
import lto.manager.web.handlers.http.templates.TemplatePage.BreadCrumbs;
import lto.manager.web.handlers.http.templates.TemplatePage.SelectedPage;
import lto.manager.web.handlers.http.templates.TemplatePage.TemplatePageModel;
import lto.manager.web.handlers.http.templates.models.BodyModel;
import lto.manager.web.handlers.http.templates.models.HeadModel;
import lto.manager.web.resource.Asset;
import lto.manager.web.resource.CSS;

public class LoggingHandler extends BaseHTTPHandler {
	public static final String PATH = AdminHandler.PATH + "/logging";
	public static final String NAME = "Logging";

	static Void content(Div<?> view, BodyModel model) {
		view
		.div()
			.table().attrClass(CSS.TABLE).attrId("table-logging")
				.tr()
					.th().text("Timestamp").__()
					.th().text("Level").__()
					.th().text("Class").__()
					.th().text("Function").__()
					.th().text("Message").__()
				.__()
			.__() // table
		.__(); //  div
		return null;
	}

	@Override
	public void requestHandle(HttpExchange he) throws IOException, InterruptedException, ExecutionException {
		HeadModel thm = HeadModel.of(NAME);
		thm.addCSS(Asset.CSS_LOGGING);
		thm.addScript(Asset.JS_WEBSOCKET).addScriptDefer(Asset.JS_LOGGING);
		BreadCrumbs crumbs = new BreadCrumbs().add(AdminHandler.NAME, AdminHandler.PATH).add(NAME, PATH);
		TemplatePageModel tpm = TemplatePageModel.of(LoggingHandler::content, LoggingHandler::header, thm, SelectedPage.Admin, BodyModel.of(he, null), crumbs);
		requestHandleCompletePage(he, tpm);
	}

	static Void header(Div<?> view, BodyModel model) {
		view
			.div()
				.attrClass(CSS.HEADER_ITEM + "")
				.ul().attrClass("menu-list")
					.li()
						.a()
							.attrClass(CSS.ICON_BOX_ARROW_RIGHT)
							.attrHref(LogOutHandler.PATH)
							.text("Test")
						.__()
					.__()
				.__() // ul
			.__() // div
			.div()
				.attrClass(CSS.HEADER_ITEM + CSS.ICON_CARET_DOWN)
				.ul().attrClass(CSS.MENU_LIST)
					.li()
						.attrClass(CSS.HEADER_LABEL_TOP)
						.text("Autoscroll")
					.__()
					.li()
						.a()
							.attrClass(CSS.ICON_BOX_ARROW_RIGHT)
							.text("Test")
						.__()
					.__()
				.__() // ul
			.__() // div
			.div()
				.attrClass(CSS.HEADER_ITEM + CSS.ICON_FUNNEL)
				.ul().attrClass(CSS.MENU_LIST)
					.li()
						.attrClass(CSS.HEADER_LABEL_TOP)
						.text("Filter")
					.__()
					.li()
						.a()
							.attrClass(CSS.ICON_BOX_ARROW_RIGHT)
							.text("Test")
						.__()
					.__()
				.__() // ul
			.__(); // div
		return null;
	}
}
