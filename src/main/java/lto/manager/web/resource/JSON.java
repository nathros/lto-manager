package lto.manager.web.resource;

import java.util.HashMap;

public class JSON {
	public static final String STATUS = "status";
	public static final String MESSAGE = "message";
	public static final String STATUS_OK = "ok";
	public static final String STATUS_ERROR = "error";

	public enum APIStatus {
		ok, error
	}

	public static String populateAPIResponse(APIStatus status, String message) {
		final String json = "{\"" + STATUS + "\":\"" + status + "\",\"" + MESSAGE + "\":\"" + message + "\"}";
		return json;
	}

	public static String populateAPIResponse(APIStatus status, HashMap<String, Object> content) throws Exception {
		String body = "";
		int count = 0;
		for (String key : content.keySet()) {
			if (count > 0) {
				body = body.concat(",");
			}
			var value = content.get(key);
			if (value instanceof Integer) {
				body = body.concat("\"" + key + "\":" + String.valueOf(value) + "");
			} else if (value instanceof Long) {
				body = body.concat("\"" + key + "\":" + String.valueOf(value) + "");
			} else if (value instanceof String) {
				body = body.concat("\"" + key + "\":\"" + content.get(key) + "\"");
			} else {
				throw new Exception("populateAPIResponse invalid Object type: " + value.getClass().getSimpleName());
			}
			count++;
		}
		final String json = "{\"" + STATUS + "\":\"" + status + "\",\"" + MESSAGE + "\":{" + body + "}}";
		return json;
	}
}
