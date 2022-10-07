package lto.manager.web.handlers.tapes;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.sql.SQLException;

import org.xmlet.htmlapifaster.EnumTypeButtonType;
import org.xmlet.htmlapifaster.EnumTypeInputType;

import com.sun.net.httpserver.HttpExchange;

import htmlflow.DynamicHtml;
import lto.manager.common.database.Database;
import lto.manager.web.handlers.BaseHandler;
import lto.manager.web.handlers.templates.TemplateHead.TemplateHeadModel;
import lto.manager.web.handlers.templates.TemplatePage;
import lto.manager.web.handlers.templates.TemplatePage.SelectedPage;
import lto.manager.web.handlers.templates.TemplatePage.TemplatePageModel;
import lto.manager.web.handlers.templates.models.BodyModel;

public class TapesDeleteHandler extends BaseHandler {
	public static final String PATH = "/tapes/delete";
	public static DynamicHtml<BodyModel> view = DynamicHtml.view(TapesDeleteHandler::body);

	public static final String ID = "id";

	static void body(DynamicHtml<BodyModel> view, BodyModel model) {
		final String idQuery = model.getQueryNoNull(ID);
		boolean s = false;
		String er = null;

		if (model.hasQuery()) {
			try {
				int tapeID = Integer.valueOf(idQuery);
				Database.DelTape(tapeID);
				s = true;
			} catch (Exception e) {
				e.printStackTrace();
				er = e.getMessage();
			}
		}

		final String errorMessage = er;
		final boolean addedTapeSuccess = s;

		try {
			view
				.div()
					.form()
						.b().text("id: ").__()
						.input().attrType(EnumTypeInputType.TEXT).attrName(ID).dynamic(input -> input.attrValue(idQuery)).__().br().__()

						.button().attrType(EnumTypeButtonType.SUBMIT).text("Delete Tape by ID").__()

						.p().dynamic(p -> {
							if (model.hasQuery()) {
								if (errorMessage == null) {
									if (addedTapeSuccess) p.text("Tape deleted successfully id = " + idQuery);
								} else {
									p.text("Failed to add tape: " + errorMessage);
								}
							}
						}).__()
					.__()
				.__(); // div
		} catch (Exception e) {
			view = DynamicHtml.view(TapesDeleteHandler::body);
			throw e;
		}
	}

	@Override
	public void requestHandle(HttpExchange he) throws IOException, SQLException {
		try {
			TemplateHeadModel thm = TemplateHeadModel.of("Tape Delete");
			TemplatePageModel tepm = TemplatePageModel.of(view, thm, SelectedPage.Tapes, BodyModel.of(he, null));
			String response = TemplatePage.view.render(tepm);

			he.sendResponseHeaders(HttpURLConnection.HTTP_OK, response.length());
			OutputStream os = he.getResponseBody();
			os.write(response.getBytes());
			os.close();
		} catch (Exception e) {
			view = DynamicHtml.view(TapesDeleteHandler::body);
			throw e;
		}
	}
}
