package lto.manager.web.handlers.http.pages.admin;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;

import org.xmlet.htmlapifaster.Div;

import com.sun.net.httpserver.HttpExchange;

import lto.manager.common.Util;
import lto.manager.common.database.Database;
import lto.manager.common.database.tables.records.RecordRole;
import lto.manager.web.handlers.http.BaseHTTPHandler;
import lto.manager.web.handlers.http.partial.list.ListContainer;
import lto.manager.web.handlers.http.templates.TemplatePage.BreadCrumbs;
import lto.manager.web.handlers.http.templates.TemplatePage.SelectedPage;
import lto.manager.web.handlers.http.templates.TemplatePage.TemplatePageModel;
import lto.manager.web.handlers.http.templates.models.BodyModel;
import lto.manager.web.handlers.http.templates.models.HeadModel;
import lto.manager.web.resource.Asset;
import lto.manager.web.resource.CSS;
import lto.manager.web.resource.JS;

public class RolesHandler extends BaseHTTPHandler {
	public static final String PATH = "/admin/roles/";
	public static final String NAME = "Roles";

	private static List<RecordRole> getRoles() {
		try {
			return Database.getAllRoles();
		} catch (Exception e) {
			Util.throwException(e);
		}
		return null;
	}

	private static List<Consumer<Div<?>>> listItemContents(final List<RecordRole> roles) {
		List<Consumer<Div<?>>> itemContents = new ArrayList<Consumer<Div<?>>>();
		for (final var role : roles) {
			itemContents.add(d -> {
				d.attrClass("list-item-role")
					.div()
						.h4().text(role.getName()).__()
						.p().text(role.getDescription()).__()
					.__()
					.div()
						.a()
							.attrClass(CSS.BUTTON)
							.attrHref(RolesEditHandler.PATH + "?" + RolesEditHandler.Qid + "=" + role.getID())
							.text("Edit")
						.__()
						.a()
							.attrClass(CSS.BUTTON + CSS.BACKGROUND_CAUTION)
							.attrHref(PATH + "?" + RolesEditHandler.Qid + "=" + role.getID())
							.attrOnclick(JS.confirmToastA("Are you sure?"))
							.text("Delete")
						.__()
					.__();
			});
		}
		return itemContents;
	}

	static Void content(Div<?> view, BodyModel model) {
		final List<RecordRole> roles = getRoles();
		final List<Consumer<Div<?>>> body = listItemContents(roles);

		view
			.div()
				.of(parent -> {
					ListContainer.content(parent, body);
				})
			.__(); //  div
		return null;
	}

	@Override
	public void requestHandle(HttpExchange he) throws IOException, InterruptedException, ExecutionException {
		HeadModel thm = HeadModel.of(NAME);
		thm.addCSS(Asset.CSS_LIST);
		thm.addScript(Asset.JS_LIST);
		BreadCrumbs crumbs = new BreadCrumbs().add(AdminHandler.NAME, AdminHandler.PATH).add(NAME, PATH);
		TemplatePageModel tpm = TemplatePageModel.of(RolesHandler::content, null, thm, SelectedPage.Admin, BodyModel.of(he, null), crumbs);
		requestHandleCompletePage(he, tpm);
	}

}
