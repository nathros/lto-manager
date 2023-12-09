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
		view
			.form().attrId("barcode-form").attrTarget(HTML.TARGET_BLANK).attrAction(LibraryGenerateBarcodeAPIHandler.PATH)

				.div()
					.attrClass(CSS.FORMS_CONTAINER)

					.b().text("Prefix:").__()
					.input()
						.attrType(EnumTypeInputType.TEXT)
						.attrName(LTOLabelOptions.QPREFIX)
						.attrMaxlength((long) TableTape.MAX_LEN_BARCODE_FORM)
					.__()

					.b().text("Barcode:").__()
					.input()
						.attrType(EnumTypeInputType.TEXT)
						.attrName(LTOLabelOptions.QBARCODE)
						.attrMaxlength((long) TableTape.MAX_LEN_BARCODE_FORM)
						.attrValue("123ABC")
					.__()
				.__()

				.button()
					.attrClass(CSS.BUTTON + CSS.ICON_CHECK + CSS.BUTTON_IMAGE_W_TEXT + CSS.BUTTON_IMAGE)
					.attrType(EnumTypeButtonType.SUBMIT)
					.text("Create")
				.__()
			.__()

			.hr().__()

			.div().attrClass(CSS.CARD).addAttr(CSS.CARD_ATTRIBUTE, "Preview")
				.div().attrId("barcode-preview").__()
			.__()
			;
		return null;
	}

	@Override
	public void requestHandle(HttpExchange he) throws IOException, SQLException, InterruptedException, ExecutionException {
		HeadModel thm = HeadModel.of(NAME);
		thm.addCSS(Asset.CSS_FORMS).addCSS(Asset.CSS_LIBRARY);
		thm.addScriptDefer(Asset.JS_LIBRARY);
		BreadCrumbs crumbs = new BreadCrumbs().add(LibraryHandler.NAME, LibraryHandler.PATH).add(NAME, PATH);
		TemplatePageModel tpm = TemplatePageModel.of(LibraryGenerateBarcodeHandler::body, thm, SelectedPage.Library, BodyModel.of(he, null), crumbs);
		requestHandleCompletePage(he, tpm);
	}
}
