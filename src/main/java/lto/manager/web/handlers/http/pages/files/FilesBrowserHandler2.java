package lto.manager.web.handlers.http.pages.files;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import org.xmlet.htmlapifaster.Div;

import com.sun.net.httpserver.HttpExchange;

import lto.manager.web.handlers.http.BaseHTTPHandler;
import lto.manager.web.handlers.http.partial.filelist.FileList;
import lto.manager.web.handlers.http.partial.filelist.FileListModel;
import lto.manager.web.handlers.http.partial.filelist.FileListOptions;
import lto.manager.web.handlers.http.templates.TemplatePage.SelectedPage;
import lto.manager.web.handlers.http.templates.TemplatePage.TemplatePageModel;
import lto.manager.web.handlers.http.templates.models.BodyModel;
import lto.manager.web.handlers.http.templates.models.HeadModel;
import lto.manager.web.resource.Asset;

public class FilesBrowserHandler2 extends BaseHTTPHandler {
	public static final String PATH = "/files/browser2";

	public static final String ONLY_ID = "only-id";

	static Void content(Div<?> view, BodyModel model) {
		final String idStr = model.getQuery(ONLY_ID) ;
		final int id = idStr == null ? FileListOptions.showAll : Integer.parseInt(idStr);
		view
			.div().of(div ->
				FileList.content(div, new FileListModel("/", FileListOptions.of(true, "", null, 1, true, true, id))))
			.__(); // div
		return null;
	}

	@Override
	public void requestHandle(HttpExchange he) throws IOException, InterruptedException, ExecutionException {
		HeadModel thm = HeadModel.of("Files Browser");
		thm.addCSS(Asset.CSS_FILE_VIEW);
		thm.addScriptDefer(Asset.JS_AJAX).addScript(Asset.JS_FILE_VIEW);
		TemplatePageModel tpm = TemplatePageModel.of(FilesBrowserHandler2::content, null, thm, SelectedPage.Files, BodyModel.of(he, null), null);
		requestHandleCompletePage(he, tpm);
	}
}
