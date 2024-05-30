package lto.manager.web.handlers.http.pages.admin.roles;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;

import org.sqlite.SQLiteErrorCode;
import org.xmlet.htmlapifaster.Div;

import com.sun.net.httpserver.HttpExchange;

import lto.manager.common.Util;
import lto.manager.common.database.Database;
import lto.manager.common.database.tables.records.RecordRole;
import lto.manager.common.database.tables.records.RecordUser;
import lto.manager.web.handlers.http.BaseHTTPHandler;
import lto.manager.web.handlers.http.pages.admin.AdminHandler;
import lto.manager.web.handlers.http.partial.inlinemessage.InlineMessage;
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

	public static final String QDEL = "del";

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
							.attrHref(PATH + "?" + QDEL + "=" + role.getID())
							.attrOnclick(JS.confirmToastA("Are you sure?"))
							.text("Delete")
						.__()
					.__();
			});
		}
		return itemContents;
	}

	static Void content(Div<?> view, BodyModel model) {
		final String deleteIDStr = model.getQuery(QDEL);
		String error = null;
		int errorC = 0;
		if (deleteIDStr != null) {
			try {
				Database.deleteRole(Integer.parseInt(deleteIDStr));
			} catch (SQLException e) {
				error = e.getMessage();
				errorC = e.getErrorCode();
			} catch (Exception e) {
				error = e.getMessage();
			}
		}
		final String errorMessage = error;
		final int errorCode = errorC;

		final List<RecordRole> roles = getRoles();
		final List<Consumer<Div<?>>> body = listItemContents(roles);

		view
			.div()
				.of(parent -> {
					if (errorMessage != null) {
						parent.div().attrClass(CSS.GAP_BOTTOM);
						if (errorCode == SQLiteErrorCode.SQLITE_CONSTRAINT.code) {
							try {
								final List<RecordUser> users = Database.getUsersByRole(Integer.parseInt(deleteIDStr), false);
								String[] usersList = { "" };
								for (var u : users) {
									usersList[0] += usersList[0].concat(u.getUsername()) + ", ";
								}
								usersList[0] = usersList[0].substring(0, usersList[0].length() - 2); // Remove last ','
								parent.div().of(d -> InlineMessage.contentGenericError(d, "Cannot delete as role is in use by following users: " + usersList[0]));
							} catch (Exception e) {
								parent.div().of(d -> InlineMessage.contentGenericError(d, "Failed to get affected users", e));
							}
						} else {
							parent.div().of(d -> InlineMessage.contentGenericError(d, "Failed to delete role: " + deleteIDStr + " with error: " + errorMessage));
						}
						parent.__(); // GAP_BOTTOM
					}
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
		TemplatePageModel tpm = TemplatePageModel.of(RolesHandler::content, RolesHandler::header, thm, SelectedPage.Admin, BodyModel.of(he, null), crumbs);
		requestHandleCompletePage(he, tpm);
	}

	static Void header(Div<?> view, BodyModel model) {
		view
			.div()
				.attrClass(CSS.HEADER_ITEM + CSS.ICON_PLUS_SQUARE)
				.ul()
					.attrClass(CSS.MENU_LIST)
					.li()
						.attrClass(CSS.HEADER_LABEL_TOP)
						.text("New")
					.__()
					.li()
						.a()
							.attrHref(RolesNewHandler.PATH)
							.text("Add New Role")
						.__()
					.__()
				.__() // ul
			.__(); // div
		return null;
	}

}
