package lto.manager.web.handlers.http.ajax.pages.library;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import org.xmlet.htmlapifaster.Div;
import org.xmlet.htmlapifaster.Option;

import com.sun.net.httpserver.HttpExchange;

import lto.manager.common.Util;
import lto.manager.common.database.DBStatus;
import lto.manager.common.database.Database;
import lto.manager.common.database.tables.TableTape;
import lto.manager.common.database.tables.records.RecordManufacturer;
import lto.manager.common.database.tables.records.RecordRole.Permission;
import lto.manager.common.database.tables.records.RecordTape;
import lto.manager.common.database.tables.records.RecordTape.RecordTapeFormatType;
import lto.manager.common.database.tables.records.RecordTapeType;
import lto.manager.web.check.FormDefinition;
import lto.manager.web.check.element.button.ElementIconButton;
import lto.manager.web.check.element.input.ElementInputCheckBox;
import lto.manager.web.check.element.input.ElementInputRadio;
import lto.manager.web.check.element.input.ElementInputRadio.ElementRadioOption;
import lto.manager.web.check.element.input.ElementInputText;
import lto.manager.web.check.element.input.ElementInputTextLTOBarcode;
import lto.manager.web.check.element.select.ElementSelect;
import lto.manager.web.check.element.select.ElementSelect.ElementSelectOption;
import lto.manager.web.handlers.http.BaseHTTPHandler;
import lto.manager.web.handlers.http.pages.library.LibraryHandler;
import lto.manager.web.handlers.http.partial.form.Forms;
import lto.manager.web.handlers.http.templates.TemplateAJAX.TemplateFetcherModel;
import lto.manager.web.handlers.http.templates.models.BodyModel;
import lto.manager.web.resource.Asset;
import lto.manager.web.resource.CSS;
import lto.manager.web.resource.JS;

public class AJAXLibraryCreateTapeForm extends BaseHTTPHandler {
	public static final String PATH = Asset.PATH_AJAX_BASE + "library/new/";
	public static final String NAME = "New Tape";
	public static final String FORM_ID = "form-" + LibraryHandler.MODAL_ID_NEW;

	private static final String NAME_TAPETYPE = "type_n";
	private static final String NAME_MANU = "manu_n";
	private static final String NAME_FORMAT = "format_n";
	private static final String NAME_SERIAL = "serial_n";
	private static final String NAME_BARCODE = "barcode_n";
	private static final String NAME_WORM = "worm_n";
	private static final String NAME_ENCRYPTED = "enc_n";
	private static final String NAME_COMPRESSION = "comp_n";
	private static final String NAME_BARCODE_DES = "barcode_des_n";

	private static final String LABEL_TAPETYPE = "LTO Tape Type";
	private static final String LABEL_MANU = "LTO Manufacturer";
	private static final String LABEL_FORMAT = "Tape Format";
	private static final String LABEL_SERIAL = "Serial Number";
	private static final String LABEL_BARCODE = "Barcode";
	private static final String LABEL_WORM = "WORM";
	private static final String LABEL_ENCRYPTED = "Encrypted";
	private static final String LABEL_COMPRESSION = "Compression Enabled";

	private static FormDefinition formDefinition(BodyModel model) {
		final FormDefinition fd = new FormDefinition(model, AJAXLibraryCreateTapeForm::submit);
		fd.withAJAXPath(PATH).withId(FORM_ID)
				.withOnReplace(JS.libraryChangeTapeType(NAME_BARCODE_DES, NAME_WORM, NAME_TAPETYPE));

		try {
			final List<RecordManufacturer> allDBTapeManufacturers = Database.getAllTapeManufacturers();
			final List<RecordTapeType> allDBTapeTypes = Database.getAllTapeTypes();

			final int selectedManuInt = model.getQueryModel().getInt(NAME_MANU, 0);
			final RecordManufacturer selectedManu = allDBTapeManufacturers.stream()
					.filter(type -> type.getID() == selectedManuInt).findFirst().orElse(RecordManufacturer.of(0, ""));
			final boolean wormChecked = model.getQueryModel().getChecked(NAME_WORM);

			{ // LTO tape type <select>
				final ElementSelect tapeTypesSelect = ElementSelect.of();
				final String selected = model.getQueryModel().getString(NAME_TAPETYPE);
				final int selectedInt = model.getQueryModel().getInt(NAME_TAPETYPE, 0);
				final RecordTapeType tapeType = allDBTapeTypes.stream().filter(t -> t.getID() == selectedInt)
						.findFirst().orElse(RecordTapeType.lazy(0));
				final boolean isHP = selectedManu.isHP();

				tapeTypesSelect.withRequired().withLabel(LABEL_TAPETYPE);
				tapeTypesSelect.withAdditionalClass(CSS.LIBRARY_TYPE_SELECT);
				tapeTypesSelect.withName(NAME_TAPETYPE).withId(NAME_TAPETYPE).withNotEmpty();
				if (wormChecked) {
					final String backgroundWorm = "url('" + Asset.IMG_LTO_COLOURS + "worm.svg')";
					tapeTypesSelect.withStyle("background-image:" + backgroundWorm + ", url('" + Asset.IMG_LTO_COLOURS
							+ (isHP ? tapeType.getColourWORMHP() : tapeType.getColourWORM()) + ".svg')");
				} else {
					tapeTypesSelect.withStyle("background-image:url('" + Asset.IMG_LTO_COLOURS
							+ (isHP ? tapeType.getColourHP() : tapeType.getColour()) + ".svg')");
				}
				tapeTypesSelect.withDisabledDefault(selected).withValue(selected); // Enable blank option
				tapeTypesSelect.withOptions(
						allDBTapeTypes.stream().map(type -> new ElementSelectOption(type.getID().toString(),
								type.getType(), selected, (Option<?> option) -> {
									option.addAttr("data-des", type.getDesignation());
									option.addAttr("data-worm", type.getDesignationWORM());
								})).collect(Collectors.toList()));
				tapeTypesSelect.withOnChangeJS(JS.libraryChangeTapeType(NAME_BARCODE_DES, NAME_WORM, NAME_TAPETYPE));
				fd.withElement(tapeTypesSelect);
			}
			{ // LTO manufacturer <select>
				final ElementSelect tapeManuSelect = ElementSelect.of();
				final String selected = model.getQueryModel().getString(NAME_MANU);
				tapeManuSelect.withAdditionalClass(CSS.LIBRARY_MANUFACTURER_SELECT);
				tapeManuSelect.withStyle("background-image:url('" + Asset.IMG_COMPANY_LOGOS
						+ selectedManu.getManufacturer().toLowerCase() + ".svg')");
				tapeManuSelect.withRequired().withLabel(LABEL_MANU).withNotEmpty();
				tapeManuSelect.withName(NAME_MANU).withId(NAME_MANU);
				tapeManuSelect.withDisabledDefault(selected).withValue(selected); // Enable blank option
				tapeManuSelect.withOnChangeJS(JS.libraryChangeManufacturer(FORM_ID, NAME_TAPETYPE));
				tapeManuSelect.withOptions(allDBTapeManufacturers.stream()
						.map(type -> new ElementSelectOption(type.getID().toString(), type.getManufacturer(), selected))
						.collect(Collectors.toList()));
				fd.withElement(tapeManuSelect);
			}
			{ // Format <input> radio
				final ElementInputRadio tapeFormatRadio = ElementInputRadio.of(NAME_FORMAT);
				tapeFormatRadio.withLabel(LABEL_FORMAT);
				final String selected = model.getQueryModel().getString(NAME_FORMAT,
						RecordTapeFormatType.values()[0].toString());
				tapeFormatRadio.withOptions(Arrays.stream(RecordTapeFormatType.values())
						.filter(format -> format != RecordTapeFormatType.STFS) // FIXME STFS not yet supported
						.map(format -> new ElementRadioOption(format.toString(), format.toString(),
								format.toString().equals(selected)))
						.collect(Collectors.toList()));
				fd.withElement(tapeFormatRadio);
			}
			{ // Serial number <input> text
				final ElementInputText serialInput = ElementInputText.of();
				serialInput.getFormValidator().setMessage("Serial number ", "");
				final String value = model.getQueryModel().getStringNotNull(NAME_SERIAL);
				serialInput.withRequired().withLabel(LABEL_SERIAL).withNotEmpty();
				serialInput.withName(NAME_SERIAL).withId(NAME_SERIAL);
				serialInput.withValue(value);
				serialInput.withMaxLength(Long.valueOf(TableTape.MAX_LEN_SERIAL));
				fd.withElement(serialInput);
			}
			{ // Barcode number <input> text
				final ElementInputTextLTOBarcode barcodeInput = ElementInputTextLTOBarcode.of(NAME_BARCODE_DES);
				barcodeInput.getFormValidator().setMessage("Barcode ", null);
				final String value = model.getQueryModel().getStringNotNull(NAME_BARCODE);
				barcodeInput.withRequired().withLabel(LABEL_BARCODE);
				barcodeInput.withName(NAME_BARCODE).withId(NAME_BARCODE).withNotEmpty();
				barcodeInput.withValue(value);
				barcodeInput.withMinLength(Long.valueOf(TableTape.MAX_LEN_BARCODE_FORM));
				barcodeInput.withMaxLength(Long.valueOf(TableTape.MAX_LEN_BARCODE_FORM));
				barcodeInput.withUppercase();
				barcodeInput.withCustomTextValidator(barcodeValue -> {
					LinkedHashSet<Character> invalidChars = new LinkedHashSet<Character>();
					for (final char cc : barcodeValue.toCharArray()) {
						if (!TableTape.BARCODE_VALID_CHARS.contains(String.valueOf(cc))) {
							invalidChars.add(cc);
						}
					}
					if (invalidChars.size() > 0) {
						final String join = invalidChars.stream().map(c -> String.valueOf(c))
								.collect(Collectors.joining(" "));
						final String first = "Found invalid character" + (join.length() == 1 ? ": " : "s: ");
						Util.throwException(new Exception(
								first + join + "<br>Code 39 barcode only allows: " + TableTape.BARCODE_VALID_CHARS));
					}
				});
				fd.withElement(barcodeInput);
			}
			{ // WORM <input> checkbox
				final ElementInputCheckBox wormCheckbox = ElementInputCheckBox.of(wormChecked);
				wormCheckbox.withLabel(LABEL_WORM);
				wormCheckbox.withName(NAME_WORM).withId(NAME_WORM);
				wormCheckbox.withOnChangeJS(JS.libraryChangeTapeType(NAME_BARCODE_DES, NAME_WORM, NAME_TAPETYPE)
						+ JS.libraryChangeManufacturer(FORM_ID, NAME_TAPETYPE));
				fd.withElement(wormCheckbox);
			}
			{ // Encrypted <input> checkbox
				final boolean checked = model.getQueryModel().getChecked(NAME_ENCRYPTED);
				final ElementInputCheckBox EncCheckbox = ElementInputCheckBox.of(checked);
				EncCheckbox.withLabel(LABEL_ENCRYPTED);
				EncCheckbox.withName(NAME_ENCRYPTED).withId(NAME_ENCRYPTED);
				fd.withElement(EncCheckbox);
			}
			{ // Compression Enabled <input> checkbox
				final boolean checked = model.getQueryModel().getChecked(NAME_COMPRESSION);
				final ElementInputCheckBox compCheckbox = ElementInputCheckBox.of(checked);
				compCheckbox.withLabel(LABEL_COMPRESSION);
				compCheckbox.withName(NAME_COMPRESSION).withId(NAME_COMPRESSION);
				fd.withElement(compCheckbox);
			}
			{ // Add buttons at end
				final ElementIconButton submit = ElementIconButton.ofTypeAdd();
				submit.asSubmit().withOnClickJS(JS.formSubmit(FORM_ID));
				fd.withButton(submit);

				final ElementIconButton cancel = ElementIconButton.ofTypeCancel();
				cancel.withOnClickJS(JS.hideModal(LibraryHandler.MODAL_ID_NEW));
				fd.withButton(cancel);
			}
		} catch (SQLException e) {
			Util.throwException(e);
		}
		return fd;
	}

	public static Void submit(final FormDefinition fd, BodyModel model) {
		try {
			final var qm = model.getQueryModel();

			final RecordManufacturer manu = RecordManufacturer.lazy(qm.getInt(NAME_MANU));
			final RecordTapeType type = RecordTapeType.lazy(qm.getInt(NAME_TAPETYPE));
			final RecordTapeFormatType format = RecordTapeFormatType.valueOf(qm.getString(NAME_FORMAT));
			final String serial = qm.getString(NAME_SERIAL);
			final String barcode = qm.getString(NAME_BARCODE);
			final boolean worm = qm.getChecked(NAME_WORM);
			final boolean enc = qm.getChecked(NAME_ENCRYPTED);
			final boolean cmp = qm.getChecked(NAME_COMPRESSION);
			final RecordTape newTape = RecordTape.of(null, manu, type, barcode, serial, 0, format, null, worm, enc,
					cmp);
			final DBStatus status = Database.addTape(newTape);
			if (!status.success()) {
				status.rethrow();
			}
		} catch (Exception e) {
			Util.throwException(e);
		}
		return null;
	}

	public static Void content(Div<?> view, BodyModel model) {
		view.of(d -> Forms.content(d, formDefinition(model)));
		return null;
	}

	@Override
	public Permission getHandlePermission() {
		// TODO add permission
		return null;
	}

	@Override
	public void requestHandle(HttpExchange he, BodyModel bm)
			throws IOException, SQLException, InterruptedException, ExecutionException {
		requestHandleCompleteFetcher(he, new TemplateFetcherModel(AJAXLibraryCreateTapeForm::content, bm));
	}
}
