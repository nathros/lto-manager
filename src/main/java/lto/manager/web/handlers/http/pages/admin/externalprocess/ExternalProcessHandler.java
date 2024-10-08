package lto.manager.web.handlers.http.pages.admin.externalprocess;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import org.xmlet.htmlapifaster.Div;

import com.sun.net.httpserver.HttpExchange;

import lto.manager.common.ExternalProcess;
import lto.manager.common.database.tables.records.RecordRole.Permission;
import lto.manager.web.handlers.http.BaseHTTPHandler;
import lto.manager.web.handlers.http.pages.admin.AdminHandler;
import lto.manager.web.handlers.http.templates.TemplatePage.BreadCrumbs;
import lto.manager.web.handlers.http.templates.TemplatePage.SelectedPage;
import lto.manager.web.handlers.http.templates.TemplatePage.TemplatePageModel;
import lto.manager.web.handlers.http.templates.models.BodyModel;
import lto.manager.web.handlers.http.templates.models.HeadModel;
import lto.manager.web.resource.CSS;

public class ExternalProcessHandler extends BaseHTTPHandler {
	public static final String PATH = "/admin/ext/";
	public static final Permission PERMISSION = Permission.ADVANCED_EXETERNAL_PROCESS;
	public static final String NAME = "External Processes";

	static Void content(Div<?> view, BodyModel model) {
		view
		.div()
			.div().attrClass(CSS.GROUP).addAttr(CSS.GROUP_ATTRIBUTE, "Current Processes").of(d -> {
				var keyList = ExternalProcess.getCurrentProcessKeyList();
				for (String key: keyList) {
					final String link = ExternalProcessViewerHandler.PATH + "?" + ExternalProcessViewerHandler.TYPE
						+ "=" + ExternalProcessViewerHandler.TYPE_CURRENT + "&" + ExternalProcessViewerHandler.ID
						+ "=" + key;
					d.a().attrClass(CSS.BUTTON).attrHref(link).text(key).__();
				}
				if (keyList.size() == 0) {
					d.p().text("Empty").__();
				}
			}).__()
			.div().attrClass(CSS.GROUP).addAttr(CSS.GROUP_ATTRIBUTE, "Completed Processes").of(d -> {
				var keyList = ExternalProcess.getFinishedProcessKeyList();
				for (String key: keyList) {
					final String link = ExternalProcessViewerHandler.PATH + "?" + ExternalProcessViewerHandler.TYPE
						+ "=" + ExternalProcessViewerHandler.TYPE_COMPLETE + "&" + ExternalProcessViewerHandler.ID
						+ "=" + key;
					d.a().attrClass(CSS.BUTTON).attrHref(link).text(key).__();
				}
				if (keyList.size() == 0) {
					d.p().text("Empty").__();
				}
			}).__()
			.i().attrStyle("float:right").text("Note: completed processes are periodically cleared").__()
		.__(); //  div
		return null;
	}

	@Override
	public void requestHandle(HttpExchange he, BodyModel bm) throws IOException, InterruptedException, ExecutionException {
		HeadModel thm = HeadModel.of(NAME);
		BreadCrumbs crumbs = new BreadCrumbs().add(AdminHandler.NAME, AdminHandler.PATH).add(NAME, PATH);
		TemplatePageModel tpm = TemplatePageModel.of(ExternalProcessHandler::content, null, thm, SelectedPage.Admin, bm, crumbs);
		requestHandleCompletePage(he, tpm);
	}

	@Override
	public Permission getHandlePermission() {
		return PERMISSION;
	}

}
