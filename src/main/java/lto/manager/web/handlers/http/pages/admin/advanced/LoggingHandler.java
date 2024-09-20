package lto.manager.web.handlers.http.pages.admin.advanced;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;

import org.xmlet.htmlapifaster.Div;

import com.sun.net.httpserver.HttpExchange;

import lto.manager.common.database.tables.records.RecordRole.Permission;
import lto.manager.web.handlers.http.BaseHTTPHandler;
import lto.manager.web.handlers.http.pages.admin.AdminHandler;
import lto.manager.web.handlers.http.partial.components.CheckBox;
import lto.manager.web.handlers.http.partial.components.CheckBox.CheckBoxOptions;
import lto.manager.web.handlers.http.partial.components.Switch;
import lto.manager.web.handlers.http.partial.components.Switch.SwitchOptions;
import lto.manager.web.handlers.http.templates.TemplatePage.BreadCrumbs;
import lto.manager.web.handlers.http.templates.TemplatePage.SelectedPage;
import lto.manager.web.handlers.http.templates.TemplatePage.TemplatePageModel;
import lto.manager.web.handlers.http.templates.models.BodyModel;
import lto.manager.web.handlers.http.templates.models.HeadModel;
import lto.manager.web.resource.Asset;
import lto.manager.web.resource.CSS;

public class LoggingHandler extends BaseHTTPHandler {
	public static final String PATH = AdminHandler.PATH + "logging/";
	public static final Permission PERMISSION = Permission.ADVANCED_VIEW_LOGGING;
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
	public void requestHandle(HttpExchange he, BodyModel bm) throws IOException, InterruptedException, ExecutionException {
		HeadModel thm = HeadModel.of(NAME);
		thm.addCSS(Asset.CSS_LOGGING);
		thm.addScript(Asset.JS_WEBSOCKET).addScriptDefer(Asset.JS_LOGGING);
		BreadCrumbs crumbs = new BreadCrumbs().add(AdminHandler.NAME, AdminHandler.PATH).add(NAME, PATH);
		TemplatePageModel tpm = TemplatePageModel.of(LoggingHandler::content, LoggingHandler::header, thm, SelectedPage.Admin, bm, crumbs);
		requestHandleCompletePage(he, tpm);
	}

	static Void header(Div<?> view, BodyModel model) {
		view
			.div()
				.attrClass(CSS.HEADER_ITEM + CSS.ICON_DOWNLOAD)
				.ul().attrClass(CSS.MENU_LIST)
					.li()
						.attrClass(CSS.HEADER_LABEL_TOP)
						.text("Log File")
					.__()
					.li()
						.attrClass(CSS.MENU_LIST_ITEM_BUTTON)
						.button()
							.attrStyle("border:var(--border)")
							.attrClass(CSS.BUTTON + CSS.BUTTON_MENU_LIST)
							.attrOnclick("downloadLogFile()")
							.text("Download")
						.__()
					.__() //li
				.__() // ul
			.__() // div
			.div()
				.attrClass(CSS.HEADER_ITEM + CSS.ICON_FILE_TEXT)
				.ul().attrClass(CSS.MENU_LIST)
					.li()
						.attrClass(CSS.HEADER_LABEL_TOP)
						.text("Compact Padding")
					.__()
					.li()
						.attrClass(CSS.MENU_LIST_ITEM_BUTTON)
						.div()
							.attrStyle("width:inherit;display:flex;justify-content:center;")
							.of(div -> Switch.content(div, SwitchOptions.of().setID("compact").setOnChange("setCompact(this.checked)").setKeepBoarderChecked()))
						.__()
					.__() // li
				.__() // ul
			.__() // div
			.div()
				.attrClass(CSS.HEADER_ITEM + CSS.ICON_CARET_DOWN)
				.ul().attrClass(CSS.MENU_LIST)
					.li()
						.attrClass(CSS.HEADER_LABEL_TOP)
						.text("Scroll to new message")
					.__()
					.li()
						.attrClass(CSS.MENU_LIST_ITEM_BUTTON)
						.div()
							.attrStyle("width:inherit")
							.of(div -> Switch.content(div, SwitchOptions.of().setID("autoscroll").setChecked().setOnChange("setAutoscroll(this.checked)").setKeepBoarderChecked()))
						.__()
					.__() // li
					.li()
						.attrClass(CSS.MENU_LIST_ITEM_BUTTON)
						.button()
							.attrStyle("border:var(--border)")
							.attrClass(CSS.BUTTON + CSS.BUTTON_MENU_LIST)
							.attrOnclick("scrollToPos(false)")
							.text("Scroll To Top")
						.__()
					.__() //li
					.li()
						.attrClass(CSS.MENU_LIST_ITEM_BUTTON)
						.button()
							.attrStyle("border:var(--border)")
							.attrClass(CSS.BUTTON + CSS.BUTTON_MENU_LIST)
							.attrOnclick("scrollToPos(true)")
							.text("Scroll To Bottom")
						.__()
					.__() //li
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
							.attrStyle("padding:0;")
							.div()
								.attrOncontextmenu("return selectSingle(this)")
								.attrStyle("display:flex;width:inherit")
								.of(div -> CheckBox.content(div, CheckBoxOptions.of(Level.FINEST.toString()).setChecked().setID(Level.FINEST.toString()).setKeepBoarderChecked().setHasPadding().setFillContainer()))
							.__()
						.__()
					.__() // li
					.li()
						.a()
							.attrStyle("padding:0;")
							.div()
								.attrOncontextmenu("return selectSingle(this)")
								.attrStyle("display:flex;width:inherit")
								.of(div -> CheckBox.content(div, CheckBoxOptions.of(Level.FINER.toString()).setChecked().setID(Level.FINER.toString()).setKeepBoarderChecked().setHasPadding().setFillContainer()))
							.__()
						.__()
					.__() // li
					.li()
						.a()
							.attrStyle("padding:0;")
							.div()
								.attrOncontextmenu("return selectSingle(this)")
								.attrStyle("display:flex;width:inherit")
								.of(div -> CheckBox.content(div, CheckBoxOptions.of(Level.FINE.toString()).setChecked().setID(Level.FINE.toString()).setKeepBoarderChecked().setHasPadding().setFillContainer()))
							.__()
						.__()
					.__() // li
					.li()
						.a()
							.attrStyle("padding:0;")
							.div()
								.attrOncontextmenu("return selectSingle(this)")
								.attrStyle("display:flex;width:inherit")
								.of(div -> CheckBox.content(div, CheckBoxOptions.of(Level.INFO.toString()).setChecked().setID(Level.INFO.toString()).setKeepBoarderChecked().setHasPadding().setFillContainer()))
							.__()
						.__()
					.__() // li
					.li()
						.a()
							.attrStyle("padding:0;")
							.div()
								.attrOncontextmenu("return selectSingle(this)")
								.attrStyle("display:flex;width:inherit")
								.of(div -> CheckBox.content(div, CheckBoxOptions.of(Level.WARNING.toString()).setChecked().setID(Level.WARNING.toString()).setKeepBoarderChecked().setHasPadding().setFillContainer()))
							.__()
						.__()
					.__() // li
					.li()
						.a()
							.attrStyle("padding:0;")
							.div()
								.attrOncontextmenu("return selectSingle(this)")
								.attrStyle("display:flex;width:inherit")
								.of(div -> CheckBox.content(div, CheckBoxOptions.of(Level.SEVERE.toString()).setChecked().setID(Level.SEVERE.toString()).setKeepBoarderChecked().setHasPadding().setFillContainer()))
							.__()
						.__()
					.__() // li
					.li()
						.attrClass(CSS.MENU_LIST_ITEM_BUTTON)
						.button()
							.attrStyle("border:var(--border)")
							.attrClass(CSS.BUTTON + CSS.BUTTON_MENU_LIST)
							.attrOnclick("applyFilter()")
							.text("Apply")
						.__()
					.__() // li
					.li()
						.attrClass(CSS.MENU_LIST_ITEM_BUTTON)
						.button()
							.attrStyle("border:var(--border)")
							.attrClass(CSS.BUTTON + CSS.BUTTON_MENU_LIST)
							.attrOnclick("resetFilter()")
							.text("Reset")
						.__()
					.__() // li
				.__() // ul
			.__(); // div
		return null;
	}

	@Override
	public Permission getHandlePermission() {
		return PERMISSION;
	}
}
