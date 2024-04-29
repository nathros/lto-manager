package lto.manager.web.handlers.http.templates.models;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import com.sun.net.httpserver.HttpExchange;

import lto.manager.common.Util;
import lto.manager.common.security.LoginSession;
import lto.manager.common.state.State;
import lto.manager.web.handlers.http.BaseHTTPHandler;

public class BodyModel {
	private final HttpExchange he;
	private final Object model;
	private final Map<String, Object> queriesURL;
	private final Map<String, String> cookiesRequest;
	private final List<String> cookiesResponse;
	private final String method;
	private final RequestBody body;

	public class RequestBody {
		private final String contentType;
		private final Map<String, Object> queriesBody;
		private final byte[] payload;
		private String filename;
		private String name;

		public RequestBody(final byte[] body, String contentType) throws IOException {
			//printBody(body);
			this.contentType = contentType;
			queriesBody = new HashMap<String, Object>();

			if (contentType.contains("application/x-www-form-urlencoded")) { // TODO JS FormData
				String bodyStr = new String(body, StandardCharsets.UTF_8);
				parseQuery(bodyStr, queriesBody);
				this.payload = null;
				this.filename = null;
			} else {
				int payloadStartIndex = 0;
				int payloadEndIndex = 0;

				// Find end of first boundary
				int newLineCount = 0;
				for (int i = 0; i < body.length; i++) {
					if (body[i] == 13) {
						if (body[i + 1] == 10) {
							newLineCount++;
							if (newLineCount == 4) {
								payloadStartIndex = i + 2;
								break;
							}
						}
					}
				}

				// Find end of second boundary
				for (int i = body.length - 2; i > 0; i--) {
					if (body[i] == 10) {
						if (body[i - 1] == 13) {
							payloadEndIndex = i - 2;
							break;
						}
					}
				}

				if ((payloadStartIndex != 0) && (payloadEndIndex != 0)) {
					this.payload = Arrays.copyOfRange(body, payloadStartIndex, payloadEndIndex);
					byte[] boundary = Arrays.copyOfRange(body, 0, payloadStartIndex);
					String boundaryStr = new String(boundary, StandardCharsets.UTF_8);
					String[] boundarLines = boundaryStr.split("\r\n");

					int index = boundarLines[1].indexOf(':');
					if (index > 0) {
						String[] fields = boundarLines[1].substring(index + 2).split(";");
						for (String f: fields) {
							String[] keyValue = f.split("=");
							if (keyValue[0].trim().equals("filename")) {
								filename = keyValue[1].substring(1, keyValue[1].length() - 1);
							} else if (keyValue[0].trim().equals("name")) {
								name = keyValue[1].substring(1, keyValue[1].length() - 1);
							}
						}
					}
				} else {
					this.payload = null;
					this.filename = null;
				}
			}
		}

		public String getContentType() {
			return contentType;
		}

		public String getName() {
			return name;
		}

		public byte[] getPayload() {
			return payload;
		}

		public String getFilename() {
			return filename;
		}

		public  Map<String, Object> getQueries() {
			return queriesBody;
		}
	}

	/*private void printBody(final byte[] body) {
		ByteArrayInputStream stream = new ByteArrayInputStream(body);
		InputStreamReader streamReader = new InputStreamReader(stream, StandardCharsets.UTF_8);
		BufferedReader bufferedReader = new BufferedReader(streamReader);
		String line;
		try {
			while ((line = bufferedReader.readLine()) != null) {
			    System.out.println(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}*/

	private BodyModel(HttpExchange he, Object model) throws IOException {
		this.he = he;
		this.model = model;
		queriesURL = new HashMap<String, Object>();
		cookiesRequest = new HashMap<String, String>();
		cookiesResponse = new ArrayList<String>();
		String query = he.getRequestURI().getRawQuery();
		parseQuery(query, queriesURL);
		method = he.getRequestMethod();
		InputStream post = he.getRequestBody();

		Set<Map.Entry<String, List<String>>> entries = he.getRequestHeaders().entrySet();
		String contentType = "";
		for (Map.Entry<String, List<String>> entry : entries) {
			if (entry.getKey().equals("Content-type")) {
				contentType = entry.getValue().get(0);
				break;
			}
			if (entry.getKey().equals("Cookie")) {
				parseCookies(entry.getValue(), cookiesRequest);
			}
		}

		body = new RequestBody(post.readAllBytes(), contentType);
	}

	public static BodyModel of(HttpExchange he, Object model) throws IOException {
		return new BodyModel(he, model);
	}

	public HttpExchange getHttpExchange() { return he; }
	public Object getModel() { return model; }

	public String getQuery(String key) {
		Map<String, Object> queries = isGETMethod() ? queriesURL : body.getQueries();
		return (String) queries.get(key);
	}

	public String getQueryNoNull(String key) {
		String query = getQuery(key);
		if (query == null) return "";
		else return Util.decodeUrl(query);
	}

	@SuppressWarnings("unchecked")
	public List<String> getQueryArray(String key) {
		Map<String, Object> queries = isGETMethod() ? queriesURL : body.getQueries();
		Object o = queries.get(key);
		if (o instanceof String) {
			var list = new ArrayList<String>();
			list.add((String) o);
			return list;
		} else {
			return (List<String>) o;
		}
	}

	public List<String> getQueryArrayNotNull(String key) {
		List<String> queries = getQueryArray(key);
		if (queries == null) {
			return new ArrayList<String>();
		}
		return queries;
	}

	public String getCookie(String key) {
		String result = cookiesRequest.get(key);
		return result;
	}

	public String getCookieNotNull(String key) {
		String result = getCookie(key);
		if (result == null) {
			return "";
		}
		return result;
	}

	public void addResponseCookie(String key, String value, int lifeTimeDays) { // max-age=seconds
		cookiesResponse.add(key + "=" + value + "; path=/; max-age=" + (lifeTimeDays * (60 * 60 * 24)));
	}

	public boolean responseHasCookies() {
		return cookiesResponse.size() > 0;
	}

	public List<String> getResponseCookies() {
		return cookiesResponse;
	}

	public UUID getSession() {
		final String session = getCookie(BaseHTTPHandler.COOKIE_SESSION);
		if (session != null) {
			return UUID.fromString(session);
		}
		return null;
	}

	public String getUserNameViaSession() {
		final UUID uuid = getSession();
		if (uuid != null) {
			final LoginSession loginSession = State.getLoginSession(uuid);
			if (loginSession != null) {
				return loginSession.user().getUsername();
			}
		}
		return null;
	}

	public void setNewSession(UUID uuid) {
		addResponseCookie(BaseHTTPHandler.COOKIE_SESSION, uuid.toString(), 31);
	}

	public void removeSession(UUID uuid) {
		addResponseCookie(BaseHTTPHandler.COOKIE_SESSION, uuid.toString(), -1);
	}

	public void clearAssetCache() {
		// Only works via localhost or HTTPS
		he.getResponseHeaders().putIfAbsent("Clear-Site-Data", Arrays.asList("\"cache\""));
	}

	public boolean hasQuery() {
		String q = he.getRequestURI().getRawQuery();
		return q != null;
	}

	public boolean isGETMethod() {
		return method.equals("GET");
	}

	public boolean isPOSTMethod() {
		return method.equals("POST");
	}

	public String getPOSTContentType() {
		return body.getContentType();
	}

	public String getPOSTType() {
		if (isPOSTMethod()) {
			return body.getName();
		}
		return null;
	}

	public byte[] getRawPOSTData() {
		if (isPOSTMethod()) {
			return body.getPayload();
		}
		return null;
	}

	public String getPOSTFilename() {
		if (isPOSTMethod()) {
			return body.getFilename();
		}
		return null;
	}

	private void parseQuery(String query, Map<String, Object> parameters) {
		if (query != null) {
			String pairs[] = query.split("[&]");

			for (String pair : pairs) {
				String param[] = pair.split("[=]");

				String key = null;
				String value = null;
				if (param.length > 1) {
					key = URLDecoder.decode(param[0], StandardCharsets.UTF_8);
					value = URLDecoder.decode(param[1], StandardCharsets.UTF_8);
				}

				if (!parameters.containsKey(key)) {
					parameters.put(key, value);
				} else if (key != null) {
					Object o = parameters.get(key);
					if (o instanceof String) {
						List<String> list = new ArrayList<String>();
						list.add((String) o);
						list.add(value);
						parameters.put(key, list);
					} else {
						@SuppressWarnings("unchecked")
						List<String> list = (List<String>) o;
						list.add(value);
					}
				}
			}
		}
	}

	private void parseCookies(List<String> cookies, Map<String, String> parameters) {
		for (String header : cookies) {
			String[] cookieKeyValue = header.split(";");
			for (String keyValue : cookieKeyValue) {
				String[] split = keyValue.split("=");
				if (split.length == 2) {
					parameters.put(split[0].trim(), split[1]);
				} else {
					parameters.put(split[0].trim(), "");
				}
			}
		}
	}
}