package lto.manager.web.handlers.http.pages.library;

import java.io.IOException;
import java.sql.SQLException;
import java.util.concurrent.ExecutionException;

import com.sun.net.httpserver.HttpExchange;

import htmlflow.HtmlFlow;
import htmlflow.HtmlPage;
import htmlflow.HtmlView;
import lto.manager.web.handlers.http.BaseHTTPHandler;
import lto.manager.web.handlers.http.ajax.labelgenerator.AJAXGenerateLTOLabelHTML;
import lto.manager.web.handlers.http.partial.PartialHead;
import lto.manager.web.handlers.http.templates.TemplatePage.SelectedPage;
import lto.manager.web.handlers.http.templates.TemplatePage.TemplatePageModel;
import lto.manager.web.handlers.http.templates.models.BodyModel;
import lto.manager.web.handlers.http.templates.models.HeadModel;
import lto.manager.web.resource.Asset;
import lto.manager.web.resource.CSS;

public class LibraryGenerateBarcodeHTMLHandler extends BaseHTTPHandler {
	public static HtmlView view = HtmlFlow.view(LibraryGenerateBarcodeHTMLHandler::content).threadSafe().setIndented(false);
	public static final String PATH = "/library/generate/html";
	public static final String NAME = "Generate Barcode";

	// FIXME finish FormValidator
	/*public static final FormValidator prefixValidator = FormValidator.of(ValidatorType.INPUT_TEXT,
			ValidatorOptions.of().valueMaxLength(TableTape.MAX_LEN_BARCODE_FORM), "Prefix");*/

	public static void content(HtmlPage view) {
		view
			.html().attrLang(BaseHTTPHandler.LANG_VALUE)
			.<TemplatePageModel>dynamic((root, bodyModel) -> PartialHead.template(root, bodyModel.getHeadModel()))
			.body()
				.div()
					.attrClass(CSS.COMMON_CONTAINER + CSS.COMMON_CONTAINER_CENTRE + CSS.COMMON_PADDING_FULL)
					.p()
						.text("Note: set print scaling to 100% for correct size barcode")
					.__()
					.button()
						.attrClass(CSS.BUTTON + CSS.BUTTON_IMAGE + CSS.BUTTON_IMAGE_W_TEXT + CSS.ICON_PRINTER)
						.attrOnclick("window.print();return false;")
						.text("Print")
					.__()
					/*.div().attrClass(CSS.COMMON_CONTAINER + CSS.COMMON_CONTAINER_CENTRE + CSS.GAP_HALF)
						.img().attrSrc(Asset.IMG_ICON_INFO).__()
						.p().text("Show help").__() TODO show per browser screenshot of setting
					.__()*/
				.__() // div
				.div()
					.<TemplatePageModel>dynamic((d, e) -> AJAXGenerateLTOLabelHTML.content(d, e.getBodyModel()))
				.__() // div
			.__() // body
		.__(); // html
	}

	@Override
	public void requestHandle(HttpExchange he) throws IOException, SQLException, InterruptedException, ExecutionException {
		HeadModel thm = HeadModel.of(NAME);
		thm.addCSS(Asset.CSS_LIBRARY);
		TemplatePageModel tpm = TemplatePageModel.of(null, null, thm, SelectedPage.Missing, BodyModel.of(he, null), null);
		requestHandleCompleteView(he, view, tpm);
	}
}
