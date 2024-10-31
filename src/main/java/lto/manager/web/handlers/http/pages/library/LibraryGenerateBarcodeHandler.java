package lto.manager.web.handlers.http.pages.library;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.xmlet.htmlapifaster.Div;
import org.xmlet.htmlapifaster.EnumTypeButtonType;
import org.xmlet.htmlapifaster.EnumTypeInputType;

import com.sun.net.httpserver.HttpExchange;

import lto.manager.common.database.Database;
import lto.manager.common.database.tables.TableTape;
import lto.manager.common.database.tables.records.RecordLabelPreset;
import lto.manager.common.database.tables.records.RecordRole.Permission;
import lto.manager.common.database.tables.records.RecordTapeType;
import lto.manager.web.handlers.http.BaseHTTPHandler;
import lto.manager.web.handlers.http.ajax.labelgenerator.AJAXGenerateLTOLabelPDF;
import lto.manager.web.handlers.http.ajax.labelgenerator.AJAXGenerateLTOLabelSVG;
import lto.manager.web.handlers.http.ajax.labelgenerator.LTOColourThemeMap;
import lto.manager.web.handlers.http.ajax.labelgenerator.LTOLabelEnum;
import lto.manager.web.handlers.http.ajax.labelgenerator.LTOLabelEnum.LTOLabelColourSettings;
import lto.manager.web.handlers.http.ajax.labelgenerator.LTOLabelEnum.LTOLabelTypeSettings;
import lto.manager.web.handlers.http.ajax.labelgenerator.LTOLabelOptions;
import lto.manager.web.handlers.http.partial.inlinemessage.InlineMessage;
import lto.manager.web.handlers.http.partial.modal.Modal;
import lto.manager.web.handlers.http.partial.modal.ModalOptions;
import lto.manager.web.handlers.http.templates.TemplatePage.BreadCrumbs;
import lto.manager.web.handlers.http.templates.TemplatePage.SelectedPage;
import lto.manager.web.handlers.http.templates.TemplatePage.TemplatePageModel;
import lto.manager.web.handlers.http.templates.models.BodyModel;
import lto.manager.web.handlers.http.templates.models.HeadModel;
import lto.manager.web.resource.Asset;
import lto.manager.web.resource.CSS;
import lto.manager.web.resource.HTML;
import lto.manager.web.resource.JS;

public class LibraryGenerateBarcodeHandler extends BaseHTTPHandler {
	public static final String PATH = "/library/generate/";
	public static final String NAME = "Generate New Label";

	private static final String MODAL_ID = "modal-preset";

	static Void body(Div<?> view, BodyModel model) {
		final List<RecordTapeType> tapeTypes = new ArrayList<RecordTapeType>();
		final LTOColourThemeMap colourTheme = LTOColourThemeMap.of();
		try {
			tapeTypes.addAll(Database.getAllTapeTypes());
		} catch (Exception e) {}

		// FIXME enter a bad value in input - click show details which will refresh AJAX
		view.div().attrClass(CSS.COMMON_CONTAINER_NO_ALIGN)
			.form()
				.attrId("barcode-form")
				.attrTarget(HTML.TARGET_BLANK)
				.attrAction(LibraryGenerateBarcodeHTMLHandler.PATH)
				.div()
					.attrClass(CSS.FORMS_CONTAINER)

					.b().text("Tape Type:").__()
					.select()
						.attrName(LTOLabelOptions.QUERY_MEDIA)
						.of(select -> {
							for (final var type : LTOLabelTypeSettings.values()) {
								select.option()
									.attrValue(type.name())
									.of(s -> HTML.option(s, type == LTOLabelTypeSettings.Gen5))
									.text(LTOLabelEnum.getTypeDescription(type))
								.__();
							}
						})
					.__()

					.b().text("Prefix:").__()
					.input()
						.attrType(EnumTypeInputType.TEXT)
						.attrName(LTOLabelOptions.QUERY_PREFIX)
						.attrMaxlength((long) TableTape.MAX_LEN_BARCODE_FORM)
						.attrOninput(JS.INPUT_UPPERCASE)
						.attrValue("P")
					.__()

					.b().text("Postfix:").__()
					.input()
						.attrType(EnumTypeInputType.TEXT)
						.attrName(LTOLabelOptions.QUERY_POSTFIX)
						.attrMaxlength((long) TableTape.MAX_LEN_BARCODE_FORM)
						.attrOninput(JS.INPUT_UPPERCASE)
						.attrValue("S")
					.__()

					.b().text("Start Index:").__()
					.input()
						.attrType(EnumTypeInputType.NUMBER)
						.attrName(LTOLabelOptions.QUERY_START_INDEX)
						.attrStep("1")
						.attrMin(String.valueOf(LTOLabelOptions.START_INDEX_MIN))
						.attrMax(String.valueOf(LTOLabelOptions.START_INDEX_MAX))
						.attrValue(String.valueOf(LTOLabelOptions.START_INDEX_DEFAULT))
					.__()

					.b().text("Quantity:").__()
					.input()
						.attrType(EnumTypeInputType.NUMBER)
						.attrName(LTOLabelOptions.QUERY_QUANTITY)
						.attrStep("1")
						.attrMin(String.valueOf(LTOLabelOptions.QUANTITY_MIN))
						.attrMax(String.valueOf(LTOLabelOptions.QUANTITY_MAX))
						.attrValue(String.valueOf(LTOLabelOptions.QUANTITY_DEFAULT))
					.__()

					.b().text("Colour Settings:").__()
					.select()
						.attrName(LTOLabelOptions.QUERY_COLOURS)
						.option()
							.attrValue(LTOLabelColourSettings.All.name())
							.text("Full colour")
						.__()
						.option()
							.attrValue(LTOLabelColourSettings.Numbers.name())
							.text("Numbers only")
						.__()
						.option()
							.attrValue(LTOLabelColourSettings.None.name())
							.text("None")
						.__()
					.__()

					.b().text("Colour Theme:").__()
					.select()
						.attrName(LTOLabelOptions.QUERY_THEME)
						.of(select -> {
							final var themeNames = colourTheme.getThemeNames();
							for (final String name : themeNames) {
								select.option()
									.attrValue(name)
									.text(name)
								.__();
							}
						})
					.__()

					.b().text("Label Border Radius:").__()
					.input()
						.attrType(EnumTypeInputType.NUMBER)
						.attrName(LTOLabelOptions.QUERY_BORDER_RADIUS_LABEL)
						.attrStep("1")
						.attrMin(String.valueOf(LTOLabelOptions.BORDER_RADIUS_LABEL_MIN))
						.attrMax(String.valueOf(LTOLabelOptions.BORDER_RADIUS_LABEL_MAX))
						.attrValue(String.valueOf(LTOLabelOptions.BORDER_RADIUS_LABEL_DEFAULT))
					.__()

					.b().text("Label Border Width:").__()
					.input()
						.attrType(EnumTypeInputType.NUMBER)
						.attrName(LTOLabelOptions.QUERY_BORDER_STROKE_LABEL)
						.attrStep("0.1")
						.attrMin(String.valueOf(LTOLabelOptions.BORDER_STROKE_LABEL_MIN))
						.attrMax(String.valueOf(LTOLabelOptions.BORDER_STROKE_LABEL_MAX))
						.attrValue(String.valueOf(LTOLabelOptions.BORDER_STROKE_LABEL_DEFAULT))
					.__()

					.b().text("Inner Border Radius:").__()
					.input()
						.attrType(EnumTypeInputType.NUMBER)
						.attrName(LTOLabelOptions.QUERY_BORDER_RADIUS_INNER)
						.attrStep("1")
						.attrMin(String.valueOf(LTOLabelOptions.BORDER_RADIUS_LABEL_MIN))
						.attrMax(String.valueOf(LTOLabelOptions.BORDER_RADIUS_LABEL_MAX))
						.attrValue(String.valueOf(LTOLabelOptions.BORDER_RADIUS_INNER_DEFAULT))
					.__()

					.b().text("Inner Border Width:").__()
					.input()
						.attrType(EnumTypeInputType.NUMBER)
						.attrName(LTOLabelOptions.QUERY_BORDER_STROKE_INNER)
						.attrStep("0.1")
						.attrMin(String.valueOf(LTOLabelOptions.BORDER_STROKE_LABEL_MIN))
						.attrMax(String.valueOf(LTOLabelOptions.BORDER_STROKE_LABEL_MAX))
						.attrValue(String.valueOf(LTOLabelOptions.BORDER_STROKE_LABEL_DEFAULT))
					.__()

					.b().text("Preview Size:").__()
					.input()
						.attrType(EnumTypeInputType.NUMBER)
						.attrId(LTOLabelOptions.QUERY_PREVIEW_SCALE)
						.attrStep("0.25")
						.attrMin(String.valueOf(LTOLabelOptions.PREVIEW_SCALE_MIN))
						.attrMax(String.valueOf(LTOLabelOptions.PREVIEW_SCALE_MAX))
						.attrValue(String.valueOf(LTOLabelOptions.PREVIEW_SCALE_DEFAULT))
					.__()

					.b().text("Preview Count:").__()
					.input()
						.attrType(EnumTypeInputType.NUMBER)
						.attrId(LTOLabelOptions.QUERY_PREVIEW_COUNT)
						.attrStep("1")
						.attrMin(String.valueOf(LTOLabelOptions.PREVIEW_COUNT_MIN_FORM))
						.attrMax(String.valueOf(LTOLabelOptions.PREVIEW_COUNT_MAX))
						.attrValue(String.valueOf(LTOLabelOptions.PREVIEW_COUNT_DEFAULT))
					.__()
				.__()

				.div().attrClass("button-container")
					.button()
						.attrClass(CSS.BUTTON + CSS.ICON_HIGHLIGHTS + CSS.BUTTON_IMAGE_W_TEXT + CSS.BUTTON_IMAGE)
						.attrType(EnumTypeButtonType.SUBMIT)
						.text("HTML")
					.__()
					.a()
						.attrClass(CSS.BUTTON + CSS.ICON_PDF + CSS.BUTTON_IMAGE_W_TEXT + CSS.BUTTON_IMAGE)
						.attrOnclick(JS.generateLTOLabel(AJAXGenerateLTOLabelPDF.PATH))
						.text("PDF")
					.__()
					.a()
						.attrClass(CSS.BUTTON + CSS.ICON_SVG + CSS.BUTTON_IMAGE_W_TEXT + CSS.BUTTON_IMAGE)
						.attrOnclick(JS.generateLTOLabel(AJAXGenerateLTOLabelSVG.PATH))
						.text("SVG")
					.__()
					.a()
						.attrClass(CSS.BUTTON + CSS.ICON_ARROW_REPEAT + CSS.BUTTON_IMAGE_W_TEXT + CSS.BUTTON_IMAGE)
						.attrHref(PATH)
						.text("Reset")
					.__()
				.__() // div
			.__() // form

			.div().attrClass(CSS.GROUP).attrStyle("width:100%").addAttr(CSS.GROUP_ATTRIBUTE, "Preview")
				.div().attrId("barcode-preview").__()
			.__()
		.__() // div

		.sub().attrStyle("float:right")
			.i()
				.a()
					.attrHref("https://en.wikipedia.org/wiki/Code_39")
					.attrTarget(HTML.TARGET_BLANK)
					.text("Info: LTO uses Code 39 barcode")
				.__()
			.__()
		.__(); // sub
		return null;
	}

	private static List<RecordLabelPreset> getUserPresents(BodyModel model) {
		try {
			return Database.getUserLabelPresets(model.getUserIDViaSession());
		} catch (SQLException | IOException e) {
			return new ArrayList<RecordLabelPreset>();
		}
	}

	public static Void header(Div<?> view, BodyModel model) {
		final List<RecordLabelPreset> presets = getUserPresents(model);

		view
			.div()
				// Start of modal dialog
				.of(parent -> Modal.content(parent, ModalOptions.of(MODAL_ID, false), innerDiv -> {
					innerDiv
						.div()
							.attrClass(CSS.FORMS_CONTAINER)
							.b().text("Name:").__()
							.input()
								.attrId("preset-name")
								.attrOnautocomplete(HTML.DISABLE_AUTOCOMPLETE)
								.attrOnkeypress("presetNameInputChange(event)")
								.attrType(EnumTypeInputType.SEARCH)
								.addAttr(HTML.AUTOFOCUS, "")
							.__()
						.__()
						.div()
							.attrId("preset-error")
							.attrStyle("display:none;margin-bottom:var(--padding-full)")
							.of(innerParent -> InlineMessage.contentGenericError(innerParent, "Message")) // Self closing __()
						.div()
							.attrClass("button-container")
							.button()
								.attrClass(CSS.BUTTON + CSS.ICON_PLUS_SQUARE + CSS.BUTTON_IMAGE_W_TEXT + CSS.BUTTON_IMAGE)
								.attrType(EnumTypeButtonType.BUTTON)
								.attrOnclick("addPreset()")
								.text("Add")
							.__()
							.button()
								.attrClass(CSS.BUTTON + CSS.ICON_CROSS + CSS.BUTTON_IMAGE_W_TEXT + CSS.BUTTON_IMAGE)
								.attrType(EnumTypeButtonType.BUTTON)
								.attrOnclick("hidePresetModal()")
								.text("Cancel")
							.__()
						.__();
				}))
				// End of modal dialog
				.div()
					.attrClass(CSS.HEADER_ITEM + CSS.ICON_PASS)
					.ul().attrClass(CSS.MENU_LIST)
						.li()
							.attrClass(CSS.HEADER_LABEL_TOP)
							.text("Presets")
						.__()
						.of(ul -> {
							for (RecordLabelPreset preset : presets) {
								ul.li()
									.a()
										.attrClass("preset-del")
										.attrOnclick("setPreset('" + preset.getName() + "')")
										.text(preset.getName())
										.button()
											.attrOnclick("deletePreset(event,'" + preset.getName() + "');return false;")
											.text("x")
										.__()
									.__()
								.__();
							}
							if (presets.size() > 0) {
								ul
									.li()
										.hr().__()
									.__();
							}
						})
						.li()
							.a()
								.attrOnclick("showPresetModal()")
								.text("Add New")
							.__()
						.__() // li
					.__() // ul
				.__() // div
			.__(); // wraper div
		return null;
	}

	@Override
	public void requestHandle(HttpExchange he, BodyModel bm) throws IOException, SQLException, InterruptedException, ExecutionException {
		HeadModel thm = HeadModel.of(NAME);
		thm.addCSS(Asset.CSS_FORMS).addCSS(Asset.CSS_LIBRARY);
		thm.addScript(Asset.JS_AJAX);
		thm.addScriptDefer(Asset.JS_LTO_LABEL_GENERATOR);
		BreadCrumbs crumbs = new BreadCrumbs().add(LibraryHandler.NAME, LibraryHandler.PATH).add(NAME, PATH);
		TemplatePageModel tpm = TemplatePageModel.of(LibraryGenerateBarcodeHandler::body, LibraryGenerateBarcodeHandler::header, thm, SelectedPage.Library, bm, crumbs);
		requestHandleCompletePage(he, tpm);
	}

	@Override
	public Permission getHandlePermission() {
		// TODO Auto-generated method stub
		return null;
	}
}
