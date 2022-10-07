package lto.manager.common.database.tables;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import com.healthmarketscience.sqlbuilder.BinaryCondition;
import com.healthmarketscience.sqlbuilder.CreateTableQuery;
import com.healthmarketscience.sqlbuilder.InsertQuery;
import com.healthmarketscience.sqlbuilder.SelectQuery;
import com.healthmarketscience.sqlbuilder.UpdateQuery;
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

	public static final int INDEX_ENABLE_LOGGING = 0;
	public static final int INDEX_TEST = 99;

	static DbTable getSelf() {
		DbSchema schema = Database.schema;
		DbTable table = schema.addTable(TABLE_NAME);
		table.addColumn(COLUMN_NAME_ID, "number", null);
		table.addColumn(COLUMN_NAME_VALUE, Types.VARCHAR, 4096);
		return table;
	}

	public static boolean createTable(Connection con) throws SQLException {
		String q = new CreateTableQuery(TableOptions.table, true).validate().toString();
		var statment = con.createStatement();

		if (!statment.execute(q)) {
			int defaultIndex[] = { INDEX_ENABLE_LOGGING, INDEX_TEST };
			String defaultValues[] = { "true", "test" };

			for (int i = 0; i < defaultIndex.length; i++) {
				InsertQuery iq = new InsertQuery(table);
				iq.addColumn(table.getColumns().get(COLUMN_INDEX_ID), defaultIndex[i]);
				iq.addColumn(table.getColumns().get(COLUMN_INDEX_VALUE), defaultValues[i]);
				q = iq.validate().toString();
				if (statment.execute(q)) {
					return false;
				}
			}
		}

		return true;
	}

	public static boolean updateOption(Connection con, int index, String newValue) throws SQLException {
		UpdateQuery uq = new UpdateQuery(table);

		uq.addCustomSetClause(table.getColumns().get(COLUMN_INDEX_ID), index);
		uq.addCondition(BinaryCondition.equalTo(table.getColumns().get(COLUMN_INDEX_ID), COLUMN_INDEX_ID));
		String q = uq.validate().toString();
		var statment = con.createStatement();
		if (!statment.execute(q)) {
			return true;
		}

		return true;
	}

	public static String getOption(Connection con, int index) throws SQLException {
		SelectQuery uq = new SelectQuery();

		uq.addAllTableColumns(table);
		uq.addCondition(BinaryCondition.equalTo(table.getColumns().get(COLUMN_INDEX_ID), COLUMN_INDEX_ID));
		String q = uq.validate().toString();

		var statment = con.createStatement();
		ResultSet result = statment.executeQuery(q);
		String opt = result.getString(COLUMN_NAME_VALUE);

		return opt;
	}

	public static boolean readAndSetAllOptions(Connection con) throws SQLException {
		String tmp = getOption(con, INDEX_ENABLE_LOGGING);
		Options.logRequests = tmp.equals("true");
		return true;
	}

}
