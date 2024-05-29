package lto.manager.web.handlers.http.ajax;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import org.xmlet.htmlapifaster.Div;

import com.sun.net.httpserver.HttpExchange;

import lto.manager.web.handlers.http.BaseHTTPHandler;
import lto.manager.web.handlers.http.partial.inlinemessage.InlineMessage;
import lto.manager.web.handlers.http.templates.TemplateAJAX.TemplateFetcherModel;
import lto.manager.web.handlers.http.templates.models.BodyModel;
import lto.manager.web.resource.Asset;

public class AJAXGetOnloadError extends BaseHTTPHandler {
	public static final String PATH = Asset.PATH_AJAX_BASE + "onloaderror/";

	static Void content(Div<?> view, BodyModel model) {
		final String title = model.getQuery("title");
		final String message = model.getQuery("message");
		view.of(div -> InlineMessage.contentGenericError(div, title, message));
		return null;
	}

	@Override
	public void requestHandle(HttpExchange he) throws IOException, InterruptedException, ExecutionException {
		BodyModel bm = BodyModel.of(he, null);
		requestHandleCompleteFetcher(he, new TemplateFetcherModel(AJAXGetOnloadError::content, bm));
	}
}
