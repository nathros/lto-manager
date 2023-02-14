package lto.manager.web.handlers.jobs;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;

import org.xmlet.htmlapifaster.EnumMethodType;
import org.xmlet.htmlapifaster.EnumTypeButtonType;
import org.xmlet.htmlapifaster.EnumTypeInputType;

import com.sun.net.httpserver.HttpExchange;

import htmlflow.DynamicHtml;
import lto.manager.common.Util;
import lto.manager.common.database.tables.records.RecordJob.RecordJobType;
import lto.manager.common.fileselector.PathTree;
import lto.manager.web.handlers.BaseHandler;
import lto.manager.web.handlers.templates.TemplateFileList;
import lto.manager.web.handlers.templates.TemplatePage;
import lto.manager.web.handlers.templates.TemplatePage.SelectedPage;
import lto.manager.web.handlers.templates.TemplatePage.TemplatePageModel;
import lto.manager.web.handlers.templates.models.BodyModel;
import lto.manager.web.handlers.templates.models.HeadModel;
import lto.manager.web.resource.Asset;
import lto.manager.web.resource.CSS;

public class JobsAddNewHandler extends BaseHandler {
	public static final String PATH = "/jobs/add";
	public static DynamicHtml<BodyModel> view = DynamicHtml.view(JobsAddNewHandler::body);

	public static final String NAME = "name";
	public static final String COMMENT = "comment";
	public static final String START = "start";
	public static final String TYPE = "type";

	private static final String TAB1 = "tab1";
	private static final String TAB2 = "tab2";
	private static final String TAB3 = "tab3";

	static void body(DynamicHtml<BodyModel> view, BodyModel model) {
		final String name = model.getQueryNoNull(NAME);
		final String comment = model.getQueryNoNull(COMMENT);
		final String type = model.getQueryNoNull(TYPE);
		final String start = model.getQueryNoNull(TYPE);
		final int typeIndex = type.equals("") ? -1 : Integer.valueOf(type);
		final var fileTree = new PathTree(Util.getWorkingDir().getAbsolutePath() + "/testdir", 0);

		view
			.div()
				.form().attrMethod(EnumMethodType.POST)
					.div().attrClass(CSS.TABS_CONTAINER)
						.input().attrType(EnumTypeInputType.RADIO).attrId("tab1").attrName("css-tab").attrChecked(true).__()
						.input().attrType(EnumTypeInputType.RADIO).attrId("tab2").attrName("css-tab").__()
						.input().attrType(EnumTypeInputType.RADIO).attrId("tab3").attrName("css-tab").__()
						.ul().attrClass(CSS.TABS_HEADERS)
							.li().attrClass(CSS.TABS_TAB)
								.label().attrFor(TAB1)
									.img().attrClass(CSS.TABS_SETUP_ICON).__()
									.text("1: Setup")
								.__()
							.__()
							.li().attrClass(CSS.TABS_TAB)
								.label().attrFor(TAB2)
									.img().attrClass(CSS.TABS_SOURCE_ICON).__()
									.text("2: Source")
								.__()
							.__()
							.li().attrClass(CSS.TABS_TAB)
								.label().attrFor(TAB3)
									.img().attrClass(CSS.TABS_DESTINATION_ICON).__()
									.text("3: Destination")
								.__()
							.__()
						.__() // ul

						.div().attrClass(CSS.TABS_CONTENT)
							.b().attrStyle("width:150px;display:inline-block").text("Job Name: ").__()
							.input().attrType(EnumTypeInputType.TEXT).attrName(NAME).dynamic(input -> input.attrValue(name)).__()
							.br().__()

							.b().attrStyle("width:150px;display:inline-block").text("Job Type: ").__()
							.select().attrName(TYPE).dynamic(select -> {
								select.option().attrValue("").attrSelected(typeIndex == -1).attrDisabled(true).text("Select").__();
								for (int i = 0; i < RecordJobType.values().length; i++) {
									final RecordJobType e = RecordJobType.values()[i];
									if (i == typeIndex) {
										select.option().attrSelected(true).attrValue(String.valueOf(i)).text(e.toString()).__();
									} else {
										select.option().attrValue(String.valueOf(i)).text(e.toString()).__();
									}
								}
							}).__().br().__()

							.b().attrStyle("width:150px;display:inline-block").text("Start: ").__()
							.input().attrType(EnumTypeInputType.DATETIME_LOCAL).attrName(START).dynamic(input -> input.attrValue(start)).__()
							.br().__()

							.b().attrStyle("width:150px;display:inline-block").text("Comment: ").__()
							.textarea().attrName(COMMENT).text(comment).__()
							.br().__()

							.label().attrFor(TAB2).attrClass(CSS.BUTTON)
								.text("Next")
							.__()
						.__() // div TABS_CONTENT


						.div().attrClass(CSS.TABS_CONTENT)
							.of(div -> {
								view.addPartial(TemplateFileList.view, fileTree);
							})
						.__()


						.div().attrClass(CSS.TABS_CONTENT)
							.button().attrClass(CSS.BUTTON).attrType(EnumTypeButtonType.SUBMIT).text("Submit").__()
						.__()
					.__() // div TABS_CONTAINER


				.__() // form
			.__(); // div
	}

	@Override
	public void requestHandle(HttpExchange he) throws IOException {
		try {
			HeadModel thm = HeadModel.of("Jobs");
			thm.AddCSS(Asset.CSS_TABS);
			thm.AddCSS(Asset.CSS_FILE_VIEW);
			thm.AddScript(Asset.JS_ADD_JOB);
			thm.AddScript(Asset.JS_FILE_VIEW);
			TemplatePageModel tepm = TemplatePageModel.of(view, thm, SelectedPage.Jobs, BodyModel.of(he, null));
			String response = TemplatePage.view.render(tepm);

			he.sendResponseHeaders(HttpURLConnection.HTTP_OK, response.length());
			OutputStream os = he.getResponseBody();
			os.write(response.getBytes());
			os.close();
		} catch (Exception e) {
			view = DynamicHtml.view(JobsAddNewHandler::body);
			throw e;
		}
	}

}
