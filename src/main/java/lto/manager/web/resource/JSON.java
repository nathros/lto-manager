package lto.manager.web.resource;

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
}
