package lto.manager.web.handlers.http.pages.admin.externalprocess;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.function.BiFunction;

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

	private static final BiFunction<String, String, ExternalProcess> getExternalProcess = (final String typeQuery, final String idQuery) -> {
		if (typeQuery.equals(TYPE_COMPLETE)) {
			return ExternalProcess.getFinishedProcess(idQuery);
		} else if (typeQuery.equals(TYPE_CURRENT)) {
			return ExternalProcess.getCurrentProcess(idQuery);
		}
		return null;
	};

	static Void content(Div<?> view, BodyModel model) {
		final String id = model.getQueryNoNull(ID);

		view
			.div()
				.of(div -> {
					final ExternalProcess pro = getExternalProcess.apply(model.getQueryNoNull(TYPE), id);
					if (pro != null) {
						div
							.b().text("Command: ").__()
							.span()
								.attrClass(CSS.FONT_MONOSPACE)
								.text(pro.getArgsAsString())
							.__()

							.div().attrClass(CSS.GROUP).addAttr(CSS.GROUP_ATTRIBUTE, "stdout")
								.div().attrClass(CSS.FONT_MONOSPACE + CSS.FONT_SMALL).of(d -> {
									for (String i: pro.getStdout()) {
										d.i().text(i).__().br().__();
									}
								}).__() // div inner
							.__() // div group

							.div().attrClass(CSS.GROUP + CSS.FONT_MONOSPACE).addAttr(CSS.GROUP_ATTRIBUTE, "stderr")
								.div().attrClass(CSS.FONT_MONOSPACE + CSS.FONT_SMALL).of(d -> {
									for (String i: pro.getStderr()) {
										d.i().text(i).__().br().__();
									}
								}).__() // div inner
							.__(); // div group
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
