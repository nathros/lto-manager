package lto.manager.web.handlers.http.pages.admin;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import org.xmlet.htmlapifaster.Div;

import com.sun.net.httpserver.HttpExchange;

import lto.manager.common.database.tables.records.RecordRole.Permission;
import lto.manager.web.handlers.http.BaseHTTPHandler;
import lto.manager.web.handlers.http.ajax.admin.AJAXCheckUpdates;
import lto.manager.web.handlers.http.partial.loading.OnLoad;
import lto.manager.web.handlers.http.partial.loading.OnLoadOptions;
import lto.manager.web.handlers.http.templates.TemplatePage.BreadCrumbs;
import lto.manager.web.handlers.http.templates.TemplatePage.SelectedPage;
import lto.manager.web.handlers.http.templates.TemplatePage.TemplatePageModel;
import lto.manager.web.handlers.http.templates.models.BodyModel;
import lto.manager.web.handlers.http.templates.models.HeadModel;
import lto.manager.web.resource.Asset;

public class AppUpdateHandler extends BaseHTTPHandler {
	public static final String PATH = "/admin/update/";
	public static final Permission PERMISSION = Permission.SYSTEM_SETTINGS_UPDATE;
	public static final String NAME = "Update";

	static Void content(Div<?> view, BodyModel model) {
		view
			.div()
				.of(div -> OnLoad.content(div, OnLoadOptions.of(AJAXCheckUpdates.PATH)))
			.__(); // div
		return null;
	}

	@Override
	public void requestHandle(HttpExchange he, BodyModel bm) throws IOException, InterruptedException, ExecutionException {
		HeadModel thm = HeadModel.of(NAME).addScript(Asset.JS_AJAX);
		BreadCrumbs crumbs = new BreadCrumbs().add(AdminHandler.NAME, AdminHandler.PATH).add(NAME, PATH);
		TemplatePageModel tpm = TemplatePageModel.of(AppUpdateHandler::content, null, thm, SelectedPage.Admin, bm, crumbs);
		requestHandleCompletePage(he, tpm);
	}

	@Override
	public Permission getHandlePermission() {
		return PERMISSION;
	}

}
