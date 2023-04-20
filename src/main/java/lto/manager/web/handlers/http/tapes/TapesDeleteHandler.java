package lto.manager.web.handlers.http.tapes;

import java.io.IOException;
import java.sql.SQLException;

import org.xmlet.htmlapifaster.Div;
import org.xmlet.htmlapifaster.EnumTypeButtonType;
import org.xmlet.htmlapifaster.EnumTypeInputType;

import com.sun.net.httpserver.HttpExchange;

import lto.manager.common.database.Database;
import lto.manager.web.handlers.http.BaseHTTPHandler;
import lto.manager.web.handlers.http.templates.TemplatePage.SelectedPage;
import lto.manager.web.handlers.http.templates.TemplatePage.TemplatePageModel;
import lto.manager.web.handlers.http.templates.models.BodyModel;
import lto.manager.web.handlers.http.templates.models.HeadModel;

public class TapesDeleteHandler extends BaseHTTPHandler {
	public static final String PATH = "/tapes/delete";
	public static final String ID = "id";

	static Void body(Div<?> view, BodyModel model) {
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

		view
			.div()
				.form()
					.b().text("id: ").__()
					.input().attrType(EnumTypeInputType.TEXT).attrName(ID).of(input -> input.attrValue(idQuery)).__().br().__()

					.button().attrType(EnumTypeButtonType.SUBMIT).text("Delete Tape by ID").__()

					.p().of(p -> {
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
		return null;
	}

	@Override
	public void requestHandle(HttpExchange he) throws IOException, SQLException {
		HeadModel thm = HeadModel.of("Tape Delete");
		TemplatePageModel tpm = TemplatePageModel.of(TapesDeleteHandler::body, thm, SelectedPage.Tapes, BodyModel.of(he, null));
		requestHandleCompletePage(he, tpm);
	}
}
