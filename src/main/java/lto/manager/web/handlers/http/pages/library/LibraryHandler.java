package lto.manager.web.handlers.http.pages.library;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.xmlet.htmlapifaster.Div;
import org.xmlet.htmlapifaster.EnumTypeInputType;

import com.sun.net.httpserver.HttpExchange;

import lto.manager.common.database.Database;
import lto.manager.common.database.tables.records.RecordTape;
import lto.manager.web.handlers.http.BaseHTTPHandler;
import lto.manager.web.handlers.http.pages.files.FilesHandler;
import lto.manager.web.handlers.http.templates.TemplatePage.BreadCrumbs;
import lto.manager.web.handlers.http.templates.TemplatePage.SelectedPage;
import lto.manager.web.handlers.http.templates.TemplatePage.TemplatePageModel;
import lto.manager.web.handlers.http.templates.models.BodyModel;
import lto.manager.web.handlers.http.templates.models.HeadModel;
import lto.manager.web.resource.CSS;
import lto.manager.web.resource.JS;

public class LibraryHandler extends BaseHTTPHandler {
	public static final String PATH = "/library/";
	public static final String NAME = "Library";

	private static final String TABLE_ID = "tab";

	static Void body(Div<?> view, BodyModel model) {
		List<RecordTape> tmp = null;
		try {
			tmp = Database.getAllTapes();
		} catch (SQLException e1) {
			e1.printStackTrace();
		}

		final List<RecordTape> tapes = tmp;

		view
			.div()
				.table().attrId(TABLE_ID).attrClass(CSS.TABLE).of(table -> {
					table.tr()
						.th()
							.div()
								.text("ID")
								.span()
									.attrClass(CSS.TABLE_SORT)
									.attrOnclick(JS.tableSort(TABLE_ID))
								.__()
							.__()
							.input()
								.attrSize((long) 1)
								.attrType(EnumTypeInputType.SEARCH)
								.attrOninput(JS.tableFilter(TABLE_ID))
							.__()
						.__()
						.th()
							.div()
								.text("Barcode")
								.span()
									.attrClass(CSS.TABLE_SORT)
									.attrOnclick(JS.tableSort(TABLE_ID))
								.__()
							.__()
							.input()
								.attrSize((long) 1)
								.attrType(EnumTypeInputType.SEARCH)
								.attrOninput(JS.tableFilter(TABLE_ID))
							.__()
						.__()
						.th()
							.div()
								.text("Type")
								.span()
									.attrClass(CSS.TABLE_SORT)
									.attrOnclick(JS.tableSort(TABLE_ID))
								.__()
							.__()
							.input()
								.attrSize((long) 1)
								.attrType(EnumTypeInputType.SEARCH)
								.attrOninput(JS.tableFilter(TABLE_ID))
							.__()
						.__()
						.th()
							.div()
								.text("Serial")
								.span()
									.attrClass(CSS.TABLE_SORT)
									.attrOnclick(JS.tableSort(TABLE_ID))
								.__()
							.__()
							.input()
								.attrSize((long) 1)
								.attrType(EnumTypeInputType.SEARCH)
								.attrOninput(JS.tableFilter(TABLE_ID))
							.__()
						.__()
						.th()
							.div()
								.text("Manufacturer")
								.span()
									.attrClass(CSS.TABLE_SORT)
									.attrOnclick(JS.tableSort(TABLE_ID))
								.__()
							.__()
							.input()
								.attrSize((long) 1)
								.attrType(EnumTypeInputType.SEARCH)
								.attrOninput(JS.tableFilter(TABLE_ID))
							.__()
						.__()
						.th()
							.div()
								.text("Format")
								.span()
									.attrClass(CSS.TABLE_SORT)
									.attrOnclick(JS.tableSort(TABLE_ID))
								.__()
							.__()
							.input()
								.attrSize((long) 1)
								.attrType(EnumTypeInputType.SEARCH)
								.attrOninput(JS.tableFilter(TABLE_ID))
							.__()
						.__()
						.th()
							.div()
								.text("Size (GB)")
								.span()
									.attrClass(CSS.TABLE_SORT)
									.attrOnclick(JS.tableSort(TABLE_ID))
								.__()
							.__()
							.input()
								.attrSize((long) 1)
								.attrType(EnumTypeInputType.SEARCH)
								.attrOninput(JS.tableFilter(TABLE_ID))
							.__()
						.__()
						.th()
							.div()
								.text("Space Used (GB)")
								.span()
									.attrClass(CSS.TABLE_SORT)
									.attrOnclick(JS.tableSort(TABLE_ID))
								.__()
							.__()
							.input()
								.attrSize((long) 1)
								.attrType(EnumTypeInputType.SEARCH)
								.attrOninput(JS.tableFilter(TABLE_ID))
							.__()
						.__()
						.th()
							.div()
								.text("WORM")
								.span()
									.attrClass(CSS.TABLE_SORT)
									.attrOnclick(JS.tableSort(TABLE_ID))
								.__()
							.__()
							.input()
								.attrSize((long) 1)
								.attrType(EnumTypeInputType.SEARCH)
								.attrOninput(JS.tableFilter(TABLE_ID))
							.__()
						.__()
						.th()
							.div()
								.text("Encrypted")
								.span()
									.attrClass(CSS.TABLE_SORT)
									.attrOnclick(JS.tableSort(TABLE_ID))
								.__()
							.__()
							.input()
								.attrSize((long) 1)
								.attrType(EnumTypeInputType.SEARCH)
								.attrOninput(JS.tableFilter(TABLE_ID))
							.__()
						.__()
						.th()
							.div()
								.text("Compressed")
								.span()
									.attrClass(CSS.TABLE_SORT)
									.attrOnclick(JS.tableSort(TABLE_ID))
								.__()
							.__()
							.input()
								.attrSize((long) 1)
								.attrType(EnumTypeInputType.SEARCH)
								.attrOninput(JS.tableFilter(TABLE_ID))
							.__()
						.__()
						.th().text("Action").__()
					.__();
					for (RecordTape item : tapes) {
						table.tr()
							.td().text(item.getID()).__()
							.td().text(item.getBarcode()).__()
							.td().text(item.getTapeType().getType()).__()
							.td().text(item.getSerial()).__()
							.td().text(item.getManufacturer().getManufacturer()).__()
							.td().text(item.getFormat().name()).__()
							.td().text((int)item.getTotalSpaceGB()).__()
							.td().text((int)item.getUsedSpaceGB()).__()
							.td().text(item.getIsWORM()).__()
							.td().text(item.getIsEncrypted()).__()
							.td().text(item.getIsCompressed()).__()
							.td()
								.div().attrClass(CSS.TABLE_ACTIONS)
									.a()
										.attrClass(CSS.TOOLTIP + CSS.ICON_EYE)
										.attrHref(FilesHandler.PATH + "?" + FilesHandler.TAPE_ID + "=" + item.getID())
										.em().text("Show Files").__()
									.__()
									.a()
										.attrClass(CSS.BACKGROUND_ERROR_BEFORE + CSS.TOOLTIP + CSS.ICON_RUBBISH)
										.attrHref(LibraryDeleteHandler.PATH + "?" + LibraryDeleteHandler.ID + "=" + item.getID())
										.attrOnclick(JS.confirmToastA("Are you sure?"))
										.em().text("Delete").__()
									.__()
								.__() // div TABLE_ACTIONS
							.__() // td
						.__(); // tr
					}
				}).__()
			.__(); // div
		return null;
	}

	@Override
	public void requestHandle(HttpExchange he) throws IOException, InterruptedException, ExecutionException {
		HeadModel thm = HeadModel.of(NAME);
		BreadCrumbs crumbs = new BreadCrumbs().add(NAME, PATH);
		TemplatePageModel tpm = TemplatePageModel.of(LibraryHandler::body, LibraryHandler::header, thm, SelectedPage.Library, BodyModel.of(he, null), crumbs);
		requestHandleCompletePage(he, tpm);
	}

	static Void header(Div<?> view, BodyModel model) {
		view
			.div()
				.attrClass(CSS.HEADER_ITEM + CSS.ICON_PRINTER)
				.ul().attrClass(CSS.MENU_LIST)
					.li()
						.attrClass(CSS.HEADER_LABEL_TOP)
						.text("Tools")
					.__()
					.li()
						.a()
							.attrHref(LibraryGenerateBarcodeHandler.PATH)
							.text("Generate LTO Label")
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
							.attrStyle("user-select:none")
							.attrOnclick(JS.tableFilterShow(TABLE_ID))
							.text("Toggle")
						.__()
					.__()
				.__()
			.__()
			.div()
				.attrClass(CSS.HEADER_ITEM + CSS.ICON_PLUS_SQUARE)
				.ul().attrClass(CSS.MENU_LIST)
					.li()
						.attrClass(CSS.HEADER_LABEL_TOP)
						.text("New")
					.__()
					.li()
						.a()
							.attrHref(LibraryCreateHandler.PATH)
							.text("Add New Tape")
						.__()
					.__()
				.__() // ul
			.__(); // div
		return null;
	}
}
