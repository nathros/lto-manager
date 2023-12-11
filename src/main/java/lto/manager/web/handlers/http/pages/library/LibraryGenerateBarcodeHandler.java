package lto.manager.web.handlers.http.pages.library;

import java.io.IOException;
import java.sql.SQLException;
import java.util.concurrent.ExecutionException;

import org.xmlet.htmlapifaster.Div;
import org.xmlet.htmlapifaster.EnumTypeButtonType;
import org.xmlet.htmlapifaster.EnumTypeInputType;

import com.sun.net.httpserver.HttpExchange;

import lto.manager.common.database.tables.TableTape;
import lto.manager.web.handlers.http.BaseHTTPHandler;
import lto.manager.web.handlers.http.ajax.labelgenerator.LTOLabelOptions;
import lto.manager.web.handlers.http.templates.TemplatePage.BreadCrumbs;
import lto.manager.web.handlers.http.templates.TemplatePage.SelectedPage;
import lto.manager.web.handlers.http.templates.TemplatePage.TemplatePageModel;
import lto.manager.web.handlers.http.templates.models.BodyModel;
import lto.manager.web.handlers.http.templates.models.HeadModel;
import lto.manager.web.resource.Asset;
import lto.manager.web.resource.CSS;
import lto.manager.web.resource.HTML;

public class LibraryGenerateBarcodeHandler extends BaseHTTPHandler {
	public static final String PATH = "/library/generate";
	public static final String NAME = "Generate New Barcode";

	static Void body(Div<?> view, BodyModel model) {
		final String UPPERCASE_INPUT_JS = "this.value = this.value.toUpperCase()";
		view
			.form().attrId("barcode-form").attrTarget(HTML.TARGET_BLANK).attrAction(LibraryGenerateBarcodeAPIHandler.PATH)

				.div()
					.attrClass(CSS.FORMS_CONTAINER)

					.b().text("Prefix:").__()
					.input()
						.attrType(EnumTypeInputType.TEXT)
						.attrName(LTOLabelOptions.QPREFIX)
						.attrMaxlength((long) TableTape.MAX_LEN_BARCODE_FORM)
						.attrOninput(UPPERCASE_INPUT_JS)
						.attrValue("P")
					.__()

					.b().text("Postfix:").__()
					.input()
						.attrType(EnumTypeInputType.TEXT)
						.attrName(LTOLabelOptions.QPOSTFIX)
						.attrMaxlength((long) TableTape.MAX_LEN_BARCODE_FORM)
						.attrOninput(UPPERCASE_INPUT_JS)
						.attrValue("")
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

					.b().text("Label Border Stroke:").__()
					.input()
						.attrType(EnumTypeInputType.NUMBER)
						.attrName(LTOLabelOptions.QUERY_BORDER_STROKE_LABEL)
						.attrStep("0.1")
						.attrMin(String.valueOf(LTOLabelOptions.BORDER_STROKE_LABEL_MIN))
						.attrMax(String.valueOf(LTOLabelOptions.BORDER_STROKE_LABEL_MAX))
						.attrValue(String.valueOf(LTOLabelOptions.BORDER_STROKE_LABEL_DEFAULT))
					.__()

					.b().text("Barcode:").__()
					.input()
						.attrType(EnumTypeInputType.TEXT)
						.attrName(LTOLabelOptions.QBARCODE)
						.attrMaxlength((long) TableTape.MAX_LEN_BARCODE_FORM)
						.attrValue("123ABC")
					.__()

					.b().text("Preview Scale:").__()
					.input()
						.attrType(EnumTypeInputType.NUMBER)
						.attrName(LTOLabelOptions.QUERY_PREVIEW_SCALE)
						.attrStep("0.25")
						.attrMin(String.valueOf(LTOLabelOptions.PREVIEW_SCALE_MIN))
						.attrMax(String.valueOf(LTOLabelOptions.PREVIEW_SCALE_MAX))
						.attrValue(String.valueOf(LTOLabelOptions.PREVIEW_SCALE_DEFAULT))
					.__()

					.b().text("Preview Count:").__()
					.input()
						.attrType(EnumTypeInputType.NUMBER)
						.attrName(LTOLabelOptions.QUERY_PREVIEW_COUNT)
						.attrStep("1")
						.attrMin(String.valueOf(LTOLabelOptions.PREVIEW_COUNT_MIN))
						.attrMax(String.valueOf(LTOLabelOptions.PREVIEW_COUNT_MAX))
						.attrValue(String.valueOf(LTOLabelOptions.PREVIEW_COUNT_DEFAULT))
					.__()
				.__()

				.button()
					.attrClass(CSS.BUTTON + CSS.ICON_CHECK + CSS.BUTTON_IMAGE_W_TEXT + CSS.BUTTON_IMAGE)
					.attrType(EnumTypeButtonType.SUBMIT)
					.text("HTML")
				.__()
				.a()
					.attrClass(CSS.BUTTON + CSS.ICON_PDF + CSS.BUTTON_IMAGE_W_TEXT + CSS.BUTTON_IMAGE)
					.attrHref(PATH)
					.text("PDF")
				.__()
				.a()
					.attrClass(CSS.BUTTON + CSS.ICON_ARROW_REPEAT + CSS.BUTTON_IMAGE_W_TEXT + CSS.BUTTON_IMAGE)
					.attrHref(PATH)
					.text("Reset")
				.__()
			.__()

			.hr().__()

			.div().attrClass(CSS.CARD).addAttr(CSS.CARD_ATTRIBUTE, "Preview")
				.div().attrId("barcode-preview").__()
			.__()
			.sub().attrStyle("float:right")
				.i()
					.a()
						.attrHref("https://en.wikipedia.org/wiki/Code_39")
						.attrTarget(HTML.TARGET_BLANK)
						.text("Info: LTO uses Code 39 barcode")
					.__()
				.__()
			.__()
			;
		return null;
	}

	@Override
	public void requestHandle(HttpExchange he) throws IOException, SQLException, InterruptedException, ExecutionException {
		HeadModel thm = HeadModel.of(NAME);
		thm.addCSS(Asset.CSS_FORMS).addCSS(Asset.CSS_LIBRARY);
		thm.addScriptDefer(Asset.JS_LTO_LABEL_GENERATOR);
		BreadCrumbs crumbs = new BreadCrumbs().add(LibraryHandler.NAME, LibraryHandler.PATH).add(NAME, PATH);
		TemplatePageModel tpm = TemplatePageModel.of(LibraryGenerateBarcodeHandler::body, thm, SelectedPage.Library, BodyModel.of(he, null), crumbs);
		requestHandleCompletePage(he, tpm);
	}
}
