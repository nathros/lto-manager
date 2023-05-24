package lto.manager.web.handlers.http.pages.tapes;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.xmlet.htmlapifaster.Div;
import org.xmlet.htmlapifaster.EnumBorderType;

import com.sun.net.httpserver.HttpExchange;

import lto.manager.common.database.Database;
import lto.manager.common.database.tables.records.RecordTape;
import lto.manager.web.handlers.http.BaseHTTPHandler;
import lto.manager.web.handlers.http.pages.files.FilesHandler;
import lto.manager.web.handlers.http.templates.TemplatePage.SelectedPage;
import lto.manager.web.handlers.http.templates.TemplatePage.TemplatePageModel;
import lto.manager.web.handlers.http.templates.models.BodyModel;
import lto.manager.web.handlers.http.templates.models.HeadModel;
import lto.manager.web.resource.CSS;

public class TapesHandler extends BaseHTTPHandler {
	public static final String PATH = "/tapes";

	static Void body(Div<?> view, BodyModel model) {
		List<RecordTape> tmp = null;
		try {
			tmp = Database.getTapeAtIDRange(0, 100);
		} catch (SQLException e1) {
			e1.printStackTrace();
		}

		final List<RecordTape> tapes = tmp;

		view
			.div()
				.a().attrClass(CSS.BUTTON).attrHref(TapesCreateHandler.PATH).text("Add New Tape").__()
				.table().of(table -> {
					table.attrBorder(EnumBorderType._1).tr()
						.th().text("Tape ID").__()
						.th().text("Barcode").__()
						.th().text("Type").__()
						.th().text("Serial").__()
						.th().text("Manufacturer").__()
						.th().text("Size (GB)").__()
						.th().text("Space Used (GB)").__()
						.th().text("Action").__()
					.__();
					for (RecordTape item : tapes) {
						table.tr()
							.td().text(item.getID()).__()
							.td().text(item.getBarcode()).__()
							.td().text(item.getTapeType().getType()).__()
							.td().text(item.getSerial()).__()
							.td().text(item.getManufacturer().getManufacturer()).__()
							.td().text((int)item.getTotalSpaceGB()).__()
							.td().text((int)item.getUsedSpaceGB()).__()
							.td()
								.a().attrClass(CSS.BUTTON + CSS.BACKGROUND_CAUTION).attrHref(TapesDeleteHandler.PATH + "?" + TapesDeleteHandler.ID + "=" + item.getID()).text("Delete").__()
								.a().attrClass(CSS.BUTTON).attrHref(FilesHandler.PATH + "?" + FilesHandler.TAPE_ID + "=" + item.getID()).text("Show Files")
							.__()
						.__();
					}
				}).__()
			.__(); // div
		return null;
	}

	@Override
	public void requestHandle(HttpExchange he) throws IOException, InterruptedException, ExecutionException {
		HeadModel thm = HeadModel.of("Tapes");
		TemplatePageModel tpm = TemplatePageModel.of(TapesHandler::body, thm, SelectedPage.Tapes, BodyModel.of(he, null));
		requestHandleCompletePage(he, tpm);
	}
}
