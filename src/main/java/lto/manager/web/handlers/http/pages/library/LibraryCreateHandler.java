package lto.manager.web.handlers.http.pages.library;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.xmlet.htmlapifaster.Div;
import org.xmlet.htmlapifaster.EnumTypeButtonType;
import org.xmlet.htmlapifaster.EnumTypeInputType;

import com.sun.net.httpserver.HttpExchange;

import lto.manager.common.database.Database;
import lto.manager.common.database.tables.TableTape;
import lto.manager.common.database.tables.records.RecordManufacturer;
import lto.manager.common.database.tables.records.RecordTape;
import lto.manager.common.database.tables.records.RecordTape.RecordTapeFormatType;
import lto.manager.common.database.tables.records.RecordTapeType;
import lto.manager.web.handlers.http.BaseHTTPHandler;
import lto.manager.web.handlers.http.partial.LTOTapeTypeSelect;
import lto.manager.web.handlers.http.templates.TemplatePage.BreadCrumbs;
import lto.manager.web.handlers.http.templates.TemplatePage.SelectedPage;
import lto.manager.web.handlers.http.templates.TemplatePage.TemplatePageModel;
import lto.manager.web.handlers.http.templates.models.BodyModel;
import lto.manager.web.handlers.http.templates.models.HeadModel;
import lto.manager.web.resource.Asset;
import lto.manager.web.resource.CSS;
import lto.manager.web.resource.FormValidator;
import lto.manager.web.resource.FormValidator.ValidatorOptions;
import lto.manager.web.resource.FormValidator.ValidatorStatus;
import lto.manager.web.resource.FormValidator.ValidatorType;
import lto.manager.web.resource.HTML;
import lto.manager.web.resource.Query;

public class LibraryCreateHandler extends BaseHTTPHandler {
	public static final String PATH = "/library/new";
	public static final String NAME = "New Tape";
	private static final String SERIAL = "serial";
	private static final String TAPETYPE = "type";
	private static final String MANU = "manu";
	private static final String BARCODE = "barcode";
	private static final String WORM = "worm";
	private static final String FORMAT = "format";
	private static final String ENCRYPTED = "enc";
	private static final String COMPRESSION = "comp";
	private static final FormValidator barcodeValidator = FormValidator.of(ValidatorType.INPUT_TEXT,
			ValidatorOptions.of().valueExpectedLength(TableTape.MAX_LEN_BARCODE_FORM).valueNotEmpty(), "Bardcode");

	static Void body(Div<?> view, BodyModel model) {
		List<RecordManufacturer> m = null;
		List<RecordTapeType> t = null;
		String er = null;
		boolean s = false;

		final String barcode = model.getQueryNoNull(BARCODE);
		final ValidatorStatus barcodeStatus = barcodeValidator.validate(model.getQueryNoNull(BARCODE), model.hasQuery());
		final String serial = model.getQueryNoNull(SERIAL);
		final String manu = model.getQueryNoNull(MANU);
		final String type = model.getQueryNoNull(TAPETYPE);
		final String worm = model.getQueryNoNull(WORM);
		final String format = model.getQueryNoNull(FORMAT);
		final String encrypted = model.getQueryNoNull(ENCRYPTED);
		final String compression = model.getQueryNoNull(COMPRESSION);

		final boolean isWORM = worm.equals(Query.CHECKED);
		final boolean isEncrypted = encrypted.equals(Query.CHECKED);
		final boolean isCompressed = compression.equals(Query.CHECKED);

		final int manuIndex = manu.equals("") ? -1 : Integer.valueOf(manu);
		final int typeIndex = type.equals("") ? -1 : Integer.valueOf(type);
		final int formatIndex = format.equals("") ? -1 : Integer.valueOf(format);

		try {
			m = Database.getAllTapeManufacturers();
			t = Database.getAllTapeTypes();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		final List<RecordManufacturer> manuList = m;

		if (model.hasQuery()) {
			try {
				RecordTapeFormatType formatType = RecordTapeFormatType.fromInteger(formatIndex);
				var tape = RecordTape.of(null, m.get(manuIndex), t.get(typeIndex), barcode, serial, 0, formatType, null, isWORM, isEncrypted, isCompressed);
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
					.b().attrStyle("width:300px;display:inline-block").text("LTO Tape Type: ").__()
					.select()
						.of(select -> LTOTapeTypeSelect.content(select, "select-type", TAPETYPE, typeIndex))
					.__()

					.br().__()

					.b().attrStyle("width:300px;display:inline-block").text("LTO Manufacturer: ").__()
					.select().attrName(MANU).of(select -> {
						select.option().of(o -> HTML.option(o, manuIndex == -1, true)).text("Select").__();
						int index = 0;
						for (RecordManufacturer item: manuList) {
							final int indexCopy = index;
							select.option()
								.of(sel -> {
									if (indexCopy == manuIndex) {
										sel.of(o -> HTML.option(o, true));
									}
								})
								.attrValue(String.valueOf(index))
								.text(item.getManufacturer())
							.__();
							index++;
						}
					}).__().br().__()

					.b().attrStyle("width:300px;display:inline-block").text("Serial Number: ").__()
					.input().attrType(EnumTypeInputType.TEXT).attrName(SERIAL).attrMaxlength((long) TableTape.MAX_LEN_SERIAL).of(input -> input.attrValue(serial)).__().br().__()

					.b().attrStyle("width:300px;display:inline-block").text("Barcode: ").__()
					.input().attrType(EnumTypeInputType.TEXT).attrName(BARCODE).attrMaxlength((long) TableTape.MAX_LEN_BARCODE_FORM).of(input -> input.attrValue(barcode)).__()
					.input().attrType(EnumTypeInputType.TEXT).attrId("des").attrDisabled(true).__().br().__()

					.b().attrStyle("width:300px;display:inline-block").text("WORM: ").__()
					.input()
						.attrId(WORM)
						.attrOnclick("onSelectType(this)")
						.attrType(EnumTypeInputType.CHECKBOX)
						.attrName(WORM)
						.attrValue(Query.CHECKED)
						.of(input -> HTML.check(input, !worm.equals(Query.EMPTY)))
					.__()
					.br().__()

					.b().attrStyle("width:300px;display:inline-block").text("Encrypted: ").__()
					.input()
						.attrId(ENCRYPTED)
						.attrType(EnumTypeInputType.CHECKBOX)
						.attrName(ENCRYPTED)
						.attrValue(Query.CHECKED)
						.of(input -> HTML.check(input, !encrypted.equals(Query.EMPTY)))
					.__()
					.br().__()

					.b().attrStyle("width:300px;display:inline-block").text("Compression Enabled: ").__()
					.input()
						.attrId(COMPRESSION)
						.attrType(EnumTypeInputType.CHECKBOX)
						.attrName(COMPRESSION)
						.attrValue(Query.CHECKED)
						.of(input -> HTML.check(input, !compression.equals(Query.EMPTY)))
					.__()
					.br().__()

					.b().attrStyle("width:300px;display:inline-block").text("Format: ").__()
					.select().attrName(FORMAT).of(select -> {
						select.option().of(o -> HTML.option(o, formatIndex == -1, true)).text("Select").__();
						int index = 0;
						for (RecordTapeFormatType item: RecordTapeFormatType.values()) {
							final int indexCopy = index;
							select.option()
								.of(sel -> {
									if (indexCopy == formatIndex) {
										sel.of(o -> HTML.option(o, true));
									}
								})
								.attrValue(String.valueOf(index))
								.text(item.toString())
							.__();
							index++;
						}
					}).__().br().__()

					.button().attrClass(CSS.BUTTON).attrType(EnumTypeButtonType.SUBMIT).text("Submit").__()

					.div().of(d -> {
						if (model.hasQuery()) {
							if (!barcodeStatus.statusOK()) {
								d.p().text(barcodeStatus.userMessage()).__();
							}

							if (errorMessage == null) {
								if (addedTapeSuccess) d.p().text("Tape added successfully");
							} else {
								d.p().text("Failed to add tape: " + errorMessage).__();
							}
						}
					}).__()
				.__()
			.__(); // div
		return null;
	}

	@Override
	public void requestHandle(HttpExchange he) throws IOException, SQLException, InterruptedException, ExecutionException {
		HeadModel thm = HeadModel.of(NAME);
		thm.addScript(Asset.JS_ADD_TAPE);
		BreadCrumbs crumbs = new BreadCrumbs().add(LibraryHandler.NAME, LibraryHandler.PATH).add(NAME, PATH);
		TemplatePageModel tpm = TemplatePageModel.of(LibraryCreateHandler::body, null, thm, SelectedPage.Library, BodyModel.of(he, null), crumbs);
		requestHandleCompletePage(he, tpm);
	}
}
