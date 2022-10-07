package lto.manager.web.handlers.tapes;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.sql.SQLException;
import java.util.List;

import org.xmlet.htmlapifaster.EnumBorderType;

import com.sun.net.httpserver.HttpExchange;

import htmlflow.DynamicHtml;
import lto.manager.common.database.Database;
import lto.manager.common.database.tables.TableTape.RecordTape;
import lto.manager.web.handlers.BaseHandler;
import lto.manager.web.handlers.files.FilesHandler;
import lto.manager.web.handlers.templates.TemplateHead.TemplateHeadModel;
import lto.manager.web.handlers.templates.TemplatePage;
import lto.manager.web.handlers.templates.TemplatePage.SelectedPage;
import lto.manager.web.handlers.templates.TemplatePage.TemplatePageModel;
import lto.manager.web.handlers.templates.models.BodyModel;

public class TapesHandler extends BaseHandler {
	public static final String PATH = "/tapes";

	public static DynamicHtml<BodyModel> view = DynamicHtml.view(TapesHandler::body);

	static void body(DynamicHtml<BodyModel> view, BodyModel model) {
		List<RecordTape> tmp = null;
		try {
			tmp = Database.getTapeAtIDRange(0, 100);
		} catch (SQLException e1) {
			e1.printStackTrace();
		}

		final List<RecordTape> tapes = tmp;

		view
			.div()
				.a().attrHref(TapesCreateHandler.PATH).text("Add New Tape").__()
				.table().dynamic(table -> {
					table.attrBorder(EnumBorderType._1).tr()
						.th().text("Tape ID").__()
						.th().text("Barcode").__()
						.th().text("Type").__()
						.th().text("Serial").__()
						.th().text("Manufacturer").__()
					.__();
					for (RecordTape item : tapes) {
						table.tr()
							.td().text(item.getID()).__()
							.td().text(item.getBarcode()).__()
							.td().text(item.getTapeType().getType()).__()
							.td().text(item.getSerial()).__()
							.td().text(item.getManufacturer().getManufacturer()).__()
							.td().a().attrHref(TapesDeleteHandler.PATH + "?" + TapesDeleteHandler.ID + "=" + item.getID()).text("Delete").__().__()
							.td().a().attrHref(FilesHandler.PATH + "?" + FilesHandler.TAPE_ID + "=" + item.getID()).text("Show Files").__()
						.__();
					}
				}).__()
			.__(); // div

	}

	@Override
	public void requestHandle(HttpExchange he) throws IOException {
		try {
			TemplateHeadModel thm = TemplateHeadModel.of("Tapes");
			TemplatePageModel tepm = TemplatePageModel.of(view, thm, SelectedPage.Tapes, BodyModel.of(he, null));
			String response = TemplatePage.view.render(tepm);

			he.sendResponseHeaders(HttpURLConnection.HTTP_OK, response.length());
			OutputStream os = he.getResponseBody();
			os.write(response.getBytes());
			os.close();
		} catch (Exception e) {
			TapesHandler.view = DynamicHtml.view(TapesHandler::body);
			throw e;
		}
	}
}
