package lto.manager.web.handlers.http.pages.admin.users;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.sqlite.SQLiteErrorCode;
import org.sqlite.SQLiteException;
import org.xmlet.htmlapifaster.Div;
import org.xmlet.htmlapifaster.EnumMethodType;
import org.xmlet.htmlapifaster.EnumTypeInputType;

import com.sun.net.httpserver.HttpExchange;

import lto.manager.common.database.Database;
import lto.manager.common.database.tables.TableUser;
import lto.manager.common.database.tables.records.RecordRole;
import lto.manager.common.database.tables.records.RecordRole.Permission;
import lto.manager.common.database.tables.records.RecordUser;
import lto.manager.web.check.CheckStatusType;
import lto.manager.web.check.FormValidator;
import lto.manager.web.check.FormValidator.ValidatorOptions;
import lto.manager.web.check.FormValidator.ValidatorStatus;
import lto.manager.web.check.FormValidator.ValidatorType;
import lto.manager.web.check.OperationStatus;
import lto.manager.web.handlers.http.BaseHTTPHandler;
import lto.manager.web.handlers.http.pages.admin.AdminHandler;
import lto.manager.web.handlers.http.partial.inlinemessage.InlineMessage;
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
	public static final String QDescription = "description";
	public static final String QEnabled = "enabled";
	public static final String QPassword = "pass";
	public static final String QPasswordConfirm = "passc";
	public static final String QLang = "lang";
	public static final String QAvatar = "avatar";
	public static final String QRole = "role";

	private static final int IID = 0;
	private static final int IName = 1;
	private static final int IDescription = 2;
	private static final int IEnabled = 3;
	private static final int IPassword = 4;
	private static final int IPasswordConfirm = 5;
	private static final int ILang = 6;
	private static final int IAvatar = 7;
	private static final int IRole = 8;

	private static final FormValidator userNameValidator = FormValidator.of(ValidatorType.INPUT_TEXT,
			ValidatorOptions.of().valueNotEmpty().valueNotNull().valueMaxLength(TableUser.MAX_LENGTH_USERNAME), "Username");
	private static final FormValidator descriptionValidator = FormValidator.of(ValidatorType.INPUT_TEXT,
			ValidatorOptions.of().valueMaxLength(TableUser.MAX_LENGTH_DESCRIPTION), "Description");
	private static final FormValidator passwordValidator = FormValidator.of(ValidatorType.INPUT_PASSWORD,
			ValidatorOptions.of(), "Password");

	static private RecordUser getUser(String idStr) {
		try {
			final int userID = Integer.valueOf(idStr);
			if (Database.NEW_RECORD_ID == userID) {
				return RecordUser.getBlank();
			}
			return Database.getUserByID(userID, true);
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
		String[] query = { model.getQuery(QID), model.getQuery(QName), model.getQuery(QDescription), model.getQuery(QEnabled),
				model.getQuery(QPassword), model.getQuery(QPasswordConfirm), model.getQuery(QLang),
				model.getQuery(QAvatar), model.getQuery(QRole) }; // Index must match for Qxx and Ixx

		final OperationStatus updateUserAction = OperationStatus.undefined();

		if (query[IID] == null) {
			view.div().attrClass(CSS.GAP_BOTTOM).div().of(div -> InlineMessage.contentGenericError(div, "User ID not specified")).__();
			return null;
		}
		final RecordUser user = getUser(query[IID]);

		if (user == null) {
			view.div().attrClass(CSS.GAP_BOTTOM).div().of(div -> InlineMessage.contentGenericError(div, "Cannot find user with ID: " + query[IID])).__();
			return null;
		}
		final boolean isNewUser = user.getID() == Database.NEW_RECORD_ID;
		final ValidatorStatus passwordStatus = passwordValidator.validatePassword(query[IPassword], query[IPasswordConfirm], false);
		final ValidatorStatus userNameStatus = userNameValidator.validate(query[IName] == null ? user.getUsername() : query[IName], true);
		final ValidatorStatus descriptionStatus = descriptionValidator.validate(query[IDescription] == null ? user.getDescription() : query[IDescription], true);
		final boolean validateOK = passwordStatus.statusOK() && userNameStatus.statusOK();
		final List<RecordRole> roles = getRoles();

		if (roles == null) {
			view.div().attrClass(CSS.GAP_BOTTOM).div().of(div -> InlineMessage.contentGenericError(div, "Cannot get user roles")).__();
			return null;
		} else if (query[IName] != null && validateOK) {
			RecordUser updated = RecordUser.of(Integer.valueOf(query[IID]), RecordRole.of(Integer.valueOf(query[IRole])), query[IName],
					query[IDescription], user.getHash(), user.getSalt(), Query.CHECKED.equals(query[IEnabled]), user.getCreated(),
					Integer.valueOf(query[ILang]), query[IAvatar]);
			if (query[IPassword] != null && passwordStatus.statusOK()) {
				updated.setPassword(query[IPassword]);
			}
			if (updated.getID() == Database.NEW_RECORD_ID) {
				try {
					Database.addUser(updated);
					updateUserAction.update(CheckStatusType.OK, "Successfully added new user");
				} catch (SQLiteException e) {
					if (e.getResultCode().code == SQLiteErrorCode.SQLITE_CONSTRAINT_UNIQUE.code) {
						userNameStatus.update(CheckStatusType.ERROR, "Failed to add user with username [" + query[IName] + "] already exists");
					} else {
						updateUserAction.update(CheckStatusType.ERROR, "Failed to add user: " + e.getMessage());
					}
				} catch (SQLException | IOException  e) {
					updateUserAction.update(CheckStatusType.ERROR, "Failed to add user: " + e.getMessage());
				}
			} else {
				try {
					Database.updateUser(updated);
					if (query[IPassword] == null && query[IPasswordConfirm] == null) {
						updateUserAction.update(CheckStatusType.OK, "Successfully updated user, password is blank and has not been changed");
					} else {
						updateUserAction.update(CheckStatusType.OK, "Successfully updated user");
					}
				} catch (SQLException | IOException e) {
					updateUserAction.update(CheckStatusType.ERROR, "Failed to update user: " + e.getMessage());
				}
			}
		}

		view.div().of(d -> {
				if (!passwordStatus.statusOK()) {
					d.div().attrClass(CSS.GAP_BOTTOM).div().of(innerD -> InlineMessage.contentGenericError(innerD, passwordStatus.getUserMessage())).__();
				}
				if (!userNameStatus.statusOK()) {
					d.div().attrClass(CSS.GAP_BOTTOM).div().of(innerD -> InlineMessage.contentGenericError(innerD, userNameStatus.getUserMessage())).__();
				}
				if (updateUserAction.getStatus() == CheckStatusType.OK) {
					d.div().attrClass(CSS.GAP_BOTTOM).div().of(innerD -> InlineMessage.contentGenericOK(innerD, updateUserAction.getMessage())).__();
				} else if (updateUserAction.getStatus() == CheckStatusType.ERROR) {
					d.div().attrClass(CSS.GAP_BOTTOM).div().of(innerD -> InlineMessage.contentGenericError(innerD, updateUserAction.getMessage())).__();
				}
			})
			.form()
				.attrMethod(EnumMethodType.POST)
				.div().attrClass(CSS.FORMS_CONTAINER)
					.of(d -> { if (!isNewUser) d.b().attrStyle("display:none").text("User ID:").__(); })
					.input()
						.attrStyle("display:none")
						.attrType(isNewUser ? EnumTypeInputType.HIDDEN : EnumTypeInputType.TEXT)
						.attrName(QID)
						.attrValue(query[IID])
						.attrReadonly(true)
					.__()

					.b().text("Username:").__()
					.input()
						.attrClass(userNameStatus.statusOK() ? "" : CSS.ERROR)
						.attrType(EnumTypeInputType.TEXT).attrName(QName)
						.attrMaxlength((long) TableUser.MAX_LENGTH_USERNAME)
						.attrValue(query[IName] == null ? user.getUsername() : query[IName])
					.__()

					.b().text("Description:").__()
					.input()
						.attrClass(descriptionStatus.statusOK() ? "" : CSS.ERROR)
						.attrType(EnumTypeInputType.TEXT).attrName(QDescription)
						.attrMaxlength((long) TableUser.MAX_LENGTH_DESCRIPTION)
						.attrValue(query[IDescription] == null ? user.getDescription() : query[IDescription])
					.__()

					.b().text("Password:").__()
					.input()
						.attrClass(passwordStatus.statusOK() ? "" : CSS.ERROR)
						.attrType(EnumTypeInputType.PASSWORD)
						.attrName(QPassword)
					.__()

					.b().text("Confirm Password:").__()
					.input()
						.attrClass(passwordStatus.statusOK() ? "" : CSS.ERROR)
						.attrType(EnumTypeInputType.PASSWORD)
						.attrName(QPasswordConfirm)
					.__()

					.b().text("Enabled:").__()
					.input()
						.attrType(EnumTypeInputType.CHECKBOX)
						.attrName(QEnabled)
						.attrChecked(query[IEnabled] == null ? user.getEnabled() : Query.CHECKED.equals(query[IEnabled]))
					.__()
					.input()
						.attrType(EnumTypeInputType.HIDDEN)
						.attrName(QEnabled)
						.attrValue("off")
					.__()

					.b().text("Language:").__().select().attrName(QLang).of(select -> {
						for (int i = 0; i < Localisation.Langugage.size(); i++) {
							final String langKey = RecordUser.df.format(i);
							final String langValue = Localisation.Langugage.get(langKey);
							select.option().attrValue(langKey).of(s -> HTML.option(s, langKey.equals(query[ILang])))
									.text(langValue).__();
						}
					}).__()

					.b().text("Avatar:").__()
					.input()
						.attrType(EnumTypeInputType.TEXT)
						.attrName(QAvatar)
						.attrMaxlength((long) TableUser.MAX_LENGTH_AVATAR)
						.attrValue(query[IAvatar] == null ? user.getAvatar() : query[IAvatar])
					.__()

					.b().text("Role:").__().select().attrName(QRole).of(select -> {
						for (var r : roles) {
							final String roleID = String.valueOf(r.getID());

							select.option().attrValue(roleID)
									.of(s -> HTML.option(s, query[IRole] == null ? (user.getRole().getID() == r.getID())
											: r.getID() == Integer.parseInt(query[IRole])))
									.text(r.getName()).__();
						}
					}).__()
				.__() // div FORMS_CONTAINER

				.div().attrClass(CSS.FORMS_BUTTONS)
					.a()
						.attrClass(CSS.BUTTON + CSS.ICON_ARROW_LEFT + CSS.BUTTON_IMAGE_W_TEXT + CSS.BUTTON_IMAGE)
						.attrHref(UsersHandler.PATH)
						.text("Back")
					.__()
					.button()
						.attrClass(CSS.BUTTON + CSS.ICON_SAVE + CSS.BUTTON_IMAGE_W_TEXT + CSS.BUTTON_IMAGE)
						.text(isNewUser ? "Add" : "Update")
					.__()
				.__() // div FORMS_BUTTONS


				.__() // form
			.__(); // div
		return null;
	}

	@Override
	public void requestHandle(HttpExchange he, BodyModel bm) throws IOException, InterruptedException, ExecutionException {
		HeadModel thm = HeadModel.of(NAME);
		thm.addCSS(Asset.CSS_FORMS);
		final String id = bm.getQuery(QID);
		BreadCrumbs crumbs = new BreadCrumbs()
				.add(AdminHandler.NAME, AdminHandler.PATH)
				.add(UsersHandler.NAME, UsersHandler.PATH)
				.add("Edit", PATH + "?" + QID + "=" + (id == null ? "" : id));

		TemplatePageModel tpm = TemplatePageModel.of(UsersEditHandler::content, null, thm, SelectedPage.Admin, bm, crumbs);
		requestHandleCompletePage(he, tpm);
	}

	@Override
	public Permission getHandlePermission() {
		// TODO Auto-generated method stub
		return null;
	}

}
