package lto.manager.web.handlers.http.pages.admin;

import java.io.IOException;
import java.net.InetAddress;
import java.util.concurrent.ExecutionException;
import java.util.function.Supplier;

import org.xmlet.htmlapifaster.Div;

import com.sun.net.httpserver.HttpExchange;

import lto.manager.Version;
import lto.manager.web.handlers.http.BaseHTTPHandler;
import lto.manager.web.handlers.http.pages.admin.externalprocess.ExternalProcessHandler;
import lto.manager.web.handlers.http.partial.components.ButtonExtended.ButtonExtendedOptions;
import lto.manager.web.handlers.http.partial.components.ButtonExtendedGroup;
import lto.manager.web.handlers.http.partial.components.ButtonExtendedGroup.ButtonExtendedGroupOptions;
import lto.manager.web.handlers.http.templates.TemplatePage.BreadCrumbs;
import lto.manager.web.handlers.http.templates.TemplatePage.SelectedPage;
import lto.manager.web.handlers.http.templates.TemplatePage.TemplatePageModel;
import lto.manager.web.handlers.http.templates.models.BodyModel;
import lto.manager.web.handlers.http.templates.models.HeadModel;
import lto.manager.web.resource.Asset;
import lto.manager.web.resource.CSS;

public class AdminHandler extends BaseHTTPHandler {
	public static final String PATH = "/admin";
	public static final String NAME = "Admin";

	private static final ButtonExtendedOptions sysOptionSetting = ButtonExtendedOptions.of("Settings", "Change system settings", UpdateOptionsHandler.PATH, CSS.ICON_WRENCH);
	private static final ButtonExtendedOptions sysOptionUpdate = ButtonExtendedOptions.of("Update", "Check for updates", AppUpdateHandler.PATH, CSS.ICON_ARROW_REPEAT);
	private static final ButtonExtendedGroupOptions groupOptionSystem = ButtonExtendedGroupOptions.of("System", CSS.ICON_GEAR, sysOptionSetting, sysOptionUpdate);

	private static final ButtonExtendedOptions advOptionLog = ButtonExtendedOptions.of("Logging", "View system logs", LoggingHandler.PATH, CSS.ICON_FILE_TEXT);
	private static final ButtonExtendedOptions advOptionSession = ButtonExtendedOptions.of("Sessions", "View login sessions", SessionViewerHandler.PATH, CSS.ICON_PERSON_CARD);
	private static final ButtonExtendedOptions advOptionDatabase = ButtonExtendedOptions.of("Database", "Execute SQL", DatabaseHandler.PATH, CSS.ICON_DATABASE);
	private static final ButtonExtendedOptions advOptionExt = ButtonExtendedOptions.of("Processes", "View external processes", ExternalProcessHandler.PATH, CSS.ICON_TERMINAL);
	private static final ButtonExtendedOptions advOptionWSCon = ButtonExtendedOptions.of("Websocket Connnections", "View open WebSockets", WebsocketListConnectionAdminHandler.PATH, CSS.ICON_DIAGRAM_2);
	private static final ButtonExtendedOptions advOptionWSTest = ButtonExtendedOptions.of("Websocket Tester", "Test WebSockets", WebsocketTestAdminHandler.PATH, CSS.ICON_DIAGRAM_3);
	private static final ButtonExtendedGroupOptions groupOptionAdvanced = ButtonExtendedGroupOptions.of("Advanced", CSS.ICON_TOOLS, advOptionLog, advOptionSession, advOptionDatabase, advOptionExt, advOptionWSCon, advOptionWSTest);

	private static final Supplier<String> hostNameSupplier = () -> {
		try {
			return InetAddress.getLocalHost().getHostName();
		} catch (IOException e) {
			return "Not found";
		}
	};

	static Void content(Div<?> view, BodyModel model) {
		final String hostname = hostNameSupplier.get();
		view
		.div()
			.of(o -> ButtonExtendedGroup.content(o, groupOptionSystem))
			.of(o -> ButtonExtendedGroup.content(o, groupOptionAdvanced))
		.__() //  div
		.hr().__()
		.div()
			.attrClass(CSS.CARD_CONTAINER)
			.div()
				.attrClass(CSS.CARD)
				.h3().text("System Information").__()
				.h4().text("Overview").__()
				.div()
					.attrClass("system-info")
					.b().text("Version: ").__()
					.span().text(Version.VERSION).__()
					.b().text("Build Date: ").__()
					.span().text(Version.BUILD_DATE).__()
					.b().text("Tag: ").__()
					.span().text(Version.TAG).__()
					.b().text("Version: ").__()
					.span().text(Version.VERSION).__()
					.b().text("Hostname: ").__()
					.span().text(hostname).__()
				.__()
				.h4().text("Libraries").__()
				.div()
					.attrClass("system-info")
					.of(div -> {
						for (int i = 0; i < Version.DEPENDENCIES.size(); i += 3) {
							div.b().text(Version.DEPENDENCIES.get(i)).__();
							div
								.span()
									.text(Version.DEPENDENCIES.get(i + 1) + ": ")
									.a()
										.attrStyle("font-size:small")
										.attrTarget("_blank")
										.attrHref(Version.DEPENDENCIES.get(i + 2))
										.text(Version.DEPENDENCIES.get(i + 2))
									.__()
								.__();
						}
					})
				.__()
			.__()

		.__(); // div
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
