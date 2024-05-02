package lto.manager.web.handlers.http.pages.admin;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import org.xmlet.htmlapifaster.Div;

import com.sun.net.httpserver.HttpExchange;

import lto.manager.Version;
import lto.manager.web.handlers.http.BaseHTTPHandler;
import lto.manager.web.handlers.http.pages.admin.externalprocess.ExternalProcessHandler;
import lto.manager.web.handlers.http.partial.pie.PieCPUUsage;
import lto.manager.web.handlers.http.partial.pie.PieJVMMemoryUsage;
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

	static Void content(Div<?> view, BodyModel model) {
		view
		.div().of(div -> {
			div
				.a().attrClass(CSS.BUTTON).attrHref(UpdateOptionsHandler.PATH).text("Change Settings").__()
				.a().attrClass(CSS.BUTTON).attrHref(ExternalProcessHandler.PATH).text("View External Processes").__()
				.a().attrClass(CSS.BUTTON).attrHref(SessionViewerHandler.PATH).text("View Login Sessions").__()
				.a().attrClass(CSS.BUTTON).attrHref(LoggingHandler.PATH).text("Logging").__()
				.a().attrClass(CSS.BUTTON).attrHref(AppUpdateHandler.PATH).text("Check Updates").__()
				.div().attrClass(CSS.CARD).addAttr(CSS.CARD_ATTRIBUTE, "System information")
					.div().attrClass(CSS.PIE_CONTAINER)
						.of(pie -> PieCPUUsage.content(pie))
						.of(pie -> PieJVMMemoryUsage.content(pie))
					.__()
				.__()
				.p().text("Version: " + Version.VERSION).__()
				.p().text("Tag: " + Version.TAG).__()
				.p().text("Branch: " + Version.BRANCH).__()
				.p().text("Commit: " + Version.COMMIT_HASH).__()
				.p().text("Build Date: " + Version.BUILD_DATE).__()
				.of(o -> {
					for (int i = 0; i < lto.manager.Version.DEPENDENCIES.size(); i += 3) {
						o.p().text(Version.DEPENDENCIES.get(i) + " : version : "
								+ lto.manager.Version.DEPENDENCIES.get(i + 1) + " " + lto.manager.Version.DEPENDENCIES.get(i + 2)).__();
					}
				})
			.__(); // div
		}).__(); //  div
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
