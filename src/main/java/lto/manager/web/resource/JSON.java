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

	public static String populateAPIResponse(APIStatus status, JSONMap content) throws Exception {
		final String json = "{\"" + STATUS + "\":\"" + status + "\",\"" + MESSAGE + "\":{" + content.serializeToString()
				+ "}}";
		return json;
	}

	public static class JSONMap {
		HashMap<String, Object> map = new HashMap<String, Object>();

		public String serializeToString() throws Exception {
			return mapToJSONStr(map);
		}

		private String mapToJSONStr(HashMap<String, Object> content) throws Exception {
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
					body = body.concat("\"" + key + "\":\"" + value + "\"");
				} else if (value instanceof HashMap) {
					@SuppressWarnings("unchecked")
					final var map = (HashMap<String, Object>) value;
					body = body.concat("\"" + key + "\":{" + mapToJSONStr(map) + "}");
				} else {
					throw new Exception("populateAPIResponse invalid Object type: " + value.getClass().getSimpleName());
				}
				count++;
			}
			return body;
		}

		@SuppressWarnings("unchecked")
		public <T> T get(String... jsonPaths) {
			int index = 0;
			if (index == jsonPaths.length - 1) {
				return (T) map.get(jsonPaths[index]);
			} else {
				var entry = (HashMap<String, Object>) map.get(jsonPaths[index]);
				return get(index + 1, entry, jsonPaths);
			}
		}

		@SuppressWarnings("unchecked")
		private <T> T get(int index, HashMap<String, Object> inner, String... jsonPaths) {
			if (index == jsonPaths.length - 1) {
				return (T) inner.get(jsonPaths[index]);
			} else {
				return get(index + 1, (HashMap<String, Object>) inner.get(jsonPaths[index]), jsonPaths);
			}
		}

		@SuppressWarnings("unchecked")
		public <T> void set(T newValue, String... jsonPaths) {
			if (jsonPaths.length == 1) {
				map.put(jsonPaths[0], newValue);
			} else {
				var entry = map.get(jsonPaths[0]);
				if (entry == null) {
					entry = new HashMap<String, Object>();
					map.put(jsonPaths[0], entry);
				}
				set(newValue, (HashMap<String, Object>) entry, 1, jsonPaths);
			}
		}

		@SuppressWarnings("unchecked")
		public <T> void set(T newValue, HashMap<String, Object> inner, int index, String... jsonPaths) {
			if (index == jsonPaths.length - 1) {
				inner.put(jsonPaths[index], newValue);
			} else {
				var entry = inner.get(jsonPaths[index]);
				if (entry == null) {
					entry = new HashMap<String, Object>();
					inner.put(jsonPaths[index], entry);
				}
				set(newValue, (HashMap<String, Object>) entry, index + 1, jsonPaths);
			}
		}
	}
}
