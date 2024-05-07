package lto.manager.web.handlers.http.pages.dashboard;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import org.xmlet.htmlapifaster.Div;

import com.sun.net.httpserver.HttpExchange;

import lto.manager.web.handlers.http.BaseHTTPHandler;
import lto.manager.web.handlers.http.partial.dashboard.DashboardContainer;
import lto.manager.web.handlers.http.partial.dashboard.DashboardMemory;
import lto.manager.web.handlers.http.partial.dashboard.DashboardVersion;
import lto.manager.web.handlers.http.partial.dashboard.DashboardVersion.DashboardVersionOptions;
import lto.manager.web.handlers.http.templates.TemplatePage.SelectedPage;
import lto.manager.web.handlers.http.templates.TemplatePage.TemplatePageModel;
import lto.manager.web.handlers.http.templates.models.BodyModel;
import lto.manager.web.handlers.http.templates.models.HeadModel;
import lto.manager.web.resource.Asset;

public class DashboardHandler extends BaseHTTPHandler {
	public static final String PATH = "/dashboard";
	public static final String NAME = "Dashboard";

	static Void content(Div<?> view, BodyModel model) {
		view
			.div()
				.of(div -> DashboardContainer.content(div, innerDiv -> {
					DashboardVersion.content(innerDiv, DashboardVersionOptions.of(false));
					DashboardMemory.content(innerDiv/*, DashboardMemoryOptions.of(true)*/);
				})) // of
			.__();
		return null;
	}

	@Override
	public void requestHandle(HttpExchange he) throws IOException, InterruptedException, ExecutionException {
		HeadModel thm = HeadModel.of(NAME);
		thm.addCSS(Asset.CSS_PIE);
		thm.addScriptDefer(Asset.JS_DASHBOARD);
		TemplatePageModel tpm = TemplatePageModel.of(DashboardHandler::content, null, thm, SelectedPage.Dashboard, BodyModel.of(he, null), null);
		requestHandleCompletePage(he, tpm);
	}
}
