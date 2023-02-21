package lto.manager.web.handlers.http.fetchers.virtualfiles;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;

import com.sun.net.httpserver.HttpExchange;

import htmlflow.DynamicHtml;
import lto.manager.web.handlers.http.BaseHTTPHandler;
import lto.manager.web.handlers.http.templates.models.BodyModel;

public class VirtualFilesBrowserFetcher extends BaseHTTPHandler {
	public static final String PATH = "/fetcher/vfilesbrowser";

	public static DynamicHtml<BodyModel> view = DynamicHtml.view(VirtualFilesBrowserFetcher::body);
	public static int a = 0;

	static void body(DynamicHtml<BodyModel> view, BodyModel model) {
		a++;
		try {
			view
				.div().text(a)
				.__(); // div
		} catch (Exception e) {
			view = DynamicHtml.view(VirtualFilesBrowserFetcher::body);
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
			view = DynamicHtml.view(VirtualFilesBrowserFetcher::body);
			throw e;
		}
	}
}
