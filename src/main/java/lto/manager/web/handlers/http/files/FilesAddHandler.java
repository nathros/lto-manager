package lto.manager.web.handlers.http.files;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.util.List;

import org.xmlet.htmlapifaster.EnumMethodType;
import org.xmlet.htmlapifaster.EnumTypeButtonType;
import org.xmlet.htmlapifaster.EnumTypeInputType;

import com.sun.net.httpserver.HttpExchange;

import htmlflow.DynamicHtml;
import lto.manager.common.Util;
import lto.manager.common.database.Database;
import lto.manager.common.fileselector.PathTree;
import lto.manager.web.handlers.http.BaseHTTPHandler;
import lto.manager.web.handlers.http.fetchers.hostfiles.HostFileListModel;
import lto.manager.web.handlers.http.templates.TemplatePage;
import lto.manager.web.handlers.http.templates.TemplatePage.SelectedPage;
import lto.manager.web.handlers.http.templates.TemplatePage.TemplatePageModel;
import lto.manager.web.handlers.http.templates.models.BodyModel;
import lto.manager.web.handlers.http.templates.models.HeadModel;
import lto.manager.web.resource.Asset;
import lto.manager.web.resource.CSS;

public class FilesAddHandler extends BaseHTTPHandler {
	public static final String PATH = "/files/add";

	public static DynamicHtml<BodyModel> view = DynamicHtml.view(FilesAddHandler::body);
	public static final String DIR = "dir";
	private static final String TAPE_ID = "tapeid";

	static void body(DynamicHtml<BodyModel> view, BodyModel model) {
		final String dir = model.getQueryNoNull(DIR);
		final String tapeId = model.getQueryNoNull(TAPE_ID);
		final List<String> files = model.getQueryArray(HostFileListModel.FILE_SELECTED);
		final String dirD = dir.equals("") ? Util.getWorkingDir().getAbsolutePath() + "/testdir" : dir;

		PathTree tmpTree = null;
		if (!"".equals(dir)) {
			tmpTree = new PathTree(dir, 0, 1);
		} else {
			tmpTree = new PathTree(Util.getWorkingDir().getAbsolutePath() + "/testdir", 0, 1);
		}

		if (model.isPOSTMethod()) {
			try {
				var id = Integer.valueOf(tapeId);
				Database.addFilesToTape(id, files, dirD);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		final PathTree fileTree = tmpTree;
		//final var finalView = view;

		try {
			view
				.div()
					.form().attrMethod(EnumMethodType.POST)
						.fieldset().dynamic(fieldset -> {
							File currentDir = new File(dir);
							if (dir.equals("")) currentDir = Util.getWorkingDir();
							fieldset.input().attrType(EnumTypeInputType.HIDDEN).attrName(DIR).attrValue(dirD).__();
							fieldset.label().text("DIR: " + currentDir.getAbsolutePath()).__().br().__();
							fieldset.label().a().attrHref("?" + DIR + "=" + Util.encodeUrl(currentDir.getParent())).text("&cularr; UP").__().__();
							fieldset.br().__();
							fieldset.hr().__();

							if (fileTree != null) {
								//finalView.addPartial(HostFileList.view, new HostFileListModel(fileTree, false, "", null));
							}

							fieldset
								.b().text("Tape ID: ").__()
								.input()
									.attrType(EnumTypeInputType.TEXT)
									.attrName(TAPE_ID)
									.of(input -> input.attrValue(tapeId))
								.__()
								.button()
									.attrClass(CSS.BUTTON)
									.attrType(EnumTypeButtonType.SUBMIT)
									.text("Submit")
								.__();
						}).__()
					.__()

					.form()
						.b().text("Directory: ").__()
						.input().attrStyle("width:26rem").attrType(EnumTypeInputType.TEXT).attrName(DIR).of(input -> input.attrValue(dir)).__()
						.text("Copy from above DIR")
						.br().__()
						.b().text("Tape ID: ").__()
						.input().attrType(EnumTypeInputType.TEXT).attrName(TAPE_ID).of(input -> input.attrValue(tapeId)).__()
						.button().attrType(EnumTypeButtonType.SUBMIT).text("Submit").__()
					.__()
				.__(); // div
		} catch (Exception e) {
			view = DynamicHtml.view(FilesAddHandler::body);
			throw e;
		}
	}

	@Override
	public void requestHandle(HttpExchange he) throws IOException {
		try {
			HeadModel thm = HeadModel.of("Files Add");
			thm.AddCSS(Asset.CSS_VIRTUAL_FILE_VIEW);
			thm.AddScript(Asset.JS_VIRTUAL_FILE_VIEW);
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
