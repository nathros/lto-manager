package lto.manager.web.handlers.http.pages.sandpit;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
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
				.div()
					.attrClass(CSS.GROUP)
					.addAttr(CSS.GROUP_ATTRIBUTE, "Send SQLite Query")
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
					.attrClass(CSS.GROUP)
					.addAttr(CSS.GROUP_ATTRIBUTE, "Query Response")
					.attrStyle("display:flex;gap:1rem")
					.div()
						.attrStyle("flex:1;resize:vertical;min-height:20rem")
						.of(d -> {
							if (errorString != null) {
								d.b().attrStyle("color:red").text(errorString).__();
							} else {
								d.table()
								.attrClass(CSS.TABLE)
									.of(t -> {
										if (results == null) return;
										try {
											ResultSetMetaData meta = results.getMetaData();
											var tr = t.tr();
											for (int i = 1; i < meta.getColumnCount(); i++) {
												tr = tr.th().text(meta.getColumnName(i)).__();
											}
											var resTR = tr.__();
											while (results.next()) {
												var row = resTR.tr();
												for (int i = 1; i < meta.getColumnCount(); i++) {
													row.td().text(results.getString(1)).__();
												}
												row.__();
											}
										} catch (SQLException e) {
											t.text("Results failure: " + e.getMessage());
										}
									})
								.__(); // table
							}
						})
					.__() // div
				.__() // div card
			.__(); // form
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
