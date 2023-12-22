package lto.manager.web.handlers.http.pages.jobs;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import org.xmlet.htmlapifaster.Div;

import com.sun.net.httpserver.HttpExchange;

import lto.manager.web.handlers.http.BaseHTTPHandler;
import lto.manager.web.handlers.http.templates.TemplatePage.BreadCrumbs;
import lto.manager.web.handlers.http.templates.TemplatePage.SelectedPage;
import lto.manager.web.handlers.http.templates.TemplatePage.TemplatePageModel;
import lto.manager.web.handlers.http.templates.models.BodyModel;
import lto.manager.web.handlers.http.templates.models.HeadModel;
import lto.manager.web.resource.CSS;

public class JobsTypeHandler extends BaseHTTPHandler {
	public static final String PATH = "/jobs/type";
	public static final String NAME = "Job Type";

	static Void content(Div<?> view, BodyModel model) {
		view
			.div()
				.a().attrClass(CSS.BUTTON).attrHref(JobsNewBackupHandler.PATH).text("Backup").__()
				.a().attrClass(CSS.BUTTON + CSS.BACKGROUND_CAUTION).text("Restore").__()
				.a().attrClass(CSS.BUTTON + CSS.BACKGROUND_CAUTION).text("Delete").__()
			.__();
		return null;
	}

	@Override
	public void requestHandle(HttpExchange he) throws IOException, InterruptedException, ExecutionException {
		HeadModel thm = HeadModel.of(NAME);
		BreadCrumbs crumbs = new BreadCrumbs().add(JobsHandler.NAME, JobsHandler.PATH).add(NAME, PATH);
		TemplatePageModel tpm = TemplatePageModel.of(JobsTypeHandler::content, null, thm, SelectedPage.Jobs, BodyModel.of(he, null), crumbs);
		requestHandleCompletePage(he, tpm);
	}

}
