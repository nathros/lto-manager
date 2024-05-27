package lto.manager.web.handlers.http.pages.admin.users;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.xmlet.htmlapifaster.Div;
import org.xmlet.htmlapifaster.EnumTypeInputType;

import com.sun.net.httpserver.HttpExchange;

import lto.manager.common.database.Database;
import lto.manager.common.database.tables.TableUser;
import lto.manager.common.database.tables.records.RecordRole;
import lto.manager.common.database.tables.records.RecordUser;
import lto.manager.web.handlers.http.BaseHTTPHandler;
import lto.manager.web.handlers.http.pages.admin.AdminHandler;
import lto.manager.web.handlers.http.partial.inlinemessage.InlineErrorMessage;
import lto.manager.web.handlers.http.templates.TemplatePage.BreadCrumbs;
import lto.manager.web.handlers.http.templates.TemplatePage.SelectedPage;
import lto.manager.web.handlers.http.templates.TemplatePage.TemplatePageModel;
import lto.manager.web.handlers.http.templates.models.BodyModel;
import lto.manager.web.handlers.http.templates.models.HeadModel;
import lto.manager.web.resource.Asset;
import lto.manager.web.resource.CSS;
import lto.manager.web.resource.HTML;
import lto.manager.web.resource.Localisation;
import lto.manager.web.resource.Query;

public class UsersEditHandler extends BaseHTTPHandler {
	public static final String PATH = "/admin/users/edit/";
	public static final String NAME = "User Edit";

	public static final String QID = "id";
	public static final String QName = "name";
	public static final String QEnabled = "enabled";
	public static final String QLang = "lang";
	public static final String QAvatar = "avatar";
	public static final String QRole = "role";

	static private RecordUser getUser(String idStr) {
		try {
			return Database.getUserByID(Integer.valueOf(idStr), true);
		} catch (Exception e) {
			return null;
		}
	}

	static private List<RecordRole> getRoles() {
		try {
			return Database.getAllRoles();
		} catch (Exception e) {
			return null;
		}
	}

	static Void content(Div<?> view, BodyModel model) {
		final String qID = model.getQuery(QID);
		if (qID == null) {
			view.div().of(div -> InlineErrorMessage.contentGeneric(div, "User ID not specified"));
			return null;
		}

		final RecordUser user = getUser(qID);
		if (user == null) {
			view.div().of(div -> InlineErrorMessage.contentGeneric(div, "Cannot find user with ID: " + qID));
			return null;
		}

		final String qName = model.getQuery(QName);
		final String qEnabled = model.getQuery(QEnabled);
		final String qLang = model.getQuery(QLang);
		final String qAvatar = model.getQuery(QAvatar);
		final String qRole = model.getQuery(QRole);
		final List<RecordRole> roles = getRoles();
		if (roles == null) {
			view.div().of(div -> InlineErrorMessage.contentGeneric(div, "Cannot get user roles"));
			return null;
		} else if (qName != null) {
			RecordUser updated = RecordUser.of(Integer.valueOf(qID), RecordRole.of(Integer.valueOf(qRole)), qName, user.getHash(), user.getSalt(), Query.CHECKED.equals(qEnabled), user.getCreated(), Integer.valueOf(qLang), qAvatar);
			try {
				Database.updateUser(updated);
			} catch (SQLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		view
			.div()
				.form().attrClass(CSS.FORMS_CONTAINER)
					.b().text("User ID:").__()
					.input()
						.attrType(EnumTypeInputType.TEXT)
						.attrName(QID)
						.attrValue(qID)
						.attrReadonly(true)
					.__()

					.b().text("Username:").__()
					.input()
						.attrType(EnumTypeInputType.TEXT)
						.attrName(QName)
						.attrMaxlength((long) TableUser.MAX_LENGTH_USERNAME)
						.attrValue(qName == null ? user.getUsername() : qName)
					.__()

					.b().text("Enabled:").__()
					.input()
						.attrType(EnumTypeInputType.CHECKBOX)
						.attrName(QEnabled)
						.attrChecked(qEnabled == null ? user.getEnabled() : Query.CHECKED.equals(qEnabled))
					.__()

					.b().text("Language:").__()
					.select()
						.attrName(QLang)
						.of(select -> {
							for (int i = 0; i < Localisation.Langugage.size(); i++) {
								final String langKey = RecordUser.df.format(i);
								final String langValue = Localisation.Langugage.get(langKey);
								select.option()
									.attrValue(langKey)
									.of(s -> HTML.option(s, langKey.equals(qLang)))
									.text(langValue)
								.__();
							}
						})
					.__()

					.b().text("Avatar:").__()
					.input()
						.attrType(EnumTypeInputType.TEXT)
						.attrName(QAvatar)
						.attrMaxlength((long) TableUser.MAX_LENGTH_AVATAR)
						.attrValue(qAvatar == null ? user.getAvatar() : qAvatar)
					.__()

					.b().text("Role:").__()
					.select()
						.attrName(QRole)
						.of(select -> {
							for (var r : roles) {
								final String roleID = String.valueOf(r.getID());

								select.option()
									.attrValue(roleID)
									.of(s -> HTML.option(s, qRole == null ? (user.getRole().getID() == r.getID()) : r.getID() == Integer.parseInt(qRole)))
									.text(r.getName())
								.__();
							}
						})
					.__()

					.button()
						.attrClass(CSS.BUTTON + CSS.ICON_CHECK + CSS.BUTTON_IMAGE_W_TEXT + CSS.BUTTON_IMAGE)
						.text("Update")
					.__()

				.__() // form
			.__(); // div
		return null;
	}

	@Override
	public void requestHandle(HttpExchange he) throws IOException, InterruptedException, ExecutionException {
		HeadModel thm = HeadModel.of(NAME);
		thm.addCSS(Asset.CSS_FORMS);
		BreadCrumbs crumbs = new BreadCrumbs().add(AdminHandler.NAME, AdminHandler.PATH).add(UsersHandler.NAME, UsersHandler.PATH).add("Edit", PATH);
		TemplatePageModel tpm = TemplatePageModel.of(UsersEditHandler::content, null, thm, SelectedPage.Admin, BodyModel.of(he, null), crumbs);
		requestHandleCompletePage(he, tpm);
	}

}
