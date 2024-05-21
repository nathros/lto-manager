package lto.manager.web.handlers.http.pages.sandpit;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import org.xmlet.htmlapifaster.Div;

import com.sun.net.httpserver.HttpExchange;

import lto.manager.web.handlers.http.BaseHTTPHandler;
import lto.manager.web.handlers.http.templates.TemplatePage.SelectedPage;
import lto.manager.web.handlers.http.templates.TemplatePage.TemplatePageModel;
import lto.manager.web.handlers.http.templates.models.BodyModel;
import lto.manager.web.handlers.http.templates.models.HeadModel;
import lto.manager.web.resource.Asset;

public class InternalErrorTesterHandler extends BaseHTTPHandler {
	public static final String PATH = Asset.PATH_SANDPIT_BASE + "internalerror";
	public static final String NAME = "Internal Error";

	static Void content(Div<?> view, BodyModel model) {
		int badInt = 32 / 0; // Cause exception
		view.div().text("This should not be shown: " + badInt).__();
		return null;
	}

	@Override
	public void requestHandle(HttpExchange he) throws IOException, InterruptedException, ExecutionException {
		HeadModel thm = HeadModel.of(NAME);
		TemplatePageModel tpm = TemplatePageModel.of(InternalErrorTesterHandler::content, null, thm, SelectedPage.Admin, BodyModel.of(he, null), null);
		requestHandleCompletePage(he, tpm);
	}

}
