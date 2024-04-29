package lto.manager.web.handlers.http.pages;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import org.xmlet.htmlapifaster.EnumHttpEquivType;

import com.sun.net.httpserver.HttpExchange;

import htmlflow.HtmlFlow;
import htmlflow.HtmlPage;
import htmlflow.HtmlView;
import lto.manager.common.state.State;
import lto.manager.web.handlers.http.BaseHTTPHandler;
import lto.manager.web.handlers.http.templates.TemplatePage.SelectedPage;
import lto.manager.web.handlers.http.templates.TemplatePage.TemplatePageModel;
import lto.manager.web.handlers.http.templates.models.BodyModel;
import lto.manager.web.handlers.http.templates.models.HeadModel;

public class LogOutHandler extends BaseHTTPHandler {
	private static HtmlView<TemplatePageModel> v = HtmlFlow.view(LogOutHandler::content);
	public static HtmlView<TemplatePageModel> view = v.threadSafe().setIndented(false);
	public static final String PATH = "/logout";


	public static void content(HtmlPage view) {
		view
		.html().attrLang(BaseHTTPHandler.LANG_VALUE)
		.<TemplatePageModel>dynamic((root, model) -> {
			final UUID session = model.getBodyModel().getSession();
			boolean removed = false;
			if (session != null) {
				model.getBodyModel().removeSession(session);
				removed = State.removeLoginSession(session);
			}
			root
			.head()
				.meta()
					.attrHttpEquiv(EnumHttpEquivType.REFRESH)
					.attrContent("0; URL=/")
				.__()
			.__() // head
			.body()
				.attrStyle("display:flex;flex-direction:column;justify-content:center;align-items:center;height:100vh;margin:0;")
				.p().text(removed ? "Logout Success!" : "Failed to remove session").__()
				.a().attrHref("/").text("Click here to continue").__()
			.__();
			})
		.__(); // html
	}

	@Override
	public void requestHandle(HttpExchange he) throws IOException, InterruptedException, ExecutionException {
		HeadModel thm = HeadModel.of("Logout");
		TemplatePageModel tpm = TemplatePageModel.of(null, null, thm, SelectedPage.Missing, BodyModel.of(he, null), null);
		requestHandleCompleteView(he, view, tpm);
	}

}
