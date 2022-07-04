package lto.manager.web.handlers;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.OverridingMethodsMustInvokeSuper;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public abstract class BaseHandler implements HttpHandler {
	public static final String LANG_VALUE = "en";

	public static final String ICON_KEY = "rel";
	public static final String ICON_VALUE = "shortcut icon";
	public static final String ICON_SIZE_KEY = "size";
	public static final String ICON_SIZE_VALUE = "any";

	public static final String CHARSET_KEY = "charset";
	public static final String CHARSET_VALUE = "UTF-8";

	public static final String TYPE_KEY = "type";
	public static final String TYPE_SVG = "image/svg+xml";

	public static final String VIEWPORT_KEY = "viewport";
	public static final String VIEWPORT_VALUE = "width=device-width, initial-scale=1";

	public static final String MEDIA_KEY = "media";
	public static final String CSS_MOBILE_MEDIA = "screen and (max-width: 400px)";

	private static int count = 0;

	protected void parseQuery(String query, Map<String, Object> parameters) throws UnsupportedEncodingException {
		if (query != null) {
			String pairs[] = query.split("[&]");

			for (String pair : pairs) {
				String param[] = pair.split("[=]");

				String key = null;
				String value = null;
				if (param.length > 0) {
					key = URLDecoder.decode(param[0], System.getProperty("file.encoding"));
				}

				if (param.length > 1) {
					value = URLDecoder.decode(param[1], System.getProperty("file.encoding"));
				}

				if (parameters.containsKey(key)) {
					Object obj = parameters.get(key);
					if (obj instanceof List<?>) {
						@SuppressWarnings("unchecked")
						List<String> values = (List<String>) obj;
						values.add(value);
					} else if (obj instanceof String) {
						List<String> values = new ArrayList<String>();
						values.add((String) obj);
						values.add(value);
						parameters.put(key, values);
					}
				} else {
					parameters.put(key, value);
				}
			}
		}
	}

	@OverridingMethodsMustInvokeSuper
	@Override
	public void handle(HttpExchange he) throws IOException {
		System.out.println("Request (" + String.format("%04d", count) + "): " + he.getRequestHeaders().getFirst("Host") + he.getRequestURI());
		count++;
	}
}
