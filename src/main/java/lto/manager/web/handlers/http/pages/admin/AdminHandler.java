package lto.manager.web.handlers.http.pages.admin;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import org.xmlet.htmlapifaster.Div;

import com.sun.net.httpserver.HttpExchange;

import lto.manager.web.handlers.http.BaseHTTPHandler;
import lto.manager.web.handlers.http.pages.admin.externalprocess.ExternalProcessHandler;
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

	private static final ButtonExtendedOptions sysOptionSetting = ButtonExtendedOptions.of("Settings", "Change system settings", UpdateOptionsHandler.PATH, CSS.ICON_WRENCH);
	private static final ButtonExtendedOptions sysOptionUsers = ButtonExtendedOptions.of("Users", "Manage Users", UsersHandler.PATH, CSS.ICON_ADMIN);
	private static final ButtonExtendedOptions sysOptionRoles = ButtonExtendedOptions.of("Roles", "Manage role permissions", RolesHandler.PATH, CSS.ICON_ADMIN_GEAR);
	private static final ButtonExtendedOptions sysOptionUpdate = ButtonExtendedOptions.of("Update", "Check for updates", AppUpdateHandler.PATH, CSS.ICON_ARROW_REPEAT);
	private static final ButtonExtendedOptions sysOptionServices = ButtonExtendedOptions.of("Services", "Manage internal services", ServicesHandler.PATH, CSS.ICON_SLIDERS);
	private static final ButtonExtendedOptions sysOptionDBBackup = ButtonExtendedOptions.of("Database Management", "Backup/restore database", ServicesHandler.PATH, CSS.ICON_DATABASE_CHECK);
	private static final ButtonExtendedGroupOptions groupOptionSystem = ButtonExtendedGroupOptions.of("System", CSS.ICON_GEAR, sysOptionSetting, sysOptionUsers, sysOptionRoles, sysOptionUpdate, sysOptionServices, sysOptionDBBackup);

	private static final ButtonExtendedOptions advOptionLog = ButtonExtendedOptions.of("Logging", "View system logs", LoggingHandler.PATH, CSS.ICON_FILE_TEXT);
	private static final ButtonExtendedOptions advOptionSession = ButtonExtendedOptions.of("Sessions", "View login sessions", SessionViewerHandler.PATH, CSS.ICON_PERSON_CARD);
	private static final ButtonExtendedOptions advOptionDatabase = ButtonExtendedOptions.of("Database", "Execute SQL", DatabaseHandler.PATH, CSS.ICON_DATABASE);
	private static final ButtonExtendedOptions advOptionExt = ButtonExtendedOptions.of("Processes", "View external processes", ExternalProcessHandler.PATH, CSS.ICON_TERMINAL);
	private static final ButtonExtendedOptions advOptionWSCon = ButtonExtendedOptions.of("Websocket Connnections", "View open WebSockets", WebsocketListConnectionAdminHandler.PATH, CSS.ICON_DIAGRAM_2);
	private static final ButtonExtendedOptions advOptionWSTest = ButtonExtendedOptions.of("Websocket Tester", "Test WebSockets", WebsocketTestAdminHandler.PATH, CSS.ICON_DIAGRAM_3);
	private static final ButtonExtendedGroupOptions groupOptionAdvanced = ButtonExtendedGroupOptions.of("Advanced", CSS.ICON_TOOLS, advOptionLog, advOptionSession, advOptionDatabase, advOptionExt, advOptionWSCon, advOptionWSTest);

	static Void content(Div<?> view, BodyModel model) {
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
	public void requestHandle(HttpExchange he) throws IOException, InterruptedException, ExecutionException {
		HeadModel thm = HeadModel.of(NAME);
		thm.addCSS(Asset.CSS_PIE);
		BreadCrumbs crumbs = new BreadCrumbs().add(NAME, PATH);
		TemplatePageModel tpm = TemplatePageModel.of(AdminHandler::content, null, thm, SelectedPage.Admin, BodyModel.of(he, null), crumbs);
		requestHandleCompletePage(he, tpm);
	}

}
