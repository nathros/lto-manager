package lto.manager.web.handlers.http.ajax.filelist;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.xmlet.htmlapifaster.Div;

import com.sun.net.httpserver.HttpExchange;

import lto.manager.common.database.tables.records.RecordRole.Permission;
import lto.manager.web.handlers.http.BaseHTTPHandler;
import lto.manager.web.handlers.http.partial.filelist.FileList;
import lto.manager.web.handlers.http.partial.filelist.FileListModel;
import lto.manager.web.handlers.http.partial.filelist.FileListOptions;
import lto.manager.web.handlers.http.templates.TemplateAJAX.TemplateFetcherModel;
import lto.manager.web.handlers.http.templates.models.BodyModel;

public class AJAXFilesListFetcher extends BaseHTTPHandler {
	public static final String PATH = "/ajax/fileslist/";

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
		var options = FileListOptions.of(showRoot, breadcrumbs, selected, depth, isVirtual, isVirtual, FileListOptions.showAll);
		final FileListModel hflModel = new FileListModel(path, options);
		view.of(v -> FileList.content(v, hflModel));
		return null;
	}

	@Override
	public void requestHandle(HttpExchange he, BodyModel bm) throws IOException, InterruptedException, ExecutionException {
		requestHandleCompleteFetcher(he, new TemplateFetcherModel(AJAXFilesListFetcher::content, bm));
	}

	@Override
	public Permission getHandlePermission() {
		return null;
	}
}
