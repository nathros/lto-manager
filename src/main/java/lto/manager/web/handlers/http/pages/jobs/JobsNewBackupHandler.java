package lto.manager.web.handlers.http.pages.jobs;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

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
import lto.manager.web.handlers.http.templates.TemplatePage.SelectedPage;
import lto.manager.web.handlers.http.templates.TemplatePage.TemplatePageModel;
import lto.manager.web.handlers.http.templates.models.BodyModel;
import lto.manager.web.handlers.http.templates.models.HeadModel;
import lto.manager.web.resource.Asset;
import lto.manager.web.resource.CSS;
import lto.manager.web.resource.HTML;
import lto.manager.web.resource.Query;

public class JobsNewBackupHandler extends BaseHTTPHandler {
	public static final String PATH = "/jobs/add";

	public static final String NAME = "name";
	public static final String COMMENT = "comment";
	public static final String START = "start";
	public static final String IMMEDIATE = "immediate";

	private static final String TAB1 = "tab1";
	private static final String TAB2 = "tab2";
	private static final String TAB3 = "tab3";

	static Void content(Div<?> view, BodyModel model) {
		final int depth = 1;
		final String name = model.getQueryNoNull(NAME);
		final String comment = model.getQueryNoNull(COMMENT).replaceAll("\t", "");
		final String start = model.getQueryNoNull(START);
		final String immediate = model.getQueryNoNull(IMMEDIATE);
		final String fileTree = Util.getWorkingDir().getAbsolutePath() + "/testdir";
		final String fileTreeVirtual = "/";

		if (model.isPOSTMethod()) {
			final List<String> sourceFiles = model.getQueryArrayNotNull(FileListModel.FILE_SELECTED);
			final String destDir = model.getQueryNoNull(FileListModel.BREADCRUMBS_LAST + FileListModel.getIDPostFix(true));
			final String sourceDir = model.getQueryNoNull(FileListModel.BREADCRUMBS_LAST + FileListModel.getIDPostFix(false));
			LocalDateTime startDateTime = immediate.equals(Query.CHECKED) || start.equals("") ? null : LocalDateTime.parse(start);
			RecordJob job = RecordJob.of(name, RecordJobType.BACKUP, startDateTime, comment);
			BackupJob bJob = new BackupJob(job, sourceFiles, destDir, sourceDir);
			try {
				TableJobs.addNewJob(Database.connection, bJob);
			} catch (SQLException e) {
				e.printStackTrace();
			}
			view.div().of(div -> {
				Div<?> d = div;
				for (String file: sourceFiles) {
					d = d.p().text(file).__();
				}
				d.hr().__();
				d.p().text("sour: " + sourceDir).__();
				d.p().text("dest: " + destDir).__();
			}).__();
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
						.b().attrStyle("width:150px;display:inline-block").text("Job Name: ").__()
						.input().attrType(EnumTypeInputType.TEXT).attrName(NAME).of(input -> input.attrValue(name)).__()
						.br().__()

						.b().attrStyle("width:150px;display:inline-block").text("Start: ").__()
						.input()
							.attrType(EnumTypeInputType.DATETIME_LOCAL)
							.attrId(START)
							.attrName(START)
							.of(input -> input.attrValue(start))
						.__()
						.input()
							.attrType(EnumTypeInputType.CHECKBOX)
							.attrId(IMMEDIATE)
							.attrName(IMMEDIATE)
							.attrOnclick("onSelectImmediate(this)")
							.of(input -> input.attrValue(immediate))
						.__()
						.label().attrFor(IMMEDIATE).text("Immediate").__()
						.br().__()

						.b().attrStyle("width:150px;display:inline-block").text("Comment: ").__()
						.textarea().attrName(COMMENT).text(comment).__()
						.br().__()

						.label().attrFor(TAB2).attrClass(CSS.BUTTON)
							.text("Next")
						.__()
					.__() // div TABS_CONTENT

					// TAB 2
					.div().attrClass(CSS.TABS_CONTENT)
						.div().of(div ->
							FileList.content(div, new FileListModel(fileTree, FileListOptions.of(true, "", null, depth, false, false)))
						).__()
					.__()

					// TAB 3
					.div().attrClass(CSS.TABS_CONTENT)
						.div().of(div ->
							FileList.content(div, new FileListModel(fileTreeVirtual, FileListOptions.of(true, "", null, depth, true, true)))
						).__()
						.button().attrClass(CSS.BUTTON).attrType(EnumTypeButtonType.SUBMIT).text("Submit").__()
					.__()
				.__() // div TABS_CONTAINER

			.__().__(); // div form
		return null;
	}

	@Override
	public void requestHandle(HttpExchange he) throws IOException {
		HeadModel thm = HeadModel.of("Jobs");
		thm.AddCSS(Asset.CSS_TABS);
		thm.AddCSS(Asset.CSS_FILE_VIEW);
		thm.AddScript(Asset.JS_ADD_JOB);
		thm.AddScript(Asset.JS_FILE_VIEW);
		TemplatePageModel tpm = TemplatePageModel.of(JobsNewBackupHandler::content, thm, SelectedPage.Jobs, BodyModel.of(he, null));
		requestHandleCompletePage(he, tpm);
	}

}
