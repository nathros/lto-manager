package lto.manager.common.database;

public record DBStatus(
	boolean success,
	Exception ex,
	String customMessage) {

	public static DBStatus OK() {
		return new DBStatus(true, null, null);
	}

	public static DBStatus Error(Exception ex, String message) {
		return new DBStatus(false, ex, message);
	}
}
