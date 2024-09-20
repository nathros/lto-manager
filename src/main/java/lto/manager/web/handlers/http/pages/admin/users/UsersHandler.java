package lto.manager.web.handlers.http.pages.admin.users;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;

import org.xmlet.htmlapifaster.Div;

import com.sun.net.httpserver.HttpExchange;

import lto.manager.common.Util;
import lto.manager.common.database.Database;
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

public class UsersHandler extends BaseHTTPHandler {
	public static final String PATH = "/admin/users/";
	public static final String NAME = "Users";

	public static final String QDEL = "del";

	private static List<RecordUser> getUsers() {
		try {
			return Database.getAllUsers(true);
		} catch (Exception e) {
			Util.throwException(e);
		}
		return null;
	}

	private static List<Consumer<Div<?>>> listUsersContents(final List<RecordUser> users) {
		List<Consumer<Div<?>>> itemContents = new ArrayList<Consumer<Div<?>>>();
		for (final var user : users) {
			itemContents.add(d -> {
				d.attrClass("list-item-role")
					.div()
						.attrClass("list-item-user gap")
						.div()
							.img()
								.attrSrc(Asset.IMG_AVATAR_PATH + user.getAvatar())
								.attrStyle("width:2rem")
							.__()
						.__()
						.div()
							.h4().text(user.getUsername()).__()
							.p().text("Role: " + user.getRole().getName()).__()
						.__()
						.div()
							.p().attrStyle("font-size:small").text("Created: " + user.getCreatedFormatted() + ",").__()
						.__()
						.div()
							.p().attrStyle("font-size:small").text("Description: " + user.getDescription()).__()
						.__()
						.of(inner -> {
							if (!user.getEnabled()) {
								inner.div()
									.b()
										.attrStyle("color:red")
										.text("Disabled")
									.__()
								.__();
							}
						})
					.__()
					.div()
						.attrClass(CSS.FORMS_BUTTONS)
						.a()
							.attrClass(CSS.TOOLTIP + CSS.ICON_EDIT)
							.attrHref(UsersEditHandler.PATH + "?" + UsersEditHandler.QID + "=" + user.getID())
							.em().text("Edit").__()
						.__()
						.a()
							.attrClass(CSS.BACKGROUND_ERROR_BEFORE + CSS.TOOLTIP + CSS.ICON_RUBBISH)
							.attrHref(PATH + "?" + QDEL + "=" + user.getID())
							.attrOnclick(JS.confirmToastA("Are you sure?"))
							.em().text("Delete").__()
						.__()
						/*.a()
							.attrClass(CSS.BUTTON)
							.attrHref(UsersEditHandler.PATH + "?" + UsersEditHandler.QID + "=" + user.getID())
							.text("Edit")
						.__()
						.a()
							.attrClass(CSS.BUTTON + CSS.BACKGROUND_CAUTION)
							.attrHref(PATH + "?" + QDEL + "=" + user.getID())
							.attrOnclick(JS.confirmToastA("Are you sure?"))
							.text("Delete")
						.__()*/
					.__();
			});
		}
		return itemContents;
	}

	static Void content(Div<?> view, BodyModel model) {
		final String deleteIDStr = model.getQuery(QDEL);
		String error = null;
		if (deleteIDStr != null) {
			try {
				Database.deleteUser(Integer.parseInt(deleteIDStr));
			} catch (Exception e) {
				error = e.getMessage();
			}
		}
		final String errorMessage = error;

		final List<RecordUser> users = getUsers();
		final List<Consumer<Div<?>>> body = listUsersContents(users);

		view
		.div()
			.of(parent -> {
				if (errorMessage != null) {
					parent
						.div().attrClass(CSS.GAP_BOTTOM)
						.div().of(d -> InlineMessage.contentGenericError(d, "Failed to delete user: " + errorMessage))
					.__();
				}
				ListContainer.content(parent, body);
			})
		.__(); //  div
		return null;
	}

	@Override
	public void requestHandle(HttpExchange he) throws IOException, InterruptedException, ExecutionException {
		HeadModel thm = HeadModel.of(NAME);
		thm.addCSS(Asset.CSS_LIST).addCSS(Asset.CSS_FORMS);
		thm.addScript(Asset.JS_LIST);
		BreadCrumbs crumbs = new BreadCrumbs().add(AdminHandler.NAME, AdminHandler.PATH).add(NAME, PATH);
		TemplatePageModel tpm = TemplatePageModel.of(UsersHandler::content, UsersHandler::header, thm, SelectedPage.Admin, BodyModel.of(he, null), crumbs);
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
							.attrHref(UsersNewHandler.PATH_NEW)
							.text("Add New User")
						.__()
					.__()
				.__() // ul
			.__(); // div
		return null;
	}

}
