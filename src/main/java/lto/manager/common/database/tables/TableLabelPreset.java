package lto.manager.common.database.tables;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import com.healthmarketscience.sqlbuilder.BinaryCondition;
import com.healthmarketscience.sqlbuilder.CreateTableQuery;
import com.healthmarketscience.sqlbuilder.DeleteQuery;
import com.healthmarketscience.sqlbuilder.InsertQuery;
import com.healthmarketscience.sqlbuilder.SelectQuery;
import com.healthmarketscience.sqlbuilder.UpdateQuery;
import com.healthmarketscience.sqlbuilder.dbspec.basic.DbColumn;
import com.healthmarketscience.sqlbuilder.dbspec.basic.DbSchema;
import com.healthmarketscience.sqlbuilder.dbspec.basic.DbTable;

import lto.manager.common.database.Database;
import lto.manager.common.database.tables.records.RecordLabelPreset;
import lto.manager.common.database.tables.records.RecordUser;

public class TableLabelPreset {
	public static DbTable table = getSelf();

	public static final String TABLE_NAME = "table_label_preset";

	public static final String COLUMN_NAME_ID = "id_label_preset";
	public static final String COLUMN_NAME_USER = "id_user";
	public static final String COLUMN_NAME_NAME = "name";
	public static final String COLUMN_NAME_CONFIG = "config";

	public static final int COLUMN_INDEX_ID = 0;
	public static final int COLUMN_INDEX_USER = 1;
	public static final int COLUMN_INDEX_NAME = 2;
	public static final int COLUMN_INDEX_CONFIG = 3;

	static DbTable getSelf() {
		DbSchema schema = Database.schema;
		DbTable table = schema.addTable(TABLE_NAME);

		DbColumn id = table.addColumn(COLUMN_NAME_ID, Types.INTEGER, null);
		id.unique();
		id.notNull();
		String key[] = new String[] { COLUMN_NAME_ID };
		table.primaryKey(COLUMN_NAME_ID, key);

		DbColumn userForeignColumn = table.addColumn(COLUMN_NAME_USER, Types.INTEGER, null);
		DbTable tableUser = TableUser.table;
		DbColumn columns[] = new DbColumn[] { userForeignColumn };
		DbColumn columnsRef[] = new DbColumn[] { tableUser.getColumns().get(TableUser.COLUMN_INDEX_ID) };
		table.foreignKey(TableUser.COLUMN_NAME_ID, columns, tableUser, columnsRef);

		table.addColumn(COLUMN_NAME_NAME, Types.VARCHAR, null);
		table.addColumn(COLUMN_NAME_CONFIG, Types.VARCHAR, null);

		return table;
	}

	public static boolean createTable(Connection con) throws SQLException {
		String q = new CreateTableQuery(TableLabelPreset.table, true).validate().toString();
		q = q.replace(COLUMN_NAME_ID + ")", COLUMN_NAME_ID + " AUTOINCREMENT)");

		var statment = con.createStatement();

		if (!statment.execute(q)) {
			return true;
		}

		return false;
	}

	public static boolean addPreset(Connection con, RecordLabelPreset newPreset) throws SQLException {
		var statment = con.createStatement();

		InsertQuery iq = new InsertQuery(table);
		if (newPreset.getID() != Database.NEW_RECORD_ID) {
			iq.addColumn(table.getColumns().get(COLUMN_INDEX_ID), newPreset.getID());
		}
		iq.addColumn(table.getColumns().get(COLUMN_INDEX_USER), newPreset.getUser().getID());
		iq.addColumn(table.getColumns().get(COLUMN_INDEX_NAME), newPreset.getName());
		iq.addColumn(table.getColumns().get(COLUMN_INDEX_CONFIG), newPreset.getConfig());

		String sql = iq.validate().toString();
		if (!statment.execute(sql)) {
			return true;
		}
		return false;
	}

	public static boolean deletePreset(Connection con, int id)
			throws SQLException, IOException {
		var statment = con.createStatement();

		DeleteQuery dq = new DeleteQuery(table);
		dq.addCondition(BinaryCondition.equalTo(table.getColumns().get(COLUMN_INDEX_ID), id));

		String sql = dq.validate().toString();
		statment.execute(sql);
		return statment.getUpdateCount() > 0;
	}

	public static boolean deletePreset(Connection con, int userID, String name)
			throws SQLException, IOException {
		var statment = con.createStatement();

		DeleteQuery dq = new DeleteQuery(table);
		dq.addCondition(BinaryCondition.equalTo(table.getColumns().get(COLUMN_INDEX_USER), userID));
		dq.addCondition(BinaryCondition.equalTo(table.getColumns().get(COLUMN_INDEX_NAME), name));

		String sql = dq.validate().toString();
		statment.execute(sql);
		return statment.getUpdateCount() > 0;
	}

	public static boolean updatePreset(Connection con, RecordLabelPreset newPreset)
			throws SQLException, IOException {
		var statment = con.createStatement();

		UpdateQuery uq = new UpdateQuery(table);
		uq.addCondition(BinaryCondition.equalTo(table.getColumns().get(COLUMN_INDEX_ID), newPreset.getID()));
		uq.addSetClause(table.getColumns().get(COLUMN_INDEX_USER), newPreset.getUser().getID());
		uq.addSetClause(table.getColumns().get(COLUMN_INDEX_CONFIG), newPreset.getConfig());

		String sql = uq.validate().toString();
		statment.execute(sql);
		if (!statment.execute(sql)) {
			return true;
		}

		return true;
	}

	public static RecordLabelPreset getPreset(Connection con, int userID, String name, boolean lazyUserDetails)
			throws SQLException, IOException {
		var statment = con.createStatement();

		SelectQuery sq = new SelectQuery();
		sq.addAllTableColumns(table);
		sq.addCondition(BinaryCondition.equalTo(table.getColumns().get(COLUMN_INDEX_USER), userID));
		sq.addCondition(BinaryCondition.equalTo(table.getColumns().get(COLUMN_INDEX_NAME), name));

		String sql = sq.validate().toString();
		statment.execute(sql);
		var results = statment.getResultSet();
		while (results.next()) {
			return fromResultSet(results, lazyUserDetails);
		}
		return null;
	}

	public static List<RecordLabelPreset> getAllForUser(Connection con, int userID, boolean lazyUserDetails)
			throws SQLException, IOException {
		var statment = con.createStatement();

		SelectQuery sq = new SelectQuery();
		sq.addAllTableColumns(table);
		sq.addCondition(BinaryCondition.equalTo(table.getColumns().get(COLUMN_INDEX_USER), userID));

		List<RecordLabelPreset> presets = new ArrayList<RecordLabelPreset>();
		String sql = sq.validate().toString();
		statment.execute(sql);
		var results = statment.getResultSet();
		while (results.next()) {
			presets.add(fromResultSet(results, lazyUserDetails));
		}
		return presets;
	}

	public static RecordLabelPreset fromResultSet(ResultSet result, boolean lazyUserDetails)
			throws SQLException, IOException {
		final int id = result.getInt(COLUMN_NAME_ID);
		final int UserId = result.getInt(COLUMN_NAME_USER);
		final String name = result.getString(COLUMN_NAME_NAME);
		final String configStr = result.getString(COLUMN_NAME_CONFIG);
		RecordUser user = RecordUser.getBlank();
		user.setID(UserId);
		return RecordLabelPreset.of(id, user, name, configStr);
	}

}
