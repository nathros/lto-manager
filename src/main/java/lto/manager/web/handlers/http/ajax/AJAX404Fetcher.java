package lto.manager.web.handlers.http.ajax;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import org.apache.commons.lang3.StringUtils;
import org.xmlet.htmlapifaster.Div;

import com.sun.net.httpserver.HttpExchange;

import lto.manager.web.handlers.http.BaseHTTPHandler;
import lto.manager.web.handlers.http.templates.TemplateAJAX.TemplateFetcherModel;
import lto.manager.web.handlers.http.templates.models.BodyModel;
import lto.manager.web.resource.Asset;

public class AJAX404Fetcher extends BaseHTTPHandler {
	public static final String PATH = StringUtils.substring(Asset.PATH_AJAX_BASE, 0, -1);

	static Void content(Div<?> view, BodyModel model) {
		view.raw("404 - AJAX request not found");
		return null;
	}

	@Override
	public void requestHandle(HttpExchange he) throws IOException, InterruptedException, ExecutionException {
		BodyModel bm = BodyModel.of(he, null);
		requestHandleCompleteFetcher404(he, new TemplateFetcherModel(AJAX404Fetcher::content, bm));
	}
}
