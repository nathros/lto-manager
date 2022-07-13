package lto.manager.common.database.tables;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.healthmarketscience.sqlbuilder.BinaryCondition;
import com.healthmarketscience.sqlbuilder.CreateTableQuery;
import com.healthmarketscience.sqlbuilder.InsertQuery;
import com.healthmarketscience.sqlbuilder.SelectQuery;
import com.healthmarketscience.sqlbuilder.UpdateQuery;
import com.healthmarketscience.sqlbuilder.dbspec.basic.DbSchema;
import com.healthmarketscience.sqlbuilder.dbspec.basic.DbTable;

import lto.manager.common.database.Database;

public class TableVersion {
	public static DbTable table = getSelf();

	public static final int TABLE_VERSION_CURRENT = 1;
	public static final String TABLE_NAME = "table_version";
	public static final String COLUMN_NAME_ID = "id_version";
	public static final String COLUMN_NAME_VERSION = "database_version";

	public static final int COLUMN_INDEX_ID = 0;
	public static final int COLUMN_INDEX_VERSION = 1;

	static DbTable getSelf() {
		DbSchema schema = Database.schema;
		DbTable table = schema.addTable(TABLE_NAME);
		table.addColumn(COLUMN_NAME_ID, "number", null);
		table.addColumn(COLUMN_NAME_VERSION, "number", null);
		return table;
	}

	public static boolean createTable(Connection con) throws SQLException {
		String q = new CreateTableQuery(TableVersion.table, true).validate().toString();
		var statment = con.createStatement();

		if (!statment.execute(q)) {
			InsertQuery iq = new InsertQuery(table);
			iq.addColumn(table.getColumns().get(COLUMN_INDEX_ID), COLUMN_INDEX_ID);
			iq.addColumn(table.getColumns().get(COLUMN_INDEX_VERSION), TABLE_VERSION_CURRENT);
			q = iq.validate().toString();
			if (!statment.execute(q)) {
				return true;
			}
		}

		return false;
	}

	public static boolean updateVersion(Connection con, int newVersion) throws SQLException {
		UpdateQuery uq = new UpdateQuery(table);

		uq.addCustomSetClause(table.getColumns().get(COLUMN_INDEX_VERSION), newVersion);
		uq.addCondition(BinaryCondition.equalTo(table.getColumns().get(COLUMN_INDEX_ID), COLUMN_INDEX_ID));
		String q = uq.validate().toString();
		var statment = con.createStatement();
		if (!statment.execute(q)) {
			return true;
		}

		return true;
	}

	public static int getVersion(Connection con) throws SQLException {
		SelectQuery uq = new SelectQuery();

		uq.addAllTableColumns(table);
		uq.addCondition(BinaryCondition.equalTo(table.getColumns().get(COLUMN_INDEX_ID), COLUMN_INDEX_ID));
		String q = uq.validate().toString();

		var statment = con.createStatement();
		ResultSet result = statment.executeQuery(q);
		int version = result.getInt(COLUMN_NAME_VERSION);

		return version;
	}
}
