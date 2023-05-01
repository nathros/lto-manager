package lto.manager.common.database.tables;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TableSequence {
	public static int getLastInsertID(Connection con) throws SQLException {
		var statment = con.createStatement();
		ResultSet result = statment.executeQuery("SELECT last_insert_rowid();");
		if (result.next()) {
			return result.getInt(1);
		}
		return -1;
	}
}
