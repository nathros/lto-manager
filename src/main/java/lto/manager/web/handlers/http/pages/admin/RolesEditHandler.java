package lto.manager.web.handlers.http.pages.admin;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import org.xmlet.htmlapifaster.Div;

import com.sun.net.httpserver.HttpExchange;

import lto.manager.web.handlers.http.BaseHTTPHandler;
import lto.manager.web.handlers.http.templates.TemplatePage.BreadCrumbs;
import lto.manager.web.handlers.http.templates.TemplatePage.SelectedPage;
import lto.manager.web.handlers.http.templates.TemplatePage.TemplatePageModel;
import lto.manager.web.handlers.http.templates.models.BodyModel;
import lto.manager.web.handlers.http.templates.models.HeadModel;

public class RolesEditHandler extends BaseHTTPHandler {
	public static final String PATH = "/admin/roles/edit/";
	public static final String NAME = "Role Edit";

	public static final String Qid = "id";

	static Void content(Div<?> view, BodyModel model) {
		final String idStr = model.getQuery(Qid);

		view
			.div()
				.text("role edit WIP for ID: " + idStr)
			.__(); // div
		return null;
	}

	@Override
	public void requestHandle(HttpExchange he) throws IOException, InterruptedException, ExecutionException {
		HeadModel thm = HeadModel.of(NAME);
		BreadCrumbs crumbs = new BreadCrumbs().add(AdminHandler.NAME, AdminHandler.PATH).add(RolesHandler.NAME, RolesHandler.PATH).add(NAME, PATH);
		TemplatePageModel tpm = TemplatePageModel.of(RolesEditHandler::content, null, thm, SelectedPage.Admin, BodyModel.of(he, null), crumbs);
		requestHandleCompletePage(he, tpm);
	}

}
