package lto.manager.common.database.tables;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Map.Entry;

import com.healthmarketscience.sqlbuilder.BinaryCondition;
import com.healthmarketscience.sqlbuilder.CreateTableQuery;
import com.healthmarketscience.sqlbuilder.InsertQuery;
import com.healthmarketscience.sqlbuilder.SelectQuery;
import com.healthmarketscience.sqlbuilder.UpdateQuery;
import com.healthmarketscience.sqlbuilder.dbspec.basic.DbColumn;
import com.healthmarketscience.sqlbuilder.dbspec.basic.DbSchema;
import com.healthmarketscience.sqlbuilder.dbspec.basic.DbTable;

import lto.manager.common.database.Database;
import lto.manager.web.Options;

public class TableOptions {
	public static DbTable table = getSelf();

	public static final String TABLE_NAME = "table_options";
	public static final String COLUMN_NAME_ID = "id_options";
	public static final String COLUMN_NAME_VALUE = "options_value";

	public static final int COLUMN_INDEX_ID = 0;
	public static final int COLUMN_INDEX_VALUE = 1;

	public static final int INDEX_ENABLE_LOG_REQUESTS = 0;
	public static final int INDEX_ENABLE_LOG_EXTERNAL_PROCESS = 1;
	public static final int INDEX_TEST = 99;

	static DbTable getSelf() {
		DbSchema schema = Database.schema;
		DbTable table = schema.addTable(TABLE_NAME);

		DbColumn id = table.addColumn(COLUMN_NAME_ID, Types.INTEGER, null);
		id.unique();
		id.notNull();
		String key[] = new String[] { COLUMN_NAME_ID};
		table.primaryKey(COLUMN_NAME_ID, key);

		table.addColumn(COLUMN_NAME_VALUE, Types.VARCHAR, 4096);
		return table;
	}

	public static boolean createTable(Connection con) throws SQLException {
		String q = new CreateTableQuery(TableOptions.table, true).validate().toString();
		var statment = con.createStatement();

		if (!statment.execute(q)) {
			for (Entry<Integer, String> entry : Options.defaultValues.entrySet()) {
			    Integer key = entry.getKey();
			    String value = entry.getValue();
			    if (!insertOption(con, key, value)) return false;
			}
		}

		return true;
	}

	private static boolean insertOption(Connection con, int index, String value) throws SQLException {
		var statment = con.createStatement();
		InsertQuery iq = new InsertQuery(table);
		iq.addColumn(table.getColumns().get(COLUMN_INDEX_ID), index);
		iq.addColumn(table.getColumns().get(COLUMN_INDEX_VALUE), value);
		String q = iq.validate().toString();
		return !statment.execute(q);
	}

	public static boolean updateOption(Connection con, int index, String newValue) throws SQLException {
		UpdateQuery uq = new UpdateQuery(table);

		uq.addCustomSetClause(table.getColumns().get(COLUMN_INDEX_ID), index);
		uq.addCustomSetClause(table.getColumns().get(COLUMN_INDEX_VALUE), newValue);
		uq.addCondition(BinaryCondition.equalTo(table.getColumns().get(COLUMN_INDEX_ID), index));
		String q = uq.validate().toString();
		var statment = con.createStatement();
		if (!statment.execute(q)) {
			int updates = statment.getUpdateCount();
			if (updates != 1) return insertOption(con, index, newValue);
			return true;
		}

		return true;
	}

	public static String getOption(Connection con, int index) throws SQLException {
		SelectQuery uq = new SelectQuery();

		uq.addAllTableColumns(table);
		uq.addCondition(BinaryCondition.equalTo(table.getColumns().get(COLUMN_INDEX_ID), index));
		String q = uq.validate().toString();

		var statment = con.createStatement();
		ResultSet result = statment.executeQuery(q);
		if (result.next()) {
			String opt = result.getString(COLUMN_NAME_VALUE);
			return opt;
		} else return null;
	}

}
