package lto.manager.web.handlers.http.pages.sandpit;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.xmlet.htmlapifaster.Div;
import org.xmlet.htmlapifaster.EnumMethodType;
import org.xmlet.htmlapifaster.EnumTypeButtonType;

import com.sun.net.httpserver.HttpExchange;

import lto.manager.common.database.Database;
import lto.manager.web.handlers.http.BaseHTTPHandler;
import lto.manager.web.handlers.http.templates.TemplatePage.BreadCrumbs;
import lto.manager.web.handlers.http.templates.TemplatePage.SelectedPage;
import lto.manager.web.handlers.http.templates.TemplatePage.TemplatePageModel;
import lto.manager.web.handlers.http.templates.models.BodyModel;
import lto.manager.web.handlers.http.templates.models.HeadModel;
import lto.manager.web.resource.Asset;
import lto.manager.web.resource.CSS;
import lto.manager.web.resource.HTML;

public class DatabaseTestHandler extends BaseHTTPHandler {
	public static final String PATH = Asset.PATH_SANDPIT_BASE + "database";
	public static final String NAME = "Database";

	public static final String QSQL = "sql";

	static Void content(Div<?> view, BodyModel model) {
		final String query = model.getQuery(QSQL);
		String errorStr = null;
		ResultSet tmpResults = null;
		if (query != null) {
			try {
				tmpResults = Database.executeRawQuery(query.trim());
			} catch (Exception e) {
				errorStr = "Error: " + e.getMessage();
			}
		}

		final ResultSet results = tmpResults;
		final String errorString = errorStr;

		view
			.form()
				.attrMethod(EnumMethodType.POST)
				.attrAction("")
				.div()
					.attrClass(CSS.CARD)
					.addAttr(CSS.CARD_ATTRIBUTE, "Send SQLite Query")
					.attrStyle("display:flex;gap:1rem")
					.textarea()
						.attrClass(CSS.FONT_MONOSPACE)
						.attrName(QSQL)
						.attrStyle("flex:1;resize:vertical;height:9rem")
						.text(query == null ? "" : query.trim())
					.__()
					.button()
						.attrClass(CSS.BUTTON)
						.attrType(EnumTypeButtonType.SUBMIT)
						.text("Send")
					.__()
				.__()
				.div()
					.attrClass(CSS.CARD)
					.addAttr(CSS.CARD_ATTRIBUTE, "Query Response")
					.attrStyle("display:flex;gap:1rem")
					.textarea()
						.of(t -> HTML.textArea(t, true))
						.attrClass(CSS.FONT_MONOSPACE)
						.attrStyle("flex:1;resize:vertical;height:20rem")
						.of(t -> {
							if (errorString != null) {
								t.text(errorString);
							} else if (results != null) {
								try {
									String text = "";
									while (results.next()) {
										text = text.concat(results.getString(1) + "\n");
									}
									t.text(text);
								} catch (SQLException e) {
									e.printStackTrace();
								}
							}
						})
					.__()
				.__()
			.__();
		return null;
	}

	@Override
	public void requestHandle(HttpExchange he) throws Exception {
		HeadModel thm = HeadModel.of(NAME);
		BreadCrumbs crumbs = new BreadCrumbs().add(SandpitHandler.NAME, SandpitHandler.PATH).add(NAME, PATH);
		TemplatePageModel tpm = TemplatePageModel.of(DatabaseTestHandler::content, null, thm, SelectedPage.Sandpit, BodyModel.of(he, null), crumbs);
		requestHandleCompletePage(he, tpm);
	}
}
