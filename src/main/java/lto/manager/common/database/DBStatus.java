package lto.manager.common.database;

import java.sql.SQLException;
import java.util.function.Function;

public record DBStatus(boolean success, Exception ex, String customMessage) {

	public static DBStatus OK() {
		return new DBStatus(true, null, null);
	}

	public static DBStatus Error(Exception ex, String message) {
		return new DBStatus(false, ex, message);
	}

	public static DBStatus Error(SQLException ex, Function<SQLException, String> pretty) {
		return new DBStatus(false, ex, pretty.apply(ex));
	}

	public void rethrow() throws Exception {
		if (customMessage != null) {
			throw new Exception(customMessage);
		}
		throw ex;
	}
}
