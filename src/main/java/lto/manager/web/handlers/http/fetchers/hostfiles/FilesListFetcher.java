package lto.manager.web.handlers.http.fetchers.hostfiles;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.util.List;

import com.sun.net.httpserver.HttpExchange;

import htmlflow.DynamicHtml;
import lto.manager.web.handlers.http.BaseHTTPHandler;
import lto.manager.web.handlers.http.templates.models.BodyModel;

public class FilesListFetcher extends BaseHTTPHandler {
	public static final String PATH = "/fetcher/fileslist";

	public static DynamicHtml<BodyModel> view = DynamicHtml.view(FilesListFetcher::body);

	static void body(DynamicHtml<BodyModel> view, BodyModel model) {
		final List<String> selected = model.getQueryArray(FileListModel.FILE_SELECTED);
		final String path = model.getQuery(FileListModel.FILE_PATH);
		final String breadcrumbs = model.getQueryNoNull(FileListModel.BREADCRUMBS_LAST);
		final String depthStr = model.getQueryNoNull(FileListModel.MAX_DEPTH);
		final String rootStr = model.getQueryNoNull(FileListModel.SHOW_ROOT);
		final String isVirtualStr = model.getQueryNoNull(FileListModel.IS_VIRTUAL);
		final boolean isVirtual = isVirtualStr.equals(Boolean.TRUE.toString());
		final boolean showRoot = rootStr.equals(Boolean.TRUE.toString());
		int depth = 1;
		try { depth = Integer.parseInt(depthStr); } catch (Exception e) {}
		var options = FileListOptions.of(showRoot, breadcrumbs, selected, depth, isVirtual, isVirtual);
		final FileListModel hflModel = new FileListModel(path, options);
		try {
			view.dynamic(v -> {
				v.addPartial(FileList.view, hflModel);
			});
		} catch (Exception e) {
			view = DynamicHtml.view(FilesListFetcher::body);
			throw e;
		}
	}

	@Override
	public void requestHandle(HttpExchange he) throws IOException {
		try {
			BodyModel bm = BodyModel.of(he, null);
			String response = view.render(bm);
			he.sendResponseHeaders(HttpURLConnection.HTTP_OK, response.length());
			OutputStream os = he.getResponseBody();
			os.write(response.getBytes());
			os.close();
		} catch (Exception e) {
			view = DynamicHtml.view(FilesListFetcher::body);
			throw e;
		}
	}
}
