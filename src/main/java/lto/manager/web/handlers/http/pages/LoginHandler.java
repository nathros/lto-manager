package lto.manager.web.handlers.http.pages;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import org.xmlet.htmlapifaster.EnumMethodType;
import org.xmlet.htmlapifaster.EnumTypeButtonType;
import org.xmlet.htmlapifaster.EnumTypeInputType;

import com.sun.net.httpserver.HttpExchange;

import htmlflow.HtmlFlow;
import htmlflow.HtmlPage;
import htmlflow.HtmlViewAsync;
import lto.manager.web.handlers.http.BaseHTTPHandler;
import lto.manager.web.handlers.http.partial.PartialHead;
import lto.manager.web.handlers.http.templates.TemplatePage.SelectedPage;
import lto.manager.web.handlers.http.templates.TemplatePage.TemplatePageModel;
import lto.manager.web.handlers.http.templates.models.BodyModel;
import lto.manager.web.handlers.http.templates.models.HeadModel;
import lto.manager.web.resource.Asset;
import lto.manager.web.resource.CSS;

public class LoginHandler extends BaseHTTPHandler {
	public static HtmlViewAsync view = HtmlFlow.viewAsync(LoginHandler::content);
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
				// Try login
				// if success then redirect

				/*root
					.head()
						.meta()
							.attrHttpEquiv(EnumHttpEquivType.REFRESH)
							.attrContent("100; URL=/")
						.__()
					.__() // head
					.body()
						.p().text("Login Success!").__()
						.a().attrHref("/").text("Click here to continue").__()
					.__();*/
				return;
			}
			//model.getBodyModel().addResponseCookie("auth", "password");

			PartialHead.template(root, model.getHeadModel());
			final boolean loginComplete = loginSuccess;
			root
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
										f.p().attrStyle("color:red").text("Failed to login").__();
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
		thm.AddCSS(Asset.CSS_LOGIN);
		TemplatePageModel tpm = TemplatePageModel.of(null, thm, SelectedPage.Missing, BodyModel.of(he, null));
		requestHandleCompleteFuture(he, view.renderAsync(tpm), tpm);
	}

}
