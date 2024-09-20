package lto.manager.web.handlers.http.pages.admin.users;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import com.sun.net.httpserver.HttpExchange;

import lto.manager.common.database.Database;
import lto.manager.common.database.tables.records.RecordRole.Permission;
import lto.manager.web.handlers.http.BaseHTTPHandler;
import lto.manager.web.handlers.http.pages.admin.AdminHandler;
import lto.manager.web.handlers.http.templates.TemplatePage.BreadCrumbs;
import lto.manager.web.handlers.http.templates.TemplatePage.SelectedPage;
import lto.manager.web.handlers.http.templates.TemplatePage.TemplatePageModel;
import lto.manager.web.handlers.http.templates.models.BodyModel;
import lto.manager.web.handlers.http.templates.models.HeadModel;
import lto.manager.web.resource.Asset;

public class UsersNewHandler extends BaseHTTPHandler {
	public static final String PATH = "/admin/users/new/";
	public static final String PATH_NEW = "/admin/users/new/?" + UsersEditHandler.QID + "=" + Database.NEW_RECORD_ID;
	public static final String NAME = "New User";

	@Override
	public void requestHandle(HttpExchange he, BodyModel bm) throws IOException, InterruptedException, ExecutionException {
		HeadModel thm = HeadModel.of(NAME);
		thm.addCSS(Asset.CSS_FORMS);
		BreadCrumbs crumbs = new BreadCrumbs().add(AdminHandler.NAME, AdminHandler.PATH).add(UsersHandler.NAME, UsersHandler.PATH).add("New", PATH_NEW);
		TemplatePageModel tpm = TemplatePageModel.of(UsersEditHandler::content, null, thm, SelectedPage.Admin, bm, crumbs);
		requestHandleCompletePage(he, tpm);
	}

	@Override
	public Permission getHandlePermission() {
		// TODO Auto-generated method stub
		return null;
	}

}
