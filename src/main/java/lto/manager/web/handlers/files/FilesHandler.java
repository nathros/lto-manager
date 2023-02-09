package lto.manager.web.handlers.files;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

import org.xmlet.htmlapifaster.EnumBorderType;
import org.xmlet.htmlapifaster.EnumTypeButtonType;
import org.xmlet.htmlapifaster.EnumTypeInputType;

import com.sun.net.httpserver.HttpExchange;

import htmlflow.DynamicHtml;
import lto.manager.common.database.Database;
import lto.manager.web.handlers.BaseHandler;
import lto.manager.web.handlers.templates.TemplatePage;
import lto.manager.web.handlers.templates.TemplatePage.SelectedPage;
import lto.manager.web.handlers.templates.TemplatePage.TemplatePageModel;
import lto.manager.web.handlers.templates.models.BodyModel;
import lto.manager.web.handlers.templates.models.HeadModel;
import lto.manager.web.resource.CSS;

public class FilesHandler extends BaseHandler {
	public static final String PATH = "/files";

	public static DynamicHtml<BodyModel> view = DynamicHtml.view(FilesHandler::body);

	public static final String TAPE_ID = "tape_id";

	static void body(DynamicHtml<BodyModel> view, BodyModel model) {
		final String tapeId = model.getQueryNoNull(TAPE_ID);

		final List<File> files = new ArrayList<File>();
		if (!tapeId.equals("")) {
			try {
				files.addAll(Database.getFilesOnTape(Integer.valueOf(tapeId)));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		try {
			view
				.div()
					.a().attrClass(CSS.BUTTON).attrHref(FilesAddHandler.PATH).text("Add files").__()
					.a().attrClass(CSS.BUTTON).attrHref(FilesBrowserHandler.PATH).text("Browse").__()
					.form().of(form -> {
							form.label().text("Show files for tape ID:").__();
							form.input().attrType(EnumTypeInputType.TEXT).attrName(TAPE_ID).dynamic(input -> input.attrValue(tapeId)).__();
						})
						.button().attrClass(CSS.BUTTON).attrType(EnumTypeButtonType.SUBMIT).text("Submit").__()
					.__()


					.table().dynamic(table -> {
						table.attrBorder(EnumBorderType._1).tr()
							.th().text("Path").__()
						.__();
						for (File f: files) {
							table.tr()
								.td().text(f.getAbsolutePath()).__()
							.__();
						}
						if (files.size() == 0) table.tr().td().text("Empty").__().__();
					}).__()
				.__(); // div
		} catch (Exception e) {
			view = DynamicHtml.view(FilesHandler::body);
			throw e;
		}

	}

	@Override
	public void requestHandle(HttpExchange he) throws IOException {
		try {
			HeadModel thm = HeadModel.of("Files");
			TemplatePageModel tepm = TemplatePageModel.of(view, thm, SelectedPage.Files, BodyModel.of(he, null));
			String response = TemplatePage.view.render(tepm);

			he.sendResponseHeaders(HttpURLConnection.HTTP_OK, response.length());
			OutputStream os = he.getResponseBody();
			os.write(response.getBytes());
			os.close();
		} catch (Exception e) {
			view = DynamicHtml.view(FilesHandler::body);
			throw e;
		}
	}
}
