package lto.manager.common.database.tables;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

import com.healthmarketscience.sqlbuilder.BinaryCondition;
import com.healthmarketscience.sqlbuilder.CreateTableQuery;
import com.healthmarketscience.sqlbuilder.InsertQuery;
import com.healthmarketscience.sqlbuilder.SelectQuery;
import com.healthmarketscience.sqlbuilder.UpdateQuery;
import com.healthmarketscience.sqlbuilder.dbspec.basic.DbColumn;
import com.healthmarketscience.sqlbuilder.dbspec.basic.DbSchema;
import com.healthmarketscience.sqlbuilder.dbspec.basic.DbTable;

import lto.manager.common.database.Database;
import lto.manager.common.database.Options;
import lto.manager.common.database.tables.records.RecordOptions;
import lto.manager.common.database.tables.records.RecordOptions.OptionsDataType;
import lto.manager.common.database.tables.records.RecordOptions.OptionsSetting;

public class TableOptions {
	public static DbTable table = getSelf();

	public static final String TABLE_NAME = "table_options";
	public static final String COLUMN_NAME_ID = "id_options";
	public static final String COLUMN_NAME_DATA_TYPE = "options_type";
	public static final String COLUMN_NAME_VALUE = "options_value";

	public static final int COLUMN_INDEX_ID = 0;
	public static final int COLUMN_INDEX_DATA_TYPE = 1;
	public static final int COLUMN_INDEX_VALUE = 2;

	static DbTable getSelf() {
		DbSchema schema = Database.schema;
		DbTable table = schema.addTable(TABLE_NAME);

		DbColumn id = table.addColumn(COLUMN_NAME_ID, Types.INTEGER, null);
		id.unique();
		id.notNull();
		String key[] = new String[] { COLUMN_NAME_ID};
		table.primaryKey(COLUMN_NAME_ID, key);
		table.addColumn(COLUMN_NAME_DATA_TYPE, Types.VARCHAR, 32);
		table.addColumn(COLUMN_NAME_VALUE, Types.VARCHAR, 4096);
		return table;
	}

	public static boolean createTable(Connection con) throws SQLException {
		String q = new CreateTableQuery(TableOptions.table, true).validate().toString();
		var statment = con.createStatement();

		if (!statment.execute(q)) {
			var defaultOptions = Options.defaultValues;
			for (OptionsSetting key : defaultOptions.keySet()) {
				Pair<OptionsDataType, String> value = defaultOptions.get(key);
				if (!insertOption(con, key, value.getLeft(), value.getRight())) return false;
			}
		}
		return true;
	}

	private static boolean insertOption(Connection con, OptionsSetting setting, OptionsDataType type, String value) throws SQLException {
		var statment = con.createStatement();
		InsertQuery iq = new InsertQuery(table);
		iq.addColumn(table.getColumns().get(COLUMN_INDEX_ID), setting.ordinal());
		iq.addColumn(table.getColumns().get(COLUMN_INDEX_VALUE), value);
		iq.addColumn(table.getColumns().get(COLUMN_INDEX_DATA_TYPE), type.toString());
		String q = iq.validate().toString();
		return !statment.execute(q);
	}

	public static boolean updateOption(Connection con, OptionsSetting setting, String newValue) throws SQLException {
		UpdateQuery uq = new UpdateQuery(table);

		var index = setting.ordinal();
		uq.addCustomSetClause(table.getColumns().get(COLUMN_INDEX_ID), index);
		uq.addCustomSetClause(table.getColumns().get(COLUMN_INDEX_VALUE), newValue);
		uq.addCondition(BinaryCondition.equalTo(table.getColumns().get(COLUMN_INDEX_ID), index));
		String q = uq.validate().toString();
		var statment = con.createStatement();
		if (!statment.execute(q)) {
			return true;
		}

		return true;
	}

	public static String getOption(Connection con, OptionsSetting setting) throws SQLException {
		SelectQuery uq = new SelectQuery();

		uq.addAllTableColumns(table);
		uq.addCondition(BinaryCondition.equalTo(table.getColumns().get(COLUMN_INDEX_ID), setting.ordinal()));
		String q = uq.validate().toString();

		var statment = con.createStatement();
		ResultSet result = statment.executeQuery(q);
		if (result.next()) {
			String opt = result.getString(COLUMN_NAME_VALUE);
			return opt;
		} else return null;
	}

	public static List<RecordOptions> getAllOptions(Connection con) throws SQLException {
		List<RecordOptions> options = new ArrayList<RecordOptions>();
		SelectQuery uq = new SelectQuery();

		uq.addAllTableColumns(table);
		String q = uq.validate().toString();

		var statment = con.createStatement();
		ResultSet result = statment.executeQuery(q);
		while (result.next()) {
			int index = result.getInt(COLUMN_NAME_ID);
			String type = result.getString(COLUMN_NAME_DATA_TYPE);
			String value = result.getString(COLUMN_NAME_VALUE);
			var opt = RecordOptions.of(index, type, value);
			options.add(opt);
		}
		return options;
	}

}
