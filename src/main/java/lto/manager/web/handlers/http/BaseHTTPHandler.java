package lto.manager.web.handlers.http;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import lto.manager.common.log.Log;
import lto.manager.web.handlers.http.templates.TemplateFetcher;
import lto.manager.web.handlers.http.templates.TemplateFetcher.TemplateFetcherModel;
import lto.manager.web.handlers.http.templates.TemplateInternalError;
import lto.manager.web.handlers.http.templates.TemplateInternalError.TemplateInternalErrorModel;
import lto.manager.web.handlers.http.templates.TemplatePage;
import lto.manager.web.handlers.http.templates.TemplatePage.TemplatePageModel;

public abstract class BaseHTTPHandler implements HttpHandler {
	public static final String LANG_VALUE = "en";

	public static final String ICON_KEY = "rel";
	public static final String ICON_VALUE = "shortcut icon";

	public static final String CHARSET_KEY = "charset";
	public static final String CHARSET_VALUE = "UTF-8";

	public static final String TYPE_KEY = "type";
	public static final String TYPE_SVG = "image/svg+xml";

	public static final String VIEWPORT_KEY = "viewport";
	public static final String VIEWPORT_VALUE = "width=device-width, initial-scale=1";

	public static final String MEDIA_KEY = "media";
	public static final String CSS_MOBILE_MEDIA = "screen and (max-width: 400px)";

	private static int count = 0;

	@Override
	public void handle(HttpExchange he) throws IOException {
		Log.l.fine("Request (" + String.format("%04d", count) + "): " + he.getRequestHeaders().getFirst("Host")
				+ he.getRequestURI());
		count++;
		try {
			this.requestHandle(he);
		} catch (Exception e) {
			e.printStackTrace();
			errorHandle(he, e);
		} catch (Throwable e) {
			e.printStackTrace();
			errorHandle(he, new Exception(e));
		}
	}

	public abstract void requestHandle(HttpExchange he) throws Exception;

	protected void requestHandleCompletePage(HttpExchange he, TemplatePageModel tpm) throws IOException {
		String response = TemplatePage.view.render(tpm);
		he.sendResponseHeaders(HttpURLConnection.HTTP_OK, response.length());
		OutputStream os = he.getResponseBody();
		os.write(response.getBytes());
		os.close();
	}

	protected void requestHandleCompleteFetcher(HttpExchange he, TemplateFetcherModel tfm) throws IOException {
		String response = TemplateFetcher.view.render(tfm);
		he.sendResponseHeaders(HttpURLConnection.HTTP_OK, response.length());
		OutputStream os = he.getResponseBody();
		os.write(response.getBytes());
		os.close();
	}

	protected void errorHandle(HttpExchange he, Exception exception) {
		try {
			String response = TemplateInternalError.view.render(TemplateInternalErrorModel.of(exception, he));
			he.sendResponseHeaders(HttpURLConnection.HTTP_INTERNAL_ERROR, response.length());
			OutputStream os = he.getResponseBody();
			os.write(response.getBytes());
			os.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
