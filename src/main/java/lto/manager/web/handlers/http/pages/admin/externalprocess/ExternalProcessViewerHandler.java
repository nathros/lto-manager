package lto.manager.web.handlers.http.pages.admin.externalprocess;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import org.xmlet.htmlapifaster.Div;

import com.sun.net.httpserver.HttpExchange;

import lto.manager.common.ExternalProcess;
import lto.manager.web.handlers.http.BaseHTTPHandler;
import lto.manager.web.handlers.http.pages.admin.AdminHandler;
import lto.manager.web.handlers.http.templates.TemplatePage.BreadCrumbs;
import lto.manager.web.handlers.http.templates.TemplatePage.SelectedPage;
import lto.manager.web.handlers.http.templates.TemplatePage.TemplatePageModel;
import lto.manager.web.handlers.http.templates.models.BodyModel;
import lto.manager.web.handlers.http.templates.models.HeadModel;
import lto.manager.web.resource.CSS;

public class ExternalProcessViewerHandler extends BaseHTTPHandler {
	public static ExternalProcessViewerHandler self = new ExternalProcessViewerHandler();
	public static final String PATH = "/admin/ext/view";
	public static final String NAME = "External Processes Viewer";

	public static final String TYPE = "type";
	public static final String ID = "id";
	public static final String TYPE_COMPLETE = "1";
	public static final String TYPE_CURRENT = "2";

	static Void content(Div<?> view, BodyModel model) {
		final String type = model.getQueryNoNull(TYPE);
		final String id = model.getQueryNoNull(ID);

		view
			.div()
				.a().attrClass(CSS.BUTTON).attrOnclick("history.back()").text("Back").__()
				.of(div -> {
					ExternalProcess ep = null;
					if (type.equals(TYPE_COMPLETE)) {
						ep = ExternalProcess.getFinishedProcess(id);
					} else if (type.equals(TYPE_CURRENT)) {
						ep = ExternalProcess.getCurrentProcess(id);
					}
					final ExternalProcess pro = ep;
					if (pro != null) {
						div
							.br().__()
							.b().text("stdout").__().br().__()
							.of(o -> {
								for (String i: pro.getStdout()) {
									o.i().text(i).__().br().__();
								}
							})
							.hr().__()
							.b().text("stderr").__().br().__()
							.of(o -> {
								for (String i: pro.getStderr()) {
									o.i().text(i).__().br().__();
								}
							});
					} else {
						div.text("ID: " + id + " NOT FOUND");
					}
			}).__(); //  div
		return null;
	}

	@Override
	public void requestHandle(HttpExchange he) throws IOException, InterruptedException, ExecutionException {
		HeadModel thm = HeadModel.of("External Processes Viewer");
		BreadCrumbs crumbs = new BreadCrumbs().add(AdminHandler.NAME, AdminHandler.PATH).add(ExternalProcessHandler.NAME, ExternalProcessHandler.PATH).add(NAME, "");
		TemplatePageModel tpm = TemplatePageModel.of(ExternalProcessViewerHandler::content, null, thm, SelectedPage.Admin, BodyModel.of(he, null), crumbs);
		requestHandleCompletePage(he, tpm);
	}

}
