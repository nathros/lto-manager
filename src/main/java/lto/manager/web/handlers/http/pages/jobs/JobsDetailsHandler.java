package lto.manager.web.handlers.http.pages.jobs;

import java.io.IOException;
import java.sql.SQLException;
import java.util.concurrent.ExecutionException;

import org.xmlet.htmlapifaster.Div;

import com.sun.net.httpserver.HttpExchange;

import lto.manager.common.database.Database;
import lto.manager.common.database.jobs.BackupJob;
import lto.manager.common.database.jobs.JobBase;
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
		JobBase result = null;
		try {
			int number = Integer.parseInt(id);
			result = Database.getJobAtID(number);
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		final JobBase jobBase = result;

		switch (jobBase.getRecordJob().getType()) {
		case BACKUP: {
			final BackupJob job = (BackupJob)jobBase;
			view
				.div()
					.button().attrClass(CSS.BUTTON).attrOnclick("history.back()").text("Back").__()
					.p().text("Name: " + job.getRecordJob().getName()).__()
					.p().text("Type: " + job.getRecordJob().getType().toString()).__()
					.p().text("Start: " + job.getRecordJob().getStartDateTimeStr()).__()
					.p().text("Completed: " + job.getRecordJob().getEndDateTimeStr()).__()
					.p().text("Comment: " + job.getRecordJob().getComment()).__()
					.br().__()
					.div().of(div -> {
						for (RecordJobMetadata meta: job.getRecordJobMetadata()) {
							div.b().text(meta.getKey()).__()
							.br().__()
							.p().text(meta.getValue()).__()
							.hr().__();
						}
					}).__()
				.__(); //  div
			break;
		}
		default:
			view
				.div().text("Not supported yet").__();
		}

		return null;
	}

	@Override
	public void requestHandle(HttpExchange he) throws IOException, InterruptedException, ExecutionException {
		HeadModel thm = HeadModel.of("Jobs");
		TemplatePageModel tpm = TemplatePageModel.of(JobsDetailsHandler::content, thm, SelectedPage.Jobs, BodyModel.of(he, null), null);
		requestHandleCompletePage(he, tpm);
	}

}
