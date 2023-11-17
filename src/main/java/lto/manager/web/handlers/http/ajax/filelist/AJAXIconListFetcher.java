package lto.manager.web.handlers.http.ajax.filelist;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import org.xmlet.htmlapifaster.Div;

import com.sun.net.httpserver.HttpExchange;

import lto.manager.web.handlers.http.BaseHTTPHandler;
import lto.manager.web.handlers.http.partial.filelist.FileListItem;
import lto.manager.web.handlers.http.templates.TemplateAJAX.TemplateFetcherModel;
import lto.manager.web.handlers.http.templates.models.BodyModel;
import lto.manager.web.resource.Asset;
import lto.manager.web.resource.CSS;
import lto.manager.web.resource.JS;

public class AJAXIconListFetcher extends BaseHTTPHandler {
	public static final String PATH = "/ajax/iconlist";

	static Void content(Div<?> view, BodyModel model) {
		var folderIcons = FileListItem.fileTypeCache.stream().filter(item -> item.contains("folder"))
				.collect(Collectors.toList());
		folderIcons.sort((first, second) -> first.compareTo(second));

		view
			.attrClass(CSS.FV_CONTEXT_NO_HOVER)
			.attrStyle("padding:0;text-align:justify;")
			.of(o -> {
			for (String image: folderIcons) {
				o.img()
					.attrClass(CSS.FV_CONTEXT_WITH_HOVER)
					.attrSrc(Asset.IMG_TYPES + image)
					.attrOnclick(JS.fnSetDirIcon())
				.__();
			}
		});
		return null;
	}

	@Override
	public void requestHandle(HttpExchange he) throws IOException, InterruptedException, ExecutionException {
		BodyModel bm = BodyModel.of(he, null);
		requestHandleCompleteFetcher(he, new TemplateFetcherModel(AJAXIconListFetcher::content, bm));
	}
}
