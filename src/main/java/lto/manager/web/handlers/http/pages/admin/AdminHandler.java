package lto.manager.web.handlers.http.pages.admin;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import org.xmlet.htmlapifaster.Div;

import com.sun.net.httpserver.HttpExchange;

import lto.manager.common.database.tables.records.RecordRole.Permission;
import lto.manager.common.database.tables.records.RecordUser;
import lto.manager.web.handlers.http.BaseHTTPHandler;
import lto.manager.web.handlers.http.pages.admin.advanced.DatabaseHandler;
import lto.manager.web.handlers.http.pages.admin.advanced.LoggingHandler;
import lto.manager.web.handlers.http.pages.admin.advanced.SessionViewerHandler;
import lto.manager.web.handlers.http.pages.admin.advanced.WebsocketListConnectionAdminHandler;
import lto.manager.web.handlers.http.pages.admin.advanced.WebsocketTestAdminHandler;
import lto.manager.web.handlers.http.pages.admin.externalprocess.ExternalProcessHandler;
import lto.manager.web.handlers.http.pages.admin.roles.RolesHandler;
import lto.manager.web.handlers.http.pages.admin.users.UsersHandler;
import lto.manager.web.handlers.http.partial.components.ButtonExtended.ButtonExtendedOptions;
import lto.manager.web.handlers.http.partial.components.ButtonExtendedGroup;
import lto.manager.web.handlers.http.partial.components.ButtonExtendedGroup.ButtonExtendedGroupOptions;
import lto.manager.web.handlers.http.partial.dashboard.DashboardContainer;
import lto.manager.web.handlers.http.partial.dashboard.DashboardVersion;
import lto.manager.web.handlers.http.partial.dashboard.DashboardVersion.DashboardVersionOptions;
import lto.manager.web.handlers.http.templates.TemplatePage.BreadCrumbs;
import lto.manager.web.handlers.http.templates.TemplatePage.SelectedPage;
import lto.manager.web.handlers.http.templates.TemplatePage.TemplatePageModel;
import lto.manager.web.handlers.http.templates.models.BodyModel;
import lto.manager.web.handlers.http.templates.models.HeadModel;
import lto.manager.web.resource.Asset;
import lto.manager.web.resource.CSS;

public class AdminHandler extends BaseHTTPHandler {
	public static final String PATH = "/admin/";
	public static final String NAME = "Admin";

	private static final ButtonExtendedOptions sysOptionSetting = ButtonExtendedOptions.of("Settings", "Change system settings", UpdateOptionsHandler.PERMISSION, UpdateOptionsHandler.PATH, CSS.ICON_WRENCH);
	private static final ButtonExtendedOptions sysOptionUsers = ButtonExtendedOptions.of("Users", "Manage Users", UsersHandler.PERMISSION, UsersHandler.PATH, CSS.ICON_PEOPLE);
	private static final ButtonExtendedOptions sysOptionRoles = ButtonExtendedOptions.of("Roles", "Manage role permissions", RolesHandler.PERMISSION, RolesHandler.PATH, CSS.ICON_ADMIN_GEAR);
	private static final ButtonExtendedOptions sysOptionUpdate = ButtonExtendedOptions.of("Update", "Check for updates", AppUpdateHandler.PERMISSION, AppUpdateHandler.PATH, CSS.ICON_ARROW_REPEAT);
	private static final ButtonExtendedOptions sysOptionServices = ButtonExtendedOptions.of("Services", "Manage internal services", ServicesHandler.PERMISSION, ServicesHandler.PATH, CSS.ICON_SLIDERS);
	private static final ButtonExtendedOptions sysOptionDBBackup = ButtonExtendedOptions.of("Database Management", "Backup/restore database", null, ServicesHandler.PATH, CSS.ICON_DATABASE_CHECK);

	private static final ButtonExtendedOptions advOptionLog = ButtonExtendedOptions.of("Logging", "View system logs", LoggingHandler.PERMISSION, LoggingHandler.PATH, CSS.ICON_FILE_TEXT);
	private static final ButtonExtendedOptions advOptionSession = ButtonExtendedOptions.of("Sessions", "View login sessions", SessionViewerHandler.PERMISSION, SessionViewerHandler.PATH, CSS.ICON_PERSON_CARD);
	private static final ButtonExtendedOptions advOptionDatabase = ButtonExtendedOptions.of("Database", "Execute SQL", DatabaseHandler.PERMISSION, DatabaseHandler.PATH, CSS.ICON_DATABASE);
	private static final ButtonExtendedOptions advOptionExt = ButtonExtendedOptions.of("Processes", "View external processes", ExternalProcessHandler.PERMISSION, ExternalProcessHandler.PATH, CSS.ICON_TERMINAL);
	private static final ButtonExtendedOptions advOptionWSCon = ButtonExtendedOptions.of("Websocket Connnections", "View open WebSockets", WebsocketListConnectionAdminHandler.PERMISSION, WebsocketListConnectionAdminHandler.PATH, CSS.ICON_DIAGRAM_2);
	private static final ButtonExtendedOptions advOptionWSTest = ButtonExtendedOptions.of("Websocket Tester", "Test WebSockets", WebsocketTestAdminHandler.PERMISSION, WebsocketTestAdminHandler.PATH, CSS.ICON_DIAGRAM_3);

	static Void content(Div<?> view, BodyModel model) {
		final RecordUser currentUser = model.getUserViaSession();
		final ButtonExtendedGroupOptions groupOptionSystem = ButtonExtendedGroupOptions.of("System", CSS.ICON_GEAR, currentUser, sysOptionSetting, sysOptionUsers, sysOptionRoles, sysOptionUpdate, sysOptionServices, sysOptionDBBackup);
		final ButtonExtendedGroupOptions groupOptionAdvanced = ButtonExtendedGroupOptions.of("Advanced", CSS.ICON_TOOLS, currentUser, advOptionLog, advOptionSession, advOptionDatabase, advOptionExt, advOptionWSCon, advOptionWSTest);
		view
		.div()
			.of(o -> ButtonExtendedGroup.content(o, groupOptionSystem))
			.of(o -> ButtonExtendedGroup.content(o, groupOptionAdvanced))
		.__() //  div
		.hr().__()
		.of(parent -> DashboardContainer.content(parent, innerDiv -> {
			DashboardVersion.content(innerDiv, DashboardVersionOptions.of(true));
		}));

		return null;
	}

	@Override
	public void requestHandle(HttpExchange he, BodyModel bm) throws IOException, InterruptedException, ExecutionException {
		HeadModel thm = HeadModel.of(NAME);
		thm.addCSS(Asset.CSS_PIE);
		BreadCrumbs crumbs = new BreadCrumbs().add(NAME, PATH);
		TemplatePageModel tpm = TemplatePageModel.of(AdminHandler::content, null, thm, SelectedPage.Admin, bm, crumbs);
		requestHandleCompletePage(he, tpm);
	}

	@Override
	public Permission getHandlePermission() {
		return null;
	}

}
