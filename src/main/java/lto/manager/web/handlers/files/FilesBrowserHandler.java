package lto.manager.web.handlers.files;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import org.xmlet.htmlapifaster.EnumBorderType;

import com.sun.net.httpserver.HttpExchange;

import htmlflow.DynamicHtml;
import lto.manager.common.database.Database;
import lto.manager.common.database.tables.TableFile;
import lto.manager.common.database.tables.TableFile.RecordFile;
import lto.manager.web.handlers.BaseHandler;
import lto.manager.web.handlers.templates.TemplatePage;
import lto.manager.web.handlers.templates.TemplatePage.SelectedPage;
import lto.manager.web.handlers.templates.TemplatePage.TemplatePageModel;
import lto.manager.web.handlers.templates.models.BodyModel;
import lto.manager.web.handlers.templates.models.HeadModel;
import lto.manager.web.resource.Asset;
import lto.manager.web.resource.CSS;

public class FilesBrowserHandler extends BaseHandler {
	public static final String PATH = "/files/browser";

	public static DynamicHtml<BodyModel> view = DynamicHtml.view(FilesBrowserHandler::body);

	public static final String DIR = "dir";

	static void body(DynamicHtml<BodyModel> view, BodyModel model) {
		final String dir = model.getQueryNoNull(DIR);

		final List<RecordFile> filesList = new ArrayList<RecordFile>();
		try {
			final String location = dir.equals("") ? "/" : dir;
			filesList.addAll(TableFile.getFilesInDir(Database.connection, location));
		} catch (SQLException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		Supplier<String> parentDir = () -> {
			if (dir != null) {
				int index = dir.lastIndexOf("/");
				if (index > 0) {
					return dir.substring(0, index);
				}
		    }
		    return "/";
		};
		final String parent = parentDir.get();

		try {
			view
				.div()
					.a().attrClass(CSS.BUTTON).attrHref("?" + DIR + "=" + parent).text("Parent").__()
					.p().text("Path " + dir).__()
					.table().dynamic(table -> {
						table.attrBorder(EnumBorderType._1).tr()
							.th().text("Filename").__()
							.th().text("Size").__()
						.__();
						for (RecordFile f: filesList) {
							table.tr()
								.td().of(td -> {
									if (f.isDirectory()) {
										td.b().a().attrHref("?" + DIR + "=" + f.getFilePath()).text(f.getFileName()).__().__();
									} else {
										td.text(f.getFileName());
									}
								}).__()
								.td().text(f.getFileSize())
							.__();
						}
						if (filesList.size() == 0) table.tr().td().text("Empty").__().__();
					}).__()
				.__(); // div
		} catch (Exception e) {
			view = DynamicHtml.view(FilesBrowserHandler::body);
			throw e;
		}

	}

	@Override
	public void requestHandle(HttpExchange he) throws IOException {
		try {
			HeadModel thm = HeadModel.of("Files Browser");
			thm.AddCSS(Asset.CSS_FILE_VIEW);
			thm.AddScript(Asset.JS_FILE_VIEW);
			TemplatePageModel tepm = TemplatePageModel.of(view, thm, SelectedPage.Files, BodyModel.of(he, null));
			String response = TemplatePage.view.render(tepm);

			he.sendResponseHeaders(HttpURLConnection.HTTP_OK, response.length());
			OutputStream os = he.getResponseBody();
			os.write(response.getBytes());
			os.close();
		} catch (Exception e) {
			view = DynamicHtml.view(FilesBrowserHandler::body);
			throw e;
		}
	}
}
