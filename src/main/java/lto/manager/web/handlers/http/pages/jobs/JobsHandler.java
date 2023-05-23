package lto.manager.web.handlers.http.pages.jobs;

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
import lto.manager.web.resource.JS;

public class JobsHandler extends BaseHTTPHandler {
	public static final String PATH = "/jobs";
	private final static String DELETE_ID = "del";

	static Void content(Div<?> view, BodyModel model) {
		final String del = model.getQueryNoNull(DELETE_ID);
		boolean delResult = false;
		if (!del.equals("")) {
			final int deleteID = Integer.parseInt(del);
			try {
				delResult = TableJobs.deleteJob(Database.connection, deleteID);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		final boolean delRes = delResult;


		List<RecordJob> results = new ArrayList<RecordJob>();
		try {
			results = TableJobs.getAtAll(Database.connection);
		} catch (SQLException e) {
			e.printStackTrace();
		}

		final List<RecordJob> jobs = results;
		final String detailsLink = JobsDetailsHandler.PATH + "?" + JobsDetailsHandler.ID + "=";
		final String deleteLink = JobsHandler.PATH + "?" + JobsHandler.DELETE_ID + "=";
		view
			.div().of(div -> {
				div
					.of(d -> {
						if (!del.equals("")) {
							d.p().text(delRes ? "success" : "failure").__();
						}
					})
					.a().attrClass(CSS.BUTTON).attrHref(JobsTypeHandler.PATH).text("Add new job").__()
					.table()
						.tr()
							.th().text("ID").__()
							.th().text("Name").__()
							.th().text("Type").__()
							.th().text("Scheduled Start").__()
							.th().text("Completed Date").__()
							.th().text("Comment").__()
							.th().text("Action").__()
						.__()
						.of(row -> {
							for (RecordJob job: jobs) {
								row
									.tr()
										.td().text(job.getID()).__()
										.td().text(job.getName()).__()
										.td().text(job.getType()).__()
										.td().text(job.getStartDateTimeStr()).__()
										.td().text(job.getEndDateTimeStr()).__()
										.td().text(job.getComment()).__()
										.td()
											.a()
												.attrClass(CSS.BUTTON)
												.attrHref(detailsLink + job.getID())
												.text("Details")
											.__()
											.a()
												.attrClass(CSS.BUTTON + CSS.BACKGROUND_CAUTION)
												.attrOnclick(JS.confirmToast(deleteLink + job.getID()))
												.text("Delete")
											.__()
										.__()
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
