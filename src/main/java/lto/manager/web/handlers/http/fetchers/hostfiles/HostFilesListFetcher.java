package lto.manager.web.handlers.http.fetchers.hostfiles;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.util.List;

import com.sun.net.httpserver.HttpExchange;

import htmlflow.DynamicHtml;
import lto.manager.web.handlers.http.BaseHTTPHandler;
import lto.manager.web.handlers.http.templates.models.BodyModel;

public class HostFilesListFetcher extends BaseHTTPHandler {
	public static final String PATH = "/fetcher/hfileslist";

	public static DynamicHtml<BodyModel> view = DynamicHtml.view(HostFilesListFetcher::body);

	static void body(DynamicHtml<BodyModel> view, BodyModel model) {
		final List<String> selected = model.getQueryArray(HostFileListModel.FILE_SELECTED);
		final String path = model.getQuery(HostFileListModel.FILE_PATH);
		final String breadcrumbs = model.getQueryNoNull(HostFileListModel.BREADCRUMBS_LAST);
		final String depthStr = model.getQueryNoNull(HostFileListModel.MAX_DEPTH);
		final String rootStr = model.getQueryNoNull(HostFileListModel.SHOW_ROOT);
		final boolean showRoot = rootStr.equals("1");
		int depth = 1;
		try { depth = Integer.parseInt(depthStr); } catch (Exception e) {}
		final HostFileListModel hflModel = new HostFileListModel(path, showRoot, breadcrumbs, selected, depth);
		try {
			view.dynamic(v -> {
				v.addPartial(HostFileList.view, hflModel);
			});
		} catch (Exception e) {
			view = DynamicHtml.view(HostFilesListFetcher::body);
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
			view = DynamicHtml.view(HostFilesListFetcher::body);
			throw e;
		}
	}
}
