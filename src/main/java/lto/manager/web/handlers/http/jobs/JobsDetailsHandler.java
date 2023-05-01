package lto.manager.web.handlers.http.jobs;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.xmlet.htmlapifaster.Div;

import com.sun.net.httpserver.HttpExchange;

import lto.manager.common.database.Database;
import lto.manager.common.database.tables.TableJobs;
import lto.manager.common.database.tables.TableJobsMetadata;
import lto.manager.common.database.tables.records.RecordJob;
import lto.manager.common.database.tables.records.RecordJobMetadata;
import lto.manager.web.handlers.http.BaseHTTPHandler;
import lto.manager.web.handlers.http.templates.TemplatePage.SelectedPage;
import lto.manager.web.handlers.http.templates.TemplatePage.TemplatePageModel;
import lto.manager.web.handlers.http.templates.models.BodyModel;
import lto.manager.web.handlers.http.templates.models.HeadModel;
import lto.manager.web.resource.CSS;

public class JobsDetailsHandler extends BaseHTTPHandler {
	public static final String PATH = "/jobs/details";
	public final static String ID = "id";

	static Void content(Div<?> view, BodyModel model) {
		final String id = model.getQuery(ID);
		RecordJob result = null;
		final List<RecordJobMetadata> metaResult = new ArrayList<RecordJobMetadata>();
		try {
			int number = Integer.parseInt(id);
			result = TableJobs.getAtID(Database.connection, number);
			metaResult.addAll(TableJobsMetadata.getAllMetadataByJob(Database.connection, result.getID()));
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (NumberFormatException e) {}


		final RecordJob job = result;

		view
			.div()
				.button().attrClass(CSS.BUTTON).attrOnclick("history.back()").text("Back").__()
				.p().text("Name: " + job.getName()).__()
				.p().text("Type: " + job.getType().toString()).__()
				.p().text("Start: " + job.getStartDateTimeStr()).__()
				.p().text("Completed: " + job.getEndDateTimeStr()).__()
				.p().text("Comment: " + job.getComment()).__()
				.br().__()
				.div().of(div -> {
					for (RecordJobMetadata meta: metaResult) {
						div.b().text(meta.getKey()).__()
						.br().__()
						.p().text(meta.getValue()).__()
						.hr().__();
					}
				}).__()
			.__(); //  div
		return null;
	}

	@Override
	public void requestHandle(HttpExchange he) throws IOException {
		HeadModel thm = HeadModel.of("Jobs");
		TemplatePageModel tpm = TemplatePageModel.of(JobsDetailsHandler::content, thm, SelectedPage.Jobs, BodyModel.of(he, null));
		requestHandleCompletePage(he, tpm);
	}

}
