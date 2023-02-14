package lto.manager.web.handlers.jobs;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.sun.net.httpserver.HttpExchange;

import htmlflow.DynamicHtml;
import lto.manager.common.database.Database;
import lto.manager.common.database.tables.TableJobs;
import lto.manager.common.database.tables.records.RecordJob;
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

	static void body(DynamicHtml<BodyModel> view, BodyModel model) {
		final String stop = model.getQueryNoNull(STOP);
		final String start = model.getQueryNoNull(START);

		List<RecordJob> results = new ArrayList<RecordJob>();
		try {
			results = TableJobs.getAtAll(Database.connection);
		} catch (SQLException e) {
		}

		final List<RecordJob> jobs = results;

		view
			.div().dynamic(div -> {
				div
					.a().attrClass(CSS.BUTTON).attrHref(JobsAddNewHandler.PATH).text("Add new job").__()
					.table()
						.tr()
							.th().text("ID").__()
							.th().text("Name").__()
							.th().text("Type").__()
						.__()
						.of(row -> {
							for (RecordJob job: jobs) {
								row
									.tr()
										.td().text(job.getID()).__()
										.td().text(job.getName()).__()
										.td().text(job.getType()).__()
									.__();
							}
						});
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
