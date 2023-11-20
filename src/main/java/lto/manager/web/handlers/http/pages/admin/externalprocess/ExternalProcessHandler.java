package lto.manager.web.handlers.http.pages.admin.externalprocess;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import org.xmlet.htmlapifaster.Div;

import com.sun.net.httpserver.HttpExchange;

import lto.manager.common.ExternalProcess;
import lto.manager.web.handlers.http.BaseHTTPHandler;
import lto.manager.web.handlers.http.templates.TemplatePage.SelectedPage;
import lto.manager.web.handlers.http.templates.TemplatePage.TemplatePageModel;
import lto.manager.web.handlers.http.templates.models.BodyModel;
import lto.manager.web.handlers.http.templates.models.HeadModel;
import lto.manager.web.resource.CSS;

public class ExternalProcessHandler extends BaseHTTPHandler {
	public static ExternalProcessHandler self = new ExternalProcessHandler();
	public static final String PATH = "/admin/ext";

	static Void content(Div<?> view, BodyModel model) {
		view
		.div()
			.a().attrClass(CSS.BUTTON).attrOnclick("history.back()").text("Back").__()
			.div().attrClass("card").addAttr("header-text", "Current Processes").of(d -> {
				var keyList = ExternalProcess.getCurrentProcessKeyList();
				for (String key: keyList) {
					final String link = ExternalProcessViewerHandler.PATH + "?" + ExternalProcessViewerHandler.TYPE
						+ "=" + ExternalProcessViewerHandler.TYPE_CURRENT + "&" + ExternalProcessViewerHandler.ID
						+ "=" + key;
					final var ep = ExternalProcess.getCurrentProcess(key);
					d.a().attrClass(CSS.BUTTON).attrHref(link).text(ep.getClass().getSimpleName() + " (" + key + ")").__();
				}
			}).__()
			.div().attrClass("card").addAttr("header-text", "Completed Processes").of(d -> {
				var keyList = ExternalProcess.getFinishedProcessKeyList();
				for (String key: keyList) {
					final String link = ExternalProcessViewerHandler.PATH + "?" + ExternalProcessViewerHandler.TYPE
						+ "=" + ExternalProcessViewerHandler.TYPE_COMPLETE + "&" + ExternalProcessViewerHandler.ID
						+ "=" + key;
					final var ep = ExternalProcess.getFinishedProcess(key);
					d.a().attrClass(CSS.BUTTON).attrHref(link).text(ep.getClass().getSimpleName() + " (" + key + ")").__();
				}
			}).__()
		.__(); //  div
		return null;
	}

	@Override
	public void requestHandle(HttpExchange he) throws IOException, InterruptedException, ExecutionException {
		HeadModel thm = HeadModel.of("External Processes");
		TemplatePageModel tpm = TemplatePageModel.of(ExternalProcessHandler::content, thm, SelectedPage.Admin, BodyModel.of(he, null));
		requestHandleCompletePage(he, tpm);
	}

}
