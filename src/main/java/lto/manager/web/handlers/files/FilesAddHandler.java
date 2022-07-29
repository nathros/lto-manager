package lto.manager.web.handlers.files;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import org.xmlet.htmlapifaster.EnumTypeButtonType;
import org.xmlet.htmlapifaster.EnumTypeInputType;

import com.sun.net.httpserver.HttpExchange;

import htmlflow.DynamicHtml;
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

	static void body(DynamicHtml<BodyModel> view, BodyModel model) {
		final String dir = model.getQueryNoNull(DIR);

		final List<File> allFiles = new ArrayList<File>();;
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
		}


		view
			.div()
				.form()
					.b().text("Directory: ").__()
					.input().attrStyle("width:26rem").attrType(EnumTypeInputType.TEXT).attrName(DIR).dynamic(input -> input.attrValue(dir)).__()
					.button().attrType(EnumTypeButtonType.SUBMIT).text("Submit").__()
				.__()

				.dynamic(div -> {
					for (File f: allFiles) {
						div.p().text(f.getAbsolutePath().substring(dir.length() + 1)).__();
					}
				})

			.__(); // div
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
