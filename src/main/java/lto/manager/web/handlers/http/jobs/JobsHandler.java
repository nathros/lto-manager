package lto.manager.web.handlers.http.jobs;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.xmlet.htmlapifaster.Div;

import com.sun.net.httpserver.HttpExchange;

import lto.manager.common.database.Database;
import lto.manager.common.database.tables.TableJobs;
import lto.manager.common.database.tables.records.RecordJob;
import lto.manager.web.handlers.http.BaseHTTPHandler;
import lto.manager.web.handlers.http.templates.TemplatePage.SelectedPage;
import lto.manager.web.handlers.http.templates.TemplatePage.TemplatePageModel;
import lto.manager.web.handlers.http.templates.models.BodyModel;
import lto.manager.web.handlers.http.templates.models.HeadModel;
import lto.manager.web.resource.CSS;

public class JobsHandler extends BaseHTTPHandler {
	public static final String PATH = "/jobs";
	//private final static String STOP = "stop";
	//private final static String START = "start";

	static Void content(Div<?> view, BodyModel model) {
		//final String stop = model.getQueryNoNull(STOP);
		//final String start = model.getQueryNoNull(START);

		List<RecordJob> results = new ArrayList<RecordJob>();
		try {
			results = TableJobs.getAtAll(Database.connection);
		} catch (SQLException e) {
		}

		final List<RecordJob> jobs = results;

		view
			.div().of(div -> {
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
		return null;
	}

	@Override
	public void requestHandle(HttpExchange he) throws IOException {
		HeadModel thm = HeadModel.of("Jobs");
		TemplatePageModel tpm = TemplatePageModel.of(JobsHandler::content, thm, SelectedPage.Jobs, BodyModel.of(he, null));
		requestHandleCompletePage(he, tpm);
	}

}
