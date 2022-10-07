package lto.manager.web.handlers.files;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.util.List;

import org.xmlet.htmlapifaster.EnumTypeButtonType;
import org.xmlet.htmlapifaster.EnumTypeInputType;

import com.sun.net.httpserver.HttpExchange;

import htmlflow.DynamicHtml;
import lto.manager.common.Util;
import lto.manager.web.handlers.BaseHandler;
import lto.manager.web.handlers.templates.TemplateHead.TemplateHeadModel;
import lto.manager.web.handlers.templates.TemplatePage;
import lto.manager.web.handlers.templates.TemplatePage.SelectedPage;
import lto.manager.web.handlers.templates.TemplatePage.TemplatePageModel;
import lto.manager.web.handlers.templates.models.BodyModel;

public class FilesAddHandler extends BaseHandler {
	public static final String PATH = "/files/add";

	public static DynamicHtml<BodyModel> view = DynamicHtml.view(FilesAddHandler::body);
	private static final String DIR = "dir";
	private static final String TAPE_ID = "tapeid";

	static void body(DynamicHtml<BodyModel> view, BodyModel model) {
		final String dir = model.getQueryNoNull(DIR);
		final String tapeId = model.getQueryNoNull(TAPE_ID);


		final List<File> workingDir = Util.getFilesListInDir(dir);

		try {
			view
				.div()
					.form()
						.fieldset().dynamic(fieldset -> {
							File currentDir = new File(dir);
							if (dir.equals("")) currentDir = Util.getWorkingDir();

							fieldset.label().text("DIR: " + currentDir.getAbsolutePath()).__().br().__();
							fieldset.label().a().attrHref("?" + DIR + "=" + Util.encodeUrl(currentDir.getParent())).text("&cularr; UP").__().br().__();
							fieldset.hr().__();

							for (File f: workingDir) {
								if (f.isDirectory()) {
									String url = Util.encodeUrl(f.getAbsolutePath());
									fieldset.b().a().attrHref("?" + DIR + "=" + url).text(f.getName()).__().__();
								} else {
									fieldset.span().text(f.getName()).__();
								}
								fieldset.br().__();
							}
							}).__()
					.__()

					.form()
						.b().text("Directory: ").__()
						.input().attrStyle("width:26rem").attrType(EnumTypeInputType.TEXT).attrName(DIR).dynamic(input -> input.attrValue(dir)).__()
						.br().__()
						.b().text("Tape ID: ").__()
						.input().attrType(EnumTypeInputType.TEXT).attrName(TAPE_ID).dynamic(input -> input.attrValue(tapeId)).__()
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
		TemplateHeadModel thm = TemplateHeadModel.of("Files Add");
		TemplatePageModel tepm = TemplatePageModel.of(view, thm, SelectedPage.Files, BodyModel.of(he, null));
		String response = TemplatePage.view.render(tepm);

		he.sendResponseHeaders(HttpURLConnection.HTTP_OK, response.length());
		OutputStream os = he.getResponseBody();
		os.write(response.getBytes());
		os.close();
	}
}
