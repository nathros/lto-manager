package lto.manager.web.handlers.tapes;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.sql.SQLException;
import java.util.List;

import org.xmlet.htmlapifaster.EnumTypeButtonType;
import org.xmlet.htmlapifaster.EnumTypeInputType;

import com.sun.net.httpserver.HttpExchange;

import htmlflow.DynamicHtml;
import lto.manager.common.database.Database;
import lto.manager.common.database.tables.TableManufacturer.RecordManufacturer;
import lto.manager.common.database.tables.TableTapeType.RecordTapeType;
import lto.manager.web.handlers.BaseHandler;
import lto.manager.web.handlers.templates.TemplateHead.TemplateHeadModel;
import lto.manager.web.handlers.templates.TemplatePage;
import lto.manager.web.handlers.templates.TemplatePage.SelectedPage;
import lto.manager.web.handlers.templates.TemplatePage.TemplatePageModel;
import lto.manager.web.handlers.templates.models.BodyModel;

public class TapesCreateHandler extends BaseHandler {
	public static final String PATH = "/tapes/new";
	public static DynamicHtml<BodyModel> view = DynamicHtml.view(TapesCreateHandler::body);

	private static final String SERIAL = "serial";
	private static final String TAPETYPE = "type";
	private static final String MANU = "manu";
	private static final String BARCODE = "barcode";

	static void body(DynamicHtml<BodyModel> view, BodyModel model) {
		List<RecordManufacturer> m = null;
		List<RecordTapeType> t = null;

		try {
			m = Database.getAllTapeManufacturers();
			t = Database.getAllTapeTypes();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		final List<RecordTapeType> typesList = t;
		final List<RecordManufacturer> manuList = m;

		final String barcode = model.getQueryNoNull(BARCODE);
		final String serial = model.getQueryNoNull(SERIAL);

		view
			.div()
				.form()
					.b().text("LTO Tape Type: ").__()
					.select().attrName(TAPETYPE).dynamic(select -> {
						select.option().attrValue("").attrDisabled(true).attrHidden(true).attrSelected(true).text("Select").__();
						for (RecordTapeType item: typesList) {
							select.option().attrValue(String.valueOf(item.getID())).text(item.getType()).__();
						}
					}).__().br().__()

					.b().text("LTO Manufacturer: ").__()
					.select().attrName(MANU).dynamic(select -> {
						select.option().attrValue("").attrDisabled(true).attrHidden(true).attrSelected(true).text("Select").__();
						for (RecordManufacturer item: manuList) {
							select.option().attrValue(String.valueOf(item.getID())).text(item.getManufacturer()).__();
						}
					}).__().br().__()

					.b().text("Serial Number: ").__()
					.input().attrType(EnumTypeInputType.TEXT).attrName(SERIAL).dynamic(input -> input.attrValue(serial)).__().br().__()

					.b().text("Barcode: ").__()
					.input().attrType(EnumTypeInputType.TEXT).attrName(BARCODE).dynamic(input -> input.attrValue(barcode)).__().br().__()

					.button().attrType(EnumTypeButtonType.SUBMIT).text("Submit").__()
				.__()
			.__(); // div
	}

	@Override
	public void requestHandle(HttpExchange he) throws IOException, SQLException {
		TemplateHeadModel thm = TemplateHeadModel.of("Tapes");
		TemplatePageModel tepm = TemplatePageModel.of(view, thm, SelectedPage.Tapes, BodyModel.of(he, null));
		String response = TemplatePage.view.render(tepm);

		he.sendResponseHeaders(HttpURLConnection.HTTP_OK, response.length());
		OutputStream os = he.getResponseBody();
		os.write(response.getBytes());
		os.close();
	}
}
