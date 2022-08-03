package lto.manager.web.handlers.files;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import org.xmlet.htmlapifaster.EnumBorderType;
import org.xmlet.htmlapifaster.EnumTypeButtonType;
import org.xmlet.htmlapifaster.EnumTypeInputType;

import com.sun.net.httpserver.HttpExchange;

import htmlflow.DynamicHtml;
import lto.manager.common.database.Database;
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

		final List<File> allFiles = new ArrayList<File>();
		if (!dir.equals("")) {
			Queue<File> dirs = new LinkedList<File>();
			dirs.add(new File(dir));

			while (!dirs.isEmpty()) {
			  for (File f : dirs.poll().listFiles()) {
			    if (f.isDirectory()) {
			      dirs.add(f);
			    } else if (f.isFile()) {
			      allFiles.add(f);
			    }
			  }
			}

			if (!tapeId.equals("")) {
				try {
					Database.addFilesToTape(Integer.valueOf(tapeId), allFiles);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}



		try {
			view
				.div()
					.form()
						.b().text("Directory: ").__()
						.input().attrStyle("width:26rem").attrType(EnumTypeInputType.TEXT).attrName(DIR).dynamic(input -> input.attrValue(dir)).__()
						.br().__()
						.b().text("Tape ID: ").__()
						.input().attrType(EnumTypeInputType.TEXT).attrName(TAPE_ID).dynamic(input -> input.attrValue(tapeId)).__()
						.button().attrType(EnumTypeButtonType.SUBMIT).text("Submit").__()
					.__()

					.table().dynamic(table -> {
						table.attrBorder(EnumBorderType._1).tr()
							.th().text("Path").__()
							.th().text("Size").__()
						.__();
						for (File f: allFiles) {
							try {
								table.tr()
									.td().text(f.getAbsolutePath().substring(dir.length() + 1)).__()
									.td().text(Files.size(f.toPath())).__()
								.__();
							} catch (IOException e) {
								e.printStackTrace();
							}
						}

					}).__()

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
