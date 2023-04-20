package lto.manager.web.handlers.http.tapes;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import org.xmlet.htmlapifaster.Div;
import org.xmlet.htmlapifaster.EnumTypeButtonType;
import org.xmlet.htmlapifaster.EnumTypeInputType;

import com.sun.net.httpserver.HttpExchange;

import lto.manager.common.database.Database;
import lto.manager.common.database.tables.records.RecordManufacturer;
import lto.manager.common.database.tables.records.RecordTape;
import lto.manager.common.database.tables.records.RecordTapeType;
import lto.manager.web.handlers.http.BaseHTTPHandler;
import lto.manager.web.handlers.http.templates.TemplatePage.SelectedPage;
import lto.manager.web.handlers.http.templates.TemplatePage.TemplatePageModel;
import lto.manager.web.handlers.http.templates.models.BodyModel;
import lto.manager.web.handlers.http.templates.models.HeadModel;
import lto.manager.web.resource.Asset;
import lto.manager.web.resource.CSS;

public class TapesCreateHandler extends BaseHTTPHandler {
	public static final String PATH = "/tapes/new";
	private static final String SERIAL = "serial";
	private static final String TAPETYPE = "type";
	private static final String MANU = "manu";
	private static final String BARCODE = "barcode";
	private static final String WORM = "worm";

	static Void body(Div<?> view, BodyModel model) {
		List<RecordManufacturer> m = null;
		List<RecordTapeType> t = null;
		String er = null;
		boolean s = false;

		final String barcode = model.getQueryNoNull(BARCODE);
		final String serial = model.getQueryNoNull(SERIAL);
		final String manu = model.getQueryNoNull(MANU);
		final String type = model.getQueryNoNull(TAPETYPE);
		final String worm = model.getQueryNoNull(WORM);

		final int manuIndex = manu.equals("") ? -1 : Integer.valueOf(manu);
		final int typeIndex = type.equals("") ? -1 : Integer.valueOf(type);

		try {
			m = Database.getAllTapeManufacturers();
			t = Database.getAllTapeTypes();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		final List<RecordTapeType> typesList = t;
		final List<RecordManufacturer> manuList = m;

		if (model.hasQuery()) {
			try {
				var tape = RecordTape.of(null, m.get(manuIndex), t.get(typeIndex), barcode, serial, 0, null);
				Database.addTape(tape);
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
					.b().attrStyle("width:150px;display:inline-block").text("LTO Tape Type: ").__()
					.select().attrId("select-type").attrName(TAPETYPE).of(select -> {
						select.attrOnchange("onSelectType()")
							.option().attrValue("").attrSelected(typeIndex == -1).attrDisabled(true).text("Select").__();
						int index = 0;
						for (RecordTapeType item: typesList) {
							final int indexCopy = index;
							select.option()
								.of(sel -> {
									if (indexCopy == typeIndex) {
										sel.attrSelected(true);
									}
								})
								.addAttr("data-des", item.getDesignation())
								.addAttr("data-worm", item.getDesignationWORM())
								.attrValue(String.valueOf(index))
								.text(item.getType())
							.__();
							index++;
						}
					}).__().br().__()

					.b().attrStyle("width:150px;display:inline-block").text("LTO Manufacturer: ").__()
					.select().attrName(MANU).of(select -> {
						select.option().attrValue("").attrSelected(manuIndex == -1).attrDisabled(true).text("Select").__();
						int index = 0;
						for (RecordManufacturer item: manuList) {
							final int indexCopy = index;
							select.option()
								.of(sel -> {
									if (indexCopy == manuIndex) {
										sel.attrSelected(true);
									}
								})
								.attrValue(String.valueOf(index))
								.text(item.getManufacturer())
							.__();
							index++;
						}
					}).__().br().__()

					.b().attrStyle("width:150px;display:inline-block").text("Serial Number: ").__()
					.input().attrType(EnumTypeInputType.TEXT).attrName(SERIAL).of(input -> input.attrValue(serial)).__().br().__()

					.b().attrStyle("width:150px;display:inline-block").text("Barcode: ").__()
					.input().attrType(EnumTypeInputType.TEXT).attrName(BARCODE).of(input -> input.attrValue(barcode)).__()
					.input().attrType(EnumTypeInputType.TEXT).attrId("des").__().br().__()

					.b().attrStyle("width:150px;display:inline-block").text("WORM: ").__()
					.input().attrId(WORM).attrOnclick("onSelectType(this)").attrType(EnumTypeInputType.CHECKBOX).attrName(WORM).of(input -> input.attrValue(worm)).__().br().__()

					.button().attrClass(CSS.BUTTON).attrType(EnumTypeButtonType.SUBMIT).text("Submit").__()

					.p().of(p -> {
						if (model.hasQuery()) {
							if (errorMessage == null) {
								if (addedTapeSuccess) p.text("Tape added successfully");
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
		HeadModel thm = HeadModel.of("Tapes");
		thm.AddScript(Asset.JS_ADD_TAPE);
		TemplatePageModel tpm = TemplatePageModel.of(TapesCreateHandler::body, thm, SelectedPage.Tapes, BodyModel.of(he, null));
		requestHandleCompletePage(he, tpm);
	}
}
