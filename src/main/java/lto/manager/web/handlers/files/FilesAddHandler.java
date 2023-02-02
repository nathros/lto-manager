package lto.manager.web.handlers.files;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;

import org.xmlet.htmlapifaster.EnumTypeButtonType;
import org.xmlet.htmlapifaster.EnumTypeInputType;

import com.sun.net.httpserver.HttpExchange;

import htmlflow.DynamicHtml;
import lto.manager.common.Util;
import lto.manager.common.fileselector.PathTree;
import lto.manager.web.handlers.BaseHandler;
import lto.manager.web.handlers.templates.TemplateFileList;
import lto.manager.web.handlers.templates.TemplatePage;
import lto.manager.web.handlers.templates.TemplatePage.SelectedPage;
import lto.manager.web.handlers.templates.TemplatePage.TemplatePageModel;
import lto.manager.web.handlers.templates.models.BodyModel;
import lto.manager.web.handlers.templates.models.HeadModel;
import lto.manager.web.resource.Asset;

public class FilesAddHandler extends BaseHandler {
	public static final String PATH = "/files/add";

	public static DynamicHtml<BodyModel> view = DynamicHtml.view(FilesAddHandler::body);
	public static final String DIR = "dir";
	private static final String TAPE_ID = "tapeid";
	public static final String FILE_SELECTED = "f";

	static void body(DynamicHtml<BodyModel> view, BodyModel model) {
		final String dir = model.getQueryNoNull(DIR);
		final String tapeId = model.getQueryNoNull(TAPE_ID);

		PathTree tmpTree = null;
		if (!"".equals(dir)) {
			tmpTree = new PathTree(dir, 0);
		} else {
			tmpTree = new PathTree(Util.getWorkingDir().getAbsolutePath() + "/testdir", 0);
		}

		final PathTree fileTree = tmpTree;

		view
			.div()
				.form()
					.fieldset().dynamic(fieldset -> {
						File currentDir = new File(dir);
						if (dir.equals("")) currentDir = Util.getWorkingDir();

						fieldset.label().text("DIR: " + currentDir.getAbsolutePath()).__().br().__();
						fieldset.label().a().attrHref("?" + DIR + "=" + Util.encodeUrl(currentDir.getParent())).text("&cularr; UP").__().__();
						fieldset.br().__();
						fieldset.hr().__();

						if (fileTree != null) {
							fieldset.p().text(fileTree.getFile().getName()).__();
							view.addPartial(TemplateFileList.view, fileTree);
						}
					}).__()
				.__()

				.form()
					.b().text("Directory: ").__()
					.input().attrStyle("width:26rem").attrType(EnumTypeInputType.TEXT).attrName(DIR).dynamic(input -> input.attrValue(dir)).__()
					.text("Copy from above DIR")
					.br().__()
					.b().text("Tape ID: ").__()
					.input().attrType(EnumTypeInputType.TEXT).attrName(TAPE_ID).dynamic(input -> input.attrValue(tapeId)).__()
					.button().attrType(EnumTypeButtonType.SUBMIT).text("Submit").__()
				.__()
			.__(); // div
	}

	@Override
	public void requestHandle(HttpExchange he) throws IOException {
		try {
			HeadModel thm = HeadModel.of("Files Add");
			thm.AddCSS(Asset.CSS_FILE_VIEW);
			thm.AddScript(Asset.JS_FILE_VIEW);
			TemplatePageModel tepm = TemplatePageModel.of(view, thm, SelectedPage.Files, BodyModel.of(he, null));
			String response = TemplatePage.view.render(tepm);

			he.sendResponseHeaders(HttpURLConnection.HTTP_OK, response.length());
			OutputStream os = he.getResponseBody();
			os.write(response.getBytes());
			os.close();
		} catch (Exception e) {
			view = DynamicHtml.view(FilesAddHandler::body);
			throw e;
		}
	}
}
