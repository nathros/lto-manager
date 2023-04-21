package lto.manager.web.handlers.http.jobs;

import java.io.IOException;

import org.xmlet.htmlapifaster.Div;
import org.xmlet.htmlapifaster.EnumTypeButtonType;
import org.xmlet.htmlapifaster.EnumTypeInputType;

import com.sun.net.httpserver.HttpExchange;

import lto.manager.common.Util;
import lto.manager.common.database.tables.records.RecordJob.RecordJobType;
import lto.manager.web.handlers.http.BaseHTTPHandler;
import lto.manager.web.handlers.http.fetchers.hostfiles.FileList;
import lto.manager.web.handlers.http.fetchers.hostfiles.FileListModel;
import lto.manager.web.handlers.http.fetchers.hostfiles.FileListOptions;
import lto.manager.web.handlers.http.templates.TemplatePage.SelectedPage;
import lto.manager.web.handlers.http.templates.TemplatePage.TemplatePageModel;
import lto.manager.web.handlers.http.templates.models.BodyModel;
import lto.manager.web.handlers.http.templates.models.HeadModel;
import lto.manager.web.resource.Asset;
import lto.manager.web.resource.CSS;
import lto.manager.web.resource.HTML;

public class JobsAddNewHandler extends BaseHTTPHandler {
	public static final String PATH = "/jobs/add";

	public static final String NAME = "name";
	public static final String COMMENT = "comment";
	public static final String START = "start";
	public static final String TYPE = "type";

	private static final String TAB1 = "tab1";
	private static final String TAB2 = "tab2";
	private static final String TAB3 = "tab3";

	static Void content(Div<?> view, BodyModel model) {
		final int depth = 1;
		final String name = model.getQueryNoNull(NAME);
		final String comment = model.getQueryNoNull(COMMENT);
		final String type = model.getQueryNoNull(TYPE);
		final String start = model.getQueryNoNull(TYPE);
		final int typeIndex = type.equals("") ? -1 : Integer.valueOf(type);
		final String fileTree = Util.getWorkingDir().getAbsolutePath() + "/testdir";
		final String fileTreeVirtual = "/";

		view
			.div()
				.div().attrClass(CSS.TABS_CONTAINER)
					.input().attrType(EnumTypeInputType.RADIO).attrId("tab1").attrName("css-tab") .__()
					.input().attrType(EnumTypeInputType.RADIO).attrId("tab2").attrName("css-tab").of(i -> HTML.check(i, true)).__()
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

						.b().attrStyle("width:150px;display:inline-block").text("Job Type: ").__()
						.select().attrName(TYPE).of(select -> {
							select.option().attrValue("").of(s -> HTML.option(s, typeIndex == -1, true)).text("Select").__();
							for (int i = 0; i < RecordJobType.values().length; i++) {
								final RecordJobType e = RecordJobType.values()[i];
								final var current = i;
								select.option().of(s -> HTML.option(s, current == typeIndex, false)).attrValue(String.valueOf(i)).text(e.toString()).__();
							}
						}).__().br().__()

						.b().attrStyle("width:150px;display:inline-block").text("Start: ").__()
						.input().attrType(EnumTypeInputType.DATETIME_LOCAL).attrName(START).of(input -> input.attrValue(start)).__()
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

			.__(); // div
		return null;
	}

	@Override
	public void requestHandle(HttpExchange he) throws IOException {
		HeadModel thm = HeadModel.of("Jobs");
		thm.AddCSS(Asset.CSS_TABS);
		thm.AddCSS(Asset.CSS_FILE_VIEW);
		thm.AddScript(Asset.JS_ADD_JOB);
		thm.AddScript(Asset.JS_FILE_VIEW);
		TemplatePageModel tpm = TemplatePageModel.of(JobsAddNewHandler::content, thm, SelectedPage.Jobs, BodyModel.of(he, null));
		requestHandleCompletePage(he, tpm);
	}

}
