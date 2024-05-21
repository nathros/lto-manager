package lto.manager.web.handlers.http.pages;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.concurrent.ExecutionException;
import java.util.function.Supplier;
import java.util.logging.Level;

import org.xmlet.htmlapifaster.EnumMethodType;

import com.sun.net.httpserver.HttpExchange;

import htmlflow.HtmlFlow;
import htmlflow.HtmlPage;
import htmlflow.HtmlView;
import lto.manager.common.log.Log;
import lto.manager.web.MainWeb;
import lto.manager.web.handlers.http.BaseHTTPHandler;
import lto.manager.web.handlers.http.templates.TemplatePage.SelectedPage;
import lto.manager.web.handlers.http.templates.TemplatePage.TemplatePageModel;
import lto.manager.web.handlers.http.templates.models.BodyModel;
import lto.manager.web.handlers.http.templates.models.HeadModel;
import lto.manager.web.resource.Asset;

public class ShutdownHandler extends BaseHTTPHandler {
	private static HtmlView<TemplatePageModel> v = HtmlFlow.view(ShutdownHandler::content);
	public static HtmlView<TemplatePageModel> view = v.threadSafe().setIndented(false);
	public static final String PATH = "/shutdown/";

	public static void content(HtmlPage view) {
		view
		.html().attrLang(BaseHTTPHandler.LANG_VALUE)
		.<TemplatePageModel>dynamic((root, model) -> {
			Supplier<String> mainCSSSupplier = () -> {
				try {
					return AssetHandler.getResourceAsString(Asset.CSS_MAIN);
				} catch (IOException e) {
					return "";
				}
			};
			Supplier<String> mobileCSSSupplier = () -> {
				try {
					return AssetHandler.getResourceAsString(Asset.CSS_MOBILE);
				} catch (IOException e) {
					return "";
				}
			};
			Supplier<String> loginCSSSupplier = () -> {
				try {
					var bytes = AssetHandler.getResourceLoader(Asset.CSS_LOGIN).readAllBytes();
					return new String(bytes, StandardCharsets.UTF_8);
				} catch (IOException e) {
					return "";
				}
			};
			Supplier<String> favicoSupplier = () -> {
				try {
					var bytes = AssetHandler.getResourceLoader(Asset.IMG_FAVICO_SVG).readAllBytes();
					return "data:image/svg+xml," + Base64.getEncoder().encodeToString(bytes);
				} catch (IOException e) {
					return "";
				}
			};

			final String CSSMain = mainCSSSupplier.get();
			final String CSSMobile = mobileCSSSupplier.get();
			final String CSSLogin = loginCSSSupplier.get();
			final String IMGFavico = favicoSupplier.get();
			root
				.head()
					// When a user is not logged in then they cannot access any resources, even CSS or JS.
					// So this page must have all resources added in-line
					.meta().addAttr(BaseHTTPHandler.CHARSET_KEY, BaseHTTPHandler.CHARSET_VALUE).__()
					.title().of(title -> title.text(model.getHeadModel().getTitle())).__()
					.link().addAttr(BaseHTTPHandler.ICON_KEY, BaseHTTPHandler.ICON_VALUE).attrHref(IMGFavico).addAttr(BaseHTTPHandler.TYPE_KEY, BaseHTTPHandler.TYPE_SVG).__()
					.meta().attrName(BaseHTTPHandler.VIEWPORT_KEY).attrContent(BaseHTTPHandler.VIEWPORT_VALUE).__()
					.script().raw("window.history.replaceState(null, null, '/'); // Remove /shutdown from URL") .__()
					.style().raw(CSSMain).__()
					.style().raw(CSSMobile).__()
					.style().raw(CSSLogin).__()
				.__()
				.body()
					.div()
						.div()
							.form().attrMethod(EnumMethodType.POST)
								.h3().text("LTO Manager").__()
								.b().attrStyle("color:red").text("Service shutdown").__()
							.__() // form
						.__()
						.div().__() // lines
						.div().__() // Background
					.__() // div
				.__(); // body
			})
		.__(); // html
	}

	@Override
	public void requestHandle(HttpExchange he) throws IOException, InterruptedException, ExecutionException {
		HeadModel thm = HeadModel.of("Shutdown");
		TemplatePageModel tpm = TemplatePageModel.of(null, null, thm, SelectedPage.Missing, BodyModel.of(he, null), null);
		requestHandleCompleteView(he, view, tpm);
		try {
			MainWeb.exitWait.put(MainWeb.ExitReason.NORMAL);
		} catch (InterruptedException e) {
			Log.log(Level.SEVERE, "Failed to initiate shutdown", e);
		}
	}

}
