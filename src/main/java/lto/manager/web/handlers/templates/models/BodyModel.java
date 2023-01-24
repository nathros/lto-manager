package lto.manager.web.handlers.templates.models;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sun.net.httpserver.HttpExchange;

import lto.manager.common.Util;
import lto.manager.web.handlers.BaseHandler;

public class BodyModel {
	private final HttpExchange he;
	private final Object model;
	private final Map<String, Object> queries;
	private final String method;
	private final POSTBody body;

	public static final String QUERY_ON = "on";
	public static final String QUERY_OFF = "off";

	public class POSTBody {
		private final String contentType;
		private final byte[] payload;
		private String filename;
		private String disposition;
		private String name;

		public POSTBody(final byte[] body) throws IOException {
			contentType = "";
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
						} else if (keyValue.length == 1) {
							disposition = keyValue[0];
						}
					}
				}
			} else {
				this.payload = null;
				this.filename = null;
				this.disposition = null;
			}

			// Print entire body
			/*ByteArrayInputStream stream = new ByteArrayInputStream(body);
			InputStreamReader streamReader = new InputStreamReader(stream, StandardCharsets.UTF_8);
			BufferedReader bufferedReader = new BufferedReader(streamReader);
			String line;
			while ((line = bufferedReader.readLine()) != null) {
			    System.out.println(line);
			}*/
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
	}

	private BodyModel(HttpExchange he, Object model) throws IOException {
		this.he = he;
		this.model = model;
		queries = new HashMap<String, Object>();
		String query = he.getRequestURI().getRawQuery();
		BaseHandler.parseQuery(query, queries);
		method = he.getRequestMethod();
		InputStream post = he.getRequestBody();
		body = new POSTBody(post.readAllBytes());
	}

	public static BodyModel of(HttpExchange he, Object model) throws IOException {
		return new BodyModel(he, model);
	}

	public HttpExchange getHttpExchange() { return he; }
	public Object getModel() { return model; }
	public String getQuery(String key) { return (String) queries.get(key); }

	public String getQueryNoNull(String key) {
		String q = (String) queries.get(key);
		if (q == null) return "";
		else return Util.decodeUrl(q);
	}

	@SuppressWarnings("unchecked")
	public List<String> getQueryArray(String key) {
		Object o = queries.get(key);
		if (o instanceof String) {
			var list = new ArrayList<String>();
			list.add((String) o);
			return list;
		} else {
			return (List<String>) o;
		}
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
}