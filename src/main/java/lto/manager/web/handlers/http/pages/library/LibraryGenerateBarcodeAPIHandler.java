package lto.manager.web.handlers.http.pages.library;

import java.io.IOException;
import java.sql.SQLException;
import java.util.concurrent.ExecutionException;

import org.xmlet.htmlapifaster.Div;

import com.sun.net.httpserver.HttpExchange;

import htmlflow.HtmlFlow;
import htmlflow.HtmlPage;
import htmlflow.HtmlViewAsync;
import lto.manager.common.database.tables.TableTape;
import lto.manager.web.handlers.http.BaseHTTPHandler;
import lto.manager.web.handlers.http.partial.PartialHead;
import lto.manager.web.handlers.http.templates.TemplatePage.SelectedPage;
import lto.manager.web.handlers.http.templates.TemplatePage.TemplatePageModel;
import lto.manager.web.handlers.http.templates.models.BodyModel;
import lto.manager.web.handlers.http.templates.models.HeadModel;
import lto.manager.web.resource.Asset;
import lto.manager.web.resource.CSS;
import lto.manager.web.resource.FormValidator;
import lto.manager.web.resource.FormValidator.ValidatorOptions;
import lto.manager.web.resource.FormValidator.ValidatorType;

public class LibraryGenerateBarcodeAPIHandler extends BaseHTTPHandler {
	public static HtmlViewAsync view = HtmlFlow.viewAsync(LibraryGenerateBarcodeAPIHandler::content);
	public static final String PATH = "/library/generate/result";
	public static final String NAME = "Generate Barcode";

	public static final FormValidator prefixValidator = FormValidator.of(ValidatorType.INPUT_TEXT,
			ValidatorOptions.of().valueMaxLength(TableTape.MAX_LEN_BARCODE), "Prefix");

	public static void content(HtmlPage view) {
		view
			.html().attrLang(BaseHTTPHandler.LANG_VALUE)
			.<TemplatePageModel>dynamic((root, bodyModel) -> PartialHead.template(root, bodyModel.getHeadModel()))
			.body()
				.div()
					.attrClass(CSS.COMMON_CONTAINER + CSS.COMMON_CONTAINER_CENTRE)
					.p()
						.text("Note: set print scaling to 100% for correct size barcode")
					.__()
					.button()
						.attrClass(CSS.BUTTON + CSS.BUTTON_IMAGE + CSS.BUTTON_IMAGE_W_TEXT + CSS.ICON_PRINTER)
						.attrOnclick("window.print();return false;")
						.text("Print")
					.__()
					.div().attrClass(CSS.COMMON_CONTAINER + CSS.COMMON_CONTAINER_CENTRE + CSS.GAP_HALF)
						.img().attrSrc(Asset.IMG_ICON_INFO).__()
						.p().text("Show help").__()
					.__()
				.__() // div
				.div()
					.of(d -> barcode(d, "test"))
				.__() // div
			.__() // body
		.__(); // html
	}

	public static Void barcode(Div<?> view, final String value) {
		final String NUMBER_STYLE = "border:1px solid black;border-right:none;border-top:none;flex:1;display:flex;align-items:center;justify-content:center;";
		view
			.div()
				.attrStyle("width:78.50mm;height:16.05mm;box-sizing:border-box;border:1px solid black")
				.div()
					.attrStyle("height:4.80mm;display:flex;margin-left:3.175mm;margin-right:3.175mm;display:flex;background-color:red")
					.div().attrStyle(NUMBER_STYLE).text("A").__()
					.div().attrStyle(NUMBER_STYLE).text("A").__()
					.div().attrStyle(NUMBER_STYLE).text("A").__()
					.div().attrStyle(NUMBER_STYLE).text("A").__()
					.div().attrStyle(NUMBER_STYLE).text("A").__()
					.div().attrStyle(NUMBER_STYLE).text("A").__()
					.div().attrStyle(NUMBER_STYLE + "border-right:1px solid black").text("L7").__()
				.__()
				// code 39 barcode
			.__();
		return null;
	}

	@Override
	public void requestHandle(HttpExchange he) throws IOException, SQLException, InterruptedException, ExecutionException {
		HeadModel thm = HeadModel.of(NAME);
		TemplatePageModel tpm = TemplatePageModel.of(null, thm, SelectedPage.Missing, BodyModel.of(he, null), null);
		requestHandleCompleteFuture(he, view.renderAsync(tpm), tpm);
	}
}
