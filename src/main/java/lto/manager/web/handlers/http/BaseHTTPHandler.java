package lto.manager.web.handlers.http;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import lto.manager.common.database.Options;
import lto.manager.common.database.tables.records.RecordOptions.OptionsSetting;
import lto.manager.common.log.Log;
import lto.manager.common.state.State;
import lto.manager.web.handlers.http.pages.AssetHandler;
import lto.manager.web.handlers.http.pages.LogInHandler;
import lto.manager.web.handlers.http.templates.TemplateAJAX;
import lto.manager.web.handlers.http.templates.TemplateAJAX.TemplateFetcherModel;
import lto.manager.web.handlers.http.templates.TemplateInternalErrorAJAX;
import lto.manager.web.handlers.http.templates.TemplateInternalErrorAJAX.TemplateInternalErrorModelAJAX;
import lto.manager.web.handlers.http.templates.TemplateInternalErrorPage;
import lto.manager.web.handlers.http.templates.TemplateInternalErrorPage.TemplateInternalErrorModelPage;
import lto.manager.web.handlers.http.templates.TemplatePage;
import lto.manager.web.handlers.http.templates.TemplatePage.TemplatePageModel;
import lto.manager.web.resource.Asset;

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

	public static final String COOKIE_SESSION = "session";

	public static final String CONTENT_TYPE_PDF = "application/pdf";
	public static final String CONTENT_TYPE_JSON = "application/json";
	public static final String CONTENT_TYPE_SVG = "image/svg+xml";

	private static int count = 0;

	@Override
	public void handle(HttpExchange he) throws IOException {
		if (Options.getData(OptionsSetting.LOG_REQUESTS) == Boolean.TRUE) {
			String message = "Request (" + String.format("%04d", count) + "): "
					+ he.getRequestHeaders().getFirst("Host") + he.getRequestURI();
			if (Options.getData(OptionsSetting.LOG_REQUESTS_ASSETS) == Boolean.TRUE) {
				Log.info(message);
			} else if (!he.getRequestURI().toString().contains(AssetHandler.PATH)) {
				Log.info(message);
			}
		}
		BaseHTTPHandler handler = this; // If authentication failed then replace this with login handler

		if ((boolean) Options.getData(OptionsSetting.ENABLE_LOGIN)) {
			final String session = getSessionCookie(he);
			if (!State.isLoginSessionValid(session)) {
				handler = new LogInHandler();
				Log.info("User not logged in show login page: " + he.getRequestHeaders().getFirst("Host")
						+ he.getRequestURI());
			}
		}

		count++;
		try {
			handler.requestHandle(he);
		} catch (Exception e) {
			if (Options.getData(OptionsSetting.LOG_EXCEPTION_STACKTRACE) == Boolean.TRUE) {
				e.printStackTrace();
			}
			if (he.getRequestURI().getPath().indexOf(Asset.PATH_AJAX_BASE) == 0) {
				errorHandleAJAX(he, e);
			} else {
				errorHandlePage(he, e);
			}
		} catch (Throwable e) {
			if (Options.getData(OptionsSetting.LOG_EXCEPTION_STACKTRACE) == Boolean.TRUE) {
				e.printStackTrace();
			}
			if (he.getRequestURI().getPath().indexOf(Asset.PATH_AJAX_BASE) == 0) {
				errorHandleAJAX(he, new Exception(e));
			} else {
				errorHandlePage(he, new Exception(e));
			}
		}
	}

	public abstract void requestHandle(HttpExchange he) throws Exception;

	protected void addResponseCookies(HttpExchange he, List<String> cookies) {
		he.getResponseHeaders().putIfAbsent("Set-Cookie", cookies);
	}

	protected void addResponseCookies(HttpExchange he, TemplatePageModel tpm) {
		if (tpm.getBodyModel().responseHasCookies()) {
			he.getResponseHeaders().putIfAbsent("Set-Cookie", tpm.getBodyModel().getResponseCookies());
		}
	}

	protected void requestHandleCompletePage(HttpExchange he, TemplatePageModel tpm)
			throws IOException, InterruptedException, ExecutionException {
		CompletableFuture<String> future = TemplatePage.view.renderAsync(tpm);
		String response = future.get();
		addResponseCookies(he, tpm);
		he.sendResponseHeaders(HttpURLConnection.HTTP_OK, response.length());
		OutputStream os = he.getResponseBody();
		os.write(response.getBytes());
		os.close();
	}

	protected void requestHandleCompleteFuture(HttpExchange he, CompletableFuture<String> future, TemplatePageModel tpm)
			throws IOException, InterruptedException, ExecutionException {
		String response = future.get();
		addResponseCookies(he, tpm);
		he.sendResponseHeaders(HttpURLConnection.HTTP_OK, response.length());
		OutputStream os = he.getResponseBody();
		os.write(response.getBytes());
		os.close();
	}

	protected void requestHandleCompleteFetcher(HttpExchange he, TemplateFetcherModel tfm)
			throws IOException, InterruptedException, ExecutionException {
		CompletableFuture<String> future = TemplateAJAX.view.renderAsync(tfm);
		String response = future.get();
		he.sendResponseHeaders(HttpURLConnection.HTTP_OK, response.length());
		OutputStream os = he.getResponseBody();
		os.write(response.getBytes());
		os.close();
	}

	protected void requestHandleCompleteAPIText(HttpExchange he, final String text, final String contentType)
			throws IOException, InterruptedException, ExecutionException {
		he.getResponseHeaders().set("Content-Type", contentType);
		he.sendResponseHeaders(HttpURLConnection.HTTP_OK, text.length());
		OutputStream os = he.getResponseBody();
		os.write(text.getBytes());
		os.close();
	}

	protected void requestHandleCompleteAPIBinary(HttpExchange he, ByteArrayOutputStream bin, final String contentType)
			throws IOException, InterruptedException, ExecutionException {
		he.getResponseHeaders().set("Content-Type", contentType);
		final var data = bin.toByteArray();
		he.sendResponseHeaders(HttpURLConnection.HTTP_OK, data.length);
		OutputStream os = he.getResponseBody();
		os.write(data);
		os.close();
	}

	protected void errorHandlePage(HttpExchange he, Exception exception) {
		try {
			String response = TemplateInternalErrorPage.view.render(TemplateInternalErrorModelPage.of(exception, he));
			he.sendResponseHeaders(HttpURLConnection.HTTP_INTERNAL_ERROR, response.length());
			OutputStream os = he.getResponseBody();
			os.write(response.getBytes());
			os.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected void errorHandleAJAX(HttpExchange he, Exception exception) {
		try {
			String response = TemplateInternalErrorAJAX.view.render(TemplateInternalErrorModelAJAX.of(exception, he));
			he.sendResponseHeaders(HttpURLConnection.HTTP_INTERNAL_ERROR, response.length());
			OutputStream os = he.getResponseBody();
			os.write(response.getBytes());
			os.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private String getSessionCookie(HttpExchange he) { // FIXME Similar to BodyModel.parseCookies - done twice
		for (Map.Entry<String, List<String>> entry : he.getRequestHeaders().entrySet()) {
			if (entry.getKey().equals("Cookie")) {
				for (final String cookieStr : entry.getValue()) {
					final var cookies = getCookieKeyPairs(cookieStr);
					final String session = cookies.get(COOKIE_SESSION);
					if (session != null) {
						return session;
					}
				}
			}
		}
		return "";
	}

	public static HashMap<String, String> getCookieKeyPairs(final String cookieStr) { // FIXME Similar to BodyModel.parseCookies - done twice
		HashMap<String, String> map = new HashMap<String, String>();
		String[] pairs = cookieStr.split(";");
		for (String keyPair : pairs) {
			String[] split = keyPair.split("=");
			if (split.length == 2) {
				map.put(split[0].trim(), split[1]);
			} else {
				Log.warning("Cookie string: " + cookieStr + " is malformed");
			}
		}
		return map;
	}
}
