package lto.manager.web.handlers.http.ajax;

import java.io.IOException;

import org.xmlet.htmlapifaster.Div;

import com.sun.net.httpserver.HttpExchange;

import lto.manager.web.handlers.http.BaseHTTPHandler;
import lto.manager.web.handlers.http.partial.inlinemessage.InlineErrorMessage;
import lto.manager.web.handlers.http.templates.TemplateFetcher.TemplateFetcherModel;
import lto.manager.web.handlers.http.templates.models.BodyModel;
import lto.manager.web.resource.Query;

public class AJAXGetOnloadError extends BaseHTTPHandler {
	public static final String PATH = "/ajax/onloaderror";

	static Void content(Div<?> view, BodyModel model) {
		final String uuid = model.getQuery(Query.UUID);
		view.of(div -> InlineErrorMessage.content(div, uuid));
		return null;
	}

	@Override
	public void requestHandle(HttpExchange he) throws IOException {
		BodyModel bm = BodyModel.of(he, null);
		requestHandleCompleteFetcher(he, new TemplateFetcherModel(AJAXGetOnloadError::content, bm));
	}
}
