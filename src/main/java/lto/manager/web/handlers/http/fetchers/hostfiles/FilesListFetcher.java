package lto.manager.web.handlers.http.fetchers.hostfiles;

import java.io.IOException;
import java.util.List;

import org.xmlet.htmlapifaster.Div;

import com.sun.net.httpserver.HttpExchange;

import lto.manager.web.handlers.http.BaseHTTPHandler;
import lto.manager.web.handlers.http.templates.TemplateFetcher.TemplateFetcherModel;
import lto.manager.web.handlers.http.templates.models.BodyModel;

public class FilesListFetcher extends BaseHTTPHandler {
	public static final String PATH = "/fetcher/fileslist";

	static Void content(Div<?> view, BodyModel model) {
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
		view.of(v -> FileList.content(v, hflModel));
		return null;
	}

	@Override
	public void requestHandle(HttpExchange he) throws IOException {
		BodyModel bm = BodyModel.of(he, null);
		requestHandleCompleteFetcher(he, new TemplateFetcherModel(FilesListFetcher::content, bm));

	}
}
