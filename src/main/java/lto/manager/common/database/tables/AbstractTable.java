package lto.manager.common.database.tables;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.healthmarketscience.sqlbuilder.dbspec.basic.DbTable;

public abstract class AbstractTable<T, R> { // FIXME refactor so no global database singleton
	protected final DbTable tableDefination;

	protected AbstractTable(DbTable table) {
		tableDefination = table;
	}

	public boolean tableExists(Connection con) throws SQLException {
		final String sql = String.format("SELECT name FROM sqlite_master WHERE name='%s'", tableDefination.getName());
		var statment = con.createStatement();
		if (!statment.execute(sql)) {
			// TODO fill
		}
		return true;
	}

	public abstract T fromResultSet(ResultSet result) throws SQLException, IOException;

	public abstract boolean createTable(Connection con);

	public abstract boolean upgradeTable(Connection con, int currentVersion, int toVersion);

	public abstract boolean insert(Connection con, T t);

	public abstract boolean update(Connection con, T t);

	public abstract boolean delete(Connection con, T t);

	public abstract boolean delete(Connection con, int id);

}
