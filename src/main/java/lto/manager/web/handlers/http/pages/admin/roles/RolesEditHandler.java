package lto.manager.web.handlers.http.pages.admin.roles;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import org.xmlet.htmlapifaster.Div;
import org.xmlet.htmlapifaster.EnumMethodType;
import org.xmlet.htmlapifaster.EnumTypeInputType;

import com.sun.net.httpserver.HttpExchange;

import lto.manager.common.database.Database;
import lto.manager.common.database.tables.TableRoles;
import lto.manager.common.database.tables.records.RecordRole;
import lto.manager.web.check.FormValidator;
import lto.manager.web.check.FormValidator.ValidatorOptions;
import lto.manager.web.check.FormValidator.ValidatorStatus;
import lto.manager.web.check.FormValidator.ValidatorType;
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

public class RolesEditHandler extends BaseHTTPHandler {
	public static final String PATH = "/admin/roles/edit/";
	public static final String NAME = "Role Edit";

	public static final String QID = "id";
	public static final String QRoleName = "role-name";
	public static final String QRoleDescription = "role-description";

	private static final int IID = 0;
	private static final int IName = 1;
	private static final int IDescription = 2;

	private static final FormValidator roleNameValidator = FormValidator.of(ValidatorType.INPUT_TEXT,
			ValidatorOptions.of().valueNotEmpty().valueNotNull().valueMaxLength(TableRoles.MAX_LENGTH_NAME), QRoleName);
	private static final FormValidator roleDescriptionValidator = FormValidator.of(ValidatorType.INPUT_TEXT,
			ValidatorOptions.of().valueMaxLength(TableRoles.MAX_LENGTH_DESCRIPTION), QRoleDescription);

	static private RecordRole getRoles(String id) {
		try {
			return Database.getRole(Integer.parseInt(id));
		} catch (Exception e) {
			return null;
		}
	}

	static Void content(Div<?> view, BodyModel model) {
		String[] query = { model.getQuery(QID), model.getQuery(QRoleName), model.getQuery(QRoleDescription) };

		if (query[IID] == null) {
			view.div().of(d -> InlineMessage.contentGenericError(d, "Role id is missing"));
			return null;
		}
		final boolean enabled = model.isPOSTMethod();
		final ValidatorStatus roleNameStatus = roleNameValidator.validate(query[IName], enabled);
		final ValidatorStatus roleDescriptionStatus = roleDescriptionValidator.validate(query[IDescription], enabled);

		final RecordRole role = getRoles(query[IID]);

		view
			.form()
				.attrMethod(EnumMethodType.POST)
				.h2().text("Role Details").__()
				.div().attrClass(CSS.FORMS_CONTAINER)
					.b().text("Name:").__()
					.input()
						.attrClass(roleNameStatus.statusOK() ? "" : CSS.ERROR)
						.attrType(EnumTypeInputType.TEXT).attrName(QRoleName)
						.attrMaxlength((long) TableRoles.MAX_LENGTH_NAME)
						.attrValue(query[IName] == null ? role.getName() : query[IName])
					.__()

					.b().text("Description:").__()
					.input()
						.attrClass(roleDescriptionStatus.statusOK() ? "" : CSS.ERROR)
						.attrType(EnumTypeInputType.TEXT).attrName(QRoleDescription)
						.attrMaxlength((long) TableRoles.MAX_LENGTH_DESCRIPTION)
						.attrValue(query[IDescription] == null ? role.getDescription() : query[IDescription])
					.__()
				.__()

				.hr().__()

				.h2().text("System Permissions").__()
				.hr().__()

				.h2().text("Page Permissions").__()
				.table()
					.attrClass(CSS.TABLE)
					.tr()
						.th().text("Page").__()
						.th().text("View").__()
						.th().text("Edit").__()
					.__() // tr
					.tr()
						.td().text("Admin").__()
						.td().text("").__()
						.td().text("").__()
					.__() // tr
				.__() // table
				.hr().__()

			.__(); // form
		return null;
	}

	@Override
	public void requestHandle(HttpExchange he) throws IOException, InterruptedException, ExecutionException {
		HeadModel thm = HeadModel.of(NAME);
		thm.addCSS(Asset.CSS_FORMS);
		BreadCrumbs crumbs = new BreadCrumbs().add(AdminHandler.NAME, AdminHandler.PATH).add(RolesHandler.NAME, RolesHandler.PATH).add("Edit", PATH);
		TemplatePageModel tpm = TemplatePageModel.of(RolesEditHandler::content, null, thm, SelectedPage.Admin, BodyModel.of(he, null), crumbs);
		requestHandleCompletePage(he, tpm);
	}

}
