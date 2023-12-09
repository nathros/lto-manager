package lto.manager.web.handlers.http.pages;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.function.Supplier;

import org.xmlet.htmlapifaster.EnumHttpEquivType;
import org.xmlet.htmlapifaster.EnumMethodType;
import org.xmlet.htmlapifaster.EnumTypeButtonType;
import org.xmlet.htmlapifaster.EnumTypeInputType;

import com.sun.net.httpserver.HttpExchange;

import htmlflow.HtmlFlow;
import htmlflow.HtmlPage;
import htmlflow.HtmlViewAsync;
import lto.manager.common.security.Security;
import lto.manager.web.handlers.http.BaseHTTPHandler;
import lto.manager.web.handlers.http.templates.TemplatePage.SelectedPage;
import lto.manager.web.handlers.http.templates.TemplatePage.TemplatePageModel;
import lto.manager.web.handlers.http.templates.models.BodyModel;
import lto.manager.web.handlers.http.templates.models.HeadModel;
import lto.manager.web.resource.Asset;
import lto.manager.web.resource.CSS;

public class LogInHandler extends BaseHTTPHandler {
	public static HtmlViewAsync view = HtmlFlow.viewAsync(LogInHandler::content);
	public static final String PATH = "/login";

	private static final String USER = "username";
	private static final String PASS = "password";

	public static void content(HtmlPage view) {
		view
		.html().attrLang(BaseHTTPHandler.LANG_VALUE)
		.<TemplatePageModel>dynamic((root, model) -> {
			final String username = model.getBodyModel().getQueryNoNull(USER);
			final String password = model.getBodyModel().getQueryNoNull(PASS);
			boolean loginSuccess = false;
			if (model.getBodyModel().isPOSTMethod()) {
				final UUID result = Security.loginUser(username, password);
				loginSuccess = result != null;
				if (loginSuccess) { // If login success redirect to /
					model.getBodyModel().setNewSession(result);
					root
						.head()
							.meta()
								.attrHttpEquiv(EnumHttpEquivType.REFRESH)
								.attrContent("0; URL=/")
							.__()
						.__() // head
						.body()
							.p().text("Login Success!").__()
							.a().attrHref("/").text("Click here to continue").__()
						.__();
					return;
				}
			}

			final boolean loginComplete = loginSuccess;

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
			Supplier<String> mainJSSupplier = () -> {
				try {
					return AssetHandler.getResourceAsString(Asset.JS_MAIN);
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
			final String JSMain = mainJSSupplier.get();
			final String IMGFavico = favicoSupplier.get();
			root
				.head()
					// When a user is not logged in then they cannot access any resources, even CSS or JS.
					// So this page must have all resources added in-line
					.meta().addAttr(BaseHTTPHandler.CHARSET_KEY, BaseHTTPHandler.CHARSET_VALUE).__()
					.title().of(title -> title.text(model.getHeadModel().getTitle())).__()
					.link().addAttr(BaseHTTPHandler.ICON_KEY, BaseHTTPHandler.ICON_VALUE).attrHref(IMGFavico).addAttr(BaseHTTPHandler.TYPE_KEY, BaseHTTPHandler.TYPE_SVG).__()
					.meta().attrName(BaseHTTPHandler.VIEWPORT_KEY).attrContent(BaseHTTPHandler.VIEWPORT_VALUE).__()
					.style().text(CSSMain).__()
					.style().text(CSSMobile).__()
					.style().text(CSSLogin).__()
					.script().text(JSMain).__()
				.__()
				.body()
					.div()
						.div()
							.form().attrMethod(EnumMethodType.POST)
								.h3().text("LTO Manager").__()
								.b().text("Username: *").__()
								.input()
									.attrType(EnumTypeInputType.TEXT)
									.attrName(USER)
									.attrValue(username)
								.__()
								.b().text("Password: *").__()
								.input()
									.attrType(EnumTypeInputType.PASSWORD)
									.attrName(PASS)
									.attrValue(password)
								.__()
								.button().attrClass(CSS.BUTTON).attrType(EnumTypeButtonType.SUBMIT).text("Login").__()
								.of(f -> {
									if (model.getBodyModel().isPOSTMethod() && !loginComplete) {
										f.b().attrStyle("color:red").text("Failed to login").__();
									}
								})
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
		HeadModel thm = HeadModel.of("Login");
		TemplatePageModel tpm = TemplatePageModel.of(null, thm, SelectedPage.Missing, BodyModel.of(he, null), null);
		requestHandleCompleteFuture(he, view.renderAsync(tpm), tpm);
	}

}
