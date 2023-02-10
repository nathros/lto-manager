package lto.manager.web.handlers.jobs;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;

import org.xmlet.htmlapifaster.EnumTypeButtonType;
import org.xmlet.htmlapifaster.EnumTypeInputType;

import com.sun.net.httpserver.HttpExchange;

import htmlflow.DynamicHtml;
import lto.manager.common.RsyncJob;
import lto.manager.web.handlers.BaseHandler;
import lto.manager.web.handlers.templates.TemplatePage;
import lto.manager.web.handlers.templates.TemplatePage.SelectedPage;
import lto.manager.web.handlers.templates.TemplatePage.TemplatePageModel;
import lto.manager.web.handlers.templates.models.BodyModel;
import lto.manager.web.handlers.templates.models.HeadModel;
import lto.manager.web.resource.CSS;

public class JobsHandler extends BaseHandler {
	public static final String PATH = "/jobs";
	public static DynamicHtml<BodyModel> view = DynamicHtml.view(JobsHandler::body);

	private final static String STOP = "stop";
	private final static String START = "start";

	private static RsyncJob job = new RsyncJob();

	static void body(DynamicHtml<BodyModel> view, BodyModel model) {
		final String stop = model.getQueryNoNull(STOP);
		final String start = model.getQueryNoNull(START);

		try {
			if (stop.equals(BodyModel.QUERY_ON)) job.stop();
			if (start.equals(BodyModel.QUERY_ON)) job.start();
		} catch (Exception e) {
			e.printStackTrace();
		}

		final var info = job.getLatestInfo();

		view
			.div().dynamic(div -> {
				div.a().attrClass(CSS.BUTTON).attrHref(JobsAddNewHandler.PATH).text("Add new job").__();
				if (job.operationInProgress()) div.p().text("Job Running").__().br().__();
				else div.p().text("Job NOT Running").__().br().__();
				div.p().text("Total copied: " + info.getTotalCopied()).__();
				div.p().text("Percent: " + info.getPercentCompleted()).__();
				div.p().text("Speed: " + info.getTransferSpeed()).__();
				div.p().text("ETA: " + info.getEstimatedTimeRemaining()).__();

				String exitCodeStr = "";
				Integer exitCode = job.getExitCode();
				if (exitCode != null) exitCodeStr = String.valueOf(exitCode);
				div.p().text("Exit Code: " + exitCodeStr).__();

				div.hr().__();
				div.p().text("err: " + job.getLatestError()).__();
				div
					.form()
						.input().attrType(EnumTypeInputType.CHECKBOX).attrName(STOP).__()
						.button().attrType(EnumTypeButtonType.SUBMIT).text("Stop").__()
						.input().attrType(EnumTypeInputType.CHECKBOX).attrName(START).__()
						.button().attrType(EnumTypeButtonType.SUBMIT).text("Start").__()
					.__();
				div.form().button().attrType(EnumTypeButtonType.SUBMIT).text("Refresh").__().__();
			}).__(); //  div

	}

	@Override
	public void requestHandle(HttpExchange he) throws IOException {
		try {
			HeadModel thm = HeadModel.of("Jobs");
			TemplatePageModel tepm = TemplatePageModel.of(view, thm, SelectedPage.Jobs, BodyModel.of(he, null));
			String response = TemplatePage.view.render(tepm);

			he.sendResponseHeaders(HttpURLConnection.HTTP_OK, response.length());
			OutputStream os = he.getResponseBody();
			os.write(response.getBytes());
			os.close();
		} catch (Exception e) {
			view = DynamicHtml.view(JobsHandler::body);
			throw e;
		}
	}

}
