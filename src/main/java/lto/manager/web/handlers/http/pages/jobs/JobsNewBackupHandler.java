package lto.manager.web.handlers.http.pages.jobs;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.xmlet.htmlapifaster.Div;
import org.xmlet.htmlapifaster.EnumMethodType;
import org.xmlet.htmlapifaster.EnumTypeButtonType;
import org.xmlet.htmlapifaster.EnumTypeInputType;

import com.sun.net.httpserver.HttpExchange;

import lto.manager.common.Util;
import lto.manager.common.database.Database;
import lto.manager.common.database.jobs.BackupJob;
import lto.manager.common.database.tables.TableJobs;
import lto.manager.common.database.tables.records.RecordJob;
import lto.manager.common.database.tables.records.RecordJob.RecordJobType;
import lto.manager.web.handlers.http.BaseHTTPHandler;
import lto.manager.web.handlers.http.partial.filelist.FileList;
import lto.manager.web.handlers.http.partial.filelist.FileListModel;
import lto.manager.web.handlers.http.partial.filelist.FileListOptions;
import lto.manager.web.handlers.http.templates.TemplatePage.BreadCrumbs;
import lto.manager.web.handlers.http.templates.TemplatePage.SelectedPage;
import lto.manager.web.handlers.http.templates.TemplatePage.TemplatePageModel;
import lto.manager.web.handlers.http.templates.models.BodyModel;
import lto.manager.web.handlers.http.templates.models.HeadModel;
import lto.manager.web.resource.Asset;
import lto.manager.web.resource.CSS;
import lto.manager.web.resource.HTML;
import lto.manager.web.resource.Query;

public class JobsNewBackupHandler extends BaseHTTPHandler {
	public static final String PATH = "/jobs/add/";
	public static final String NAME = "Add Job";

	public static final String QNAME = "name";
	public static final String COMMENT = "comment";
	public static final String START = "start";
	public static final String IMMEDIATE = "immediate";
	public static final String TAPE_ID = "tape-id";

	private static final String TAB1 = "tab1";
	private static final String TAB2 = "tab2";
	private static final String TAB3 = "tab3";

	static Void content(Div<?> view, BodyModel model) {
		final int depth = 1;
		final int db = FileListOptions.showAll;
		final String name = model.getQueryNoNull(QNAME);
		final String comment = model.getQueryNoNull(COMMENT).replaceAll("\t", "");
		final String start = model.getQueryNoNull(START);
		final String immediate = model.getQueryNoNull(IMMEDIATE);
		final String fileTree = Util.getWorkingDir().getAbsolutePath() + "/testdir";
		final String fileTreeVirtual = "/";
		final String tapeID = model.getQueryNoNull(TAPE_ID);

		if (model.isPOSTMethod()) {
			final List<String> sourceFiles = model.getQueryArrayNotNull(FileListModel.FILE_SELECTED);
			final String destDir = model.getQueryNoNull(FileListModel.BREADCRUMBS_LAST + FileListModel.getIDPostFix(true));
			final String sourceDir = model.getQueryNoNull(FileListModel.BREADCRUMBS_LAST + FileListModel.getIDPostFix(false));
			LocalDateTime startDateTime = immediate.equals(Query.CHECKED) || start.equals("") ? null : LocalDateTime.parse(start);
			RecordJob job = RecordJob.of(name, RecordJobType.BACKUP, startDateTime, comment);
			try {
				BackupJob bJob = new BackupJob(job, sourceFiles, destDir, sourceDir, Integer.valueOf(tapeID));
				TableJobs.addNewJob(Database.connection, bJob);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}



		view
			.div().form().attrMethod(EnumMethodType.POST)
				.div().attrClass(CSS.TABS_CONTAINER)
					.input().attrType(EnumTypeInputType.RADIO).attrId("tab1").attrName("css-tab").of(i -> HTML.check(i, true)).__()
					.input().attrType(EnumTypeInputType.RADIO).attrId("tab2").attrName("css-tab").__()
					.input().attrType(EnumTypeInputType.RADIO).attrId("tab3").attrName("css-tab").__()
					.ul().attrClass(CSS.TABS_HEADERS)
						.li().attrClass(CSS.TABS_TAB)
							.label().attrFor(TAB1)
								.img().attrClass(CSS.TABS_SETUP_ICON).attrSrc("/").attrAlt("").__()
								.text("1: Setup")
							.__()
						.__()
						.li().attrClass(CSS.TABS_TAB)
							.label().attrFor(TAB2)
								.img().attrClass(CSS.TABS_SOURCE_ICON).attrSrc("/").attrAlt("").__()
								.text("2: Source")
							.__()
						.__()
						.li().attrClass(CSS.TABS_TAB)
							.label().attrFor(TAB3)
								.img().attrClass(CSS.TABS_DESTINATION_ICON).attrSrc("/").attrAlt("").__()
								.text("3: Destination")
							.__()
						.__()
					.__() // ul

					// TAB 1
					.div().attrClass(CSS.TABS_CONTENT)
						.div().attrClass(CSS.FORMS_CONTAINER)
							.b().text("Job Name: ").__()
							.input().attrType(EnumTypeInputType.TEXT).attrName(QNAME).of(input -> input.attrValue(name)).__()

							.b().text("Start: ").__()
							.input()
								.attrType(EnumTypeInputType.DATETIME_LOCAL)
								.attrId(START)
								.attrName(START)
								.of(input -> input.attrValue(start))
							.__()
							.label().attrFor(IMMEDIATE).text("Immediate").__()
							.input()
								.attrType(EnumTypeInputType.CHECKBOX)
								.attrId(IMMEDIATE)
								.attrName(IMMEDIATE)
								.attrOnclick("onSelectImmediate(this)")
								.of(input -> input.attrValue(immediate))
							.__()

							.b().text("Tape ID: ").__()
							.input().attrType(EnumTypeInputType.NUMBER).attrName(TAPE_ID).of(input -> input.attrValue(tapeID)).__()

							.b().text("Comment: ").__()
							.textarea().attrName(COMMENT).text(comment).__()

						.__() // div CSS.FORMS_FLEX
						.label().attrFor(TAB2).attrClass(CSS.BUTTON)
							.text("Next")
						.__()
					.__() // div TABS_CONTENT

					// TAB 2
					.div().attrClass(CSS.TABS_CONTENT)
						.div().of(div ->
							FileList.content(div, new FileListModel(fileTree, FileListOptions.of(true, "", null, depth, false, false, db)))
						).__()
					.__()

					// TAB 3
					.div().attrClass(CSS.TABS_CONTENT)
						.div().of(div ->
							FileList.content(div, new FileListModel(fileTreeVirtual, FileListOptions.of(true, "", null, depth, true, true, db)))
						).__()
						.button().attrClass(CSS.BUTTON).attrType(EnumTypeButtonType.SUBMIT).text("Submit").__()
					.__()
				.__() // div TABS_CONTAINER

			.__().__(); // div form
		return null;
	}

	@Override
	public void requestHandle(HttpExchange he) throws IOException, InterruptedException, ExecutionException {
		HeadModel thm = HeadModel.of(NAME);
		thm.addCSS(Asset.CSS_TABS).addCSS(Asset.CSS_FILE_VIEW).addCSS(Asset.CSS_FORMS);
		thm.addScriptDefer(Asset.JS_AJAX).addScript(Asset.JS_ADD_JOB).addScript(Asset.JS_FILE_VIEW);
		BreadCrumbs crumbs = new BreadCrumbs().add(JobsHandler.NAME, JobsHandler.PATH).add(JobsTypeHandler.NAME + " [Backup]", JobsTypeHandler.PATH).add(NAME, PATH);
		TemplatePageModel tpm = TemplatePageModel.of(JobsNewBackupHandler::content, null, thm, SelectedPage.Jobs, BodyModel.of(he, null), crumbs);
		requestHandleCompletePage(he, tpm);
	}

}
