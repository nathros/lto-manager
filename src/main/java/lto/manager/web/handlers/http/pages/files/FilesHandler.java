package lto.manager.web.handlers.http.pages.files;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.xmlet.htmlapifaster.Div;
import org.xmlet.htmlapifaster.EnumBorderType;
import org.xmlet.htmlapifaster.EnumTypeButtonType;
import org.xmlet.htmlapifaster.EnumTypeInputType;

import com.sun.net.httpserver.HttpExchange;

import lto.manager.common.database.Database;
import lto.manager.web.handlers.http.BaseHTTPHandler;
import lto.manager.web.handlers.http.templates.TemplatePage.SelectedPage;
import lto.manager.web.handlers.http.templates.TemplatePage.TemplatePageModel;
import lto.manager.web.handlers.http.templates.models.BodyModel;
import lto.manager.web.handlers.http.templates.models.HeadModel;
import lto.manager.web.resource.CSS;

public class FilesHandler extends BaseHTTPHandler {
	public static final String PATH = "/files";

	public static final String TAPE_ID = "tape_id";

	static Void content(Div<?> view, BodyModel model) {
		final String tapeId = model.getQueryNoNull(TAPE_ID);

		final List<File> files = new ArrayList<File>();
		if (!tapeId.equals("")) {
			try {
				files.addAll(Database.getFilesOnTape(Integer.valueOf(tapeId)));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		view
			.div()
				.a().attrClass(CSS.BUTTON).attrHref(FilesAddHandler.PATH).text("Add files").__()
				.a().attrClass(CSS.BUTTON).attrHref(FilesBrowserHandler.PATH).text("Browse").__()
				.form().of(form -> {
						form.label().text("Show files for tape ID:").__();
						form.input().attrType(EnumTypeInputType.TEXT).attrName(TAPE_ID).of(input -> input.attrValue(tapeId)).__();
					})
					.button().attrClass(CSS.BUTTON).attrType(EnumTypeButtonType.SUBMIT).text("Submit").__()
				.__()


				.table().attrClass(CSS.TABLE).of(table -> {
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
		return null;
	}

	@Override
	public void requestHandle(HttpExchange he) throws IOException, InterruptedException, ExecutionException {
		HeadModel thm = HeadModel.of("Files");
		TemplatePageModel tpm = TemplatePageModel.of(FilesHandler::content, thm, SelectedPage.Files, BodyModel.of(he, null));
		requestHandleCompletePage(he, tpm);
	}
}
