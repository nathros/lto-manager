package lto.manager.web.handlers.http.ajax.pages.library;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import org.xmlet.htmlapifaster.Div;
import org.xmlet.htmlapifaster.Option;

import com.sun.net.httpserver.HttpExchange;

import lto.manager.common.database.Database;
import lto.manager.common.database.tables.TableTape;
import lto.manager.common.database.tables.records.RecordManufacturer;
import lto.manager.common.database.tables.records.RecordRole.Permission;
import lto.manager.common.database.tables.records.RecordTape.RecordTapeFormatType;
import lto.manager.common.database.tables.records.RecordTapeType;
import lto.manager.web.check.FormDefinition;
import lto.manager.web.check.element.ElementInputCheckBox;
import lto.manager.web.check.element.ElementInputText;
import lto.manager.web.check.element.ElementSelect;
import lto.manager.web.check.element.ElementSelect.ElementSelectOption;
import lto.manager.web.check.element.button.ElementIconButton;
import lto.manager.web.handlers.http.BaseHTTPHandler;
import lto.manager.web.handlers.http.pages.library.LibraryHandler;
import lto.manager.web.handlers.http.partial.form.Forms;
import lto.manager.web.handlers.http.templates.TemplateAJAX.TemplateFetcherModel;
import lto.manager.web.handlers.http.templates.models.BodyModel;
import lto.manager.web.resource.Asset;
import lto.manager.web.resource.JS;

public class AJAXLibraryCreateTapeForm extends BaseHTTPHandler {
	public static final String PATH = Asset.PATH_AJAX_BASE + "library/new/";
	public static final String NAME = "New Tape";

	private static final String NAME_TAPETYPE = "type";
	private static final String NAME_MANU = "manu";
	private static final String NAME_FORMAT = "format";
	private static final String NAME_SERIAL = "serial";
	private static final String NAME_BARCODE = "barcode";
	private static final String NAME_WORM = "worm";
	private static final String NAME_ENCRYPTED = "enc";
	private static final String NAME_COMPRESSION = "comp";

	private static FormDefinition formDefinition(BodyModel model) {
		final FormDefinition fd = new FormDefinition();
		fd.withAJAXPath(PATH);

		try {
			final List<RecordManufacturer> allDBTapeManufacturers = Database.getAllTapeManufacturers();
			final List<RecordTapeType> allDBTapeTypes = Database.getAllTapeTypes();

			{ // LTO tape type <select>
				final ElementSelect tapeTypesSelect = ElementSelect.of();
				final String selected = model.getQueryModel().getString(NAME_TAPETYPE);
				tapeTypesSelect.withLabel("LTO Tape Type:");
				tapeTypesSelect.withName(NAME_TAPETYPE).withId(NAME_TAPETYPE);
				tapeTypesSelect.withDisabledDefault(selected); // Enable blank option
				tapeTypesSelect.withOptions(
						allDBTapeTypes.stream().map(type -> new ElementSelectOption(type.getID().toString(),
								type.getType(), selected, (Option<?> option) -> {
									option.addAttr("data-des", type.getDesignation());
									option.addAttr("data-worm", type.getDesignationWORM());
								})).collect(Collectors.toList()));
				fd.withElement(tapeTypesSelect);
			}
			{ // LTO manufacturer <select>
				final ElementSelect tapeManuSelect = ElementSelect.of();
				final String selected = model.getQueryModel().getString(NAME_MANU);
				tapeManuSelect.withLabel("LTO Manufacturer:");
				tapeManuSelect.withName(NAME_MANU).withId(NAME_MANU);
				tapeManuSelect.withDisabledDefault(selected); // Enable blank option
				tapeManuSelect.withOptions(allDBTapeManufacturers.stream()
						.map(type -> new ElementSelectOption(type.getID().toString(), type.getManufacturer(), selected))
						.collect(Collectors.toList()));
				fd.withElement(tapeManuSelect);
			}
			{ // Format <select>
				final ElementSelect tapeFormatSelect = ElementSelect.of();
				final String selected = model.getQueryModel().getString(NAME_FORMAT);
				tapeFormatSelect.withLabel("Tape Format:");
				tapeFormatSelect.withName(NAME_FORMAT).withId(NAME_FORMAT);
				tapeFormatSelect.withDisabledDefault(selected); // Enable blank option
				tapeFormatSelect.withOptions(Arrays.stream(RecordTapeFormatType.values())
						.map(format -> new ElementSelectOption(format.toString(), format.toString(), selected))
						.collect(Collectors.toList()));
				fd.withElement(tapeFormatSelect);
			}
			{ // Serial number <input> text
				final ElementInputText serialInput = ElementInputText.of();
				serialInput.getFormValidator().setMessage("Serial number ", "");
				final String value = model.getQueryModel().getStringNotNull(NAME_SERIAL);
				serialInput.withLabel("Serial Number:");
				serialInput.withName(NAME_SERIAL).withId(NAME_SERIAL);
				serialInput.withValue(value);
				serialInput.withMaxLength(Long.valueOf(TableTape.MAX_LEN_SERIAL));
				serialInput.withMaxLength(Long.valueOf(3));
				fd.withElement(serialInput);
			}
			{ // Barcode number <input> text
				final ElementInputText barcodeInput = ElementInputText.of();
				final String value = model.getQueryModel().getStringNotNull(NAME_BARCODE);
				barcodeInput.withLabel("Barcode:");
				barcodeInput.withName(NAME_BARCODE).withId(NAME_BARCODE);
				barcodeInput.withValue(value);
				barcodeInput.withMaxLength(Long.valueOf(TableTape.MAX_LEN_BARCODE_FORM));
				fd.withElement(barcodeInput);
			}
			{ // WORM <input> checkbox
				final boolean checked = model.getQueryModel().getChecked(NAME_WORM);
				final ElementInputCheckBox wormCheckbox = ElementInputCheckBox.of(checked);
				wormCheckbox.withLabel("WORM:");
				wormCheckbox.withName(NAME_WORM).withId(NAME_WORM);
				fd.withElement(wormCheckbox);
			}
			{ // Encrypted <input> checkbox
				final boolean checked = model.getQueryModel().getChecked(NAME_ENCRYPTED);
				final ElementInputCheckBox EncCheckbox = ElementInputCheckBox.of(checked);
				EncCheckbox.withLabel("Encrypted:");
				EncCheckbox.withName(NAME_ENCRYPTED).withId(NAME_ENCRYPTED);
				fd.withElement(EncCheckbox);
			}
			{ // Compression Enabled <input> checkbox
				final boolean checked = model.getQueryModel().getChecked(NAME_COMPRESSION);
				final ElementInputCheckBox compCheckbox = ElementInputCheckBox.of(checked);
				compCheckbox.withLabel("Compression Enabled:");
				compCheckbox.withName(NAME_COMPRESSION).withId(NAME_COMPRESSION);
				fd.withElement(compCheckbox);
			}
			{ // Add buttons at end
				final ElementIconButton submit = ElementIconButton.ofTypeAdd();
				submit.withOnClickJS("alert('aaa')");
				fd.withButton(submit);

				final ElementIconButton cancel = ElementIconButton.ofTypeCancel();
				cancel.withOnClickJS(JS.hideModal(LibraryHandler.MODAL_ID));
				fd.withButton(cancel);
			}

		} catch (SQLException e) {
			e.printStackTrace(); // TODO add startup error
		}

		return fd;
	}

	public static Void content(Div<?> view, BodyModel model) {
		view.of(d -> Forms.content(d, formDefinition(model), model)); // FIXME static formDefinition
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
