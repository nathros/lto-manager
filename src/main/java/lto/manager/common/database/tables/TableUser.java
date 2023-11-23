package lto.manager.common.database.tables;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.time.LocalDateTime;

import com.healthmarketscience.sqlbuilder.BinaryCondition;
import com.healthmarketscience.sqlbuilder.CreateTableQuery;
import com.healthmarketscience.sqlbuilder.InsertQuery;
import com.healthmarketscience.sqlbuilder.SelectQuery;
import com.healthmarketscience.sqlbuilder.dbspec.basic.DbColumn;
import com.healthmarketscience.sqlbuilder.dbspec.basic.DbSchema;
import com.healthmarketscience.sqlbuilder.dbspec.basic.DbTable;

import lto.manager.common.database.Database;
import lto.manager.common.database.tables.records.RecordUser;

public class TableUser {
	public static final DbTable table = getSelf();
	public static final String TABLE_NAME = "table_users";
	public static final String COLUMN_NAME_ID = "id_user";
	public static final String COLUMN_NAME_USERNAME = "username";
	public static final String COLUMN_NAME_HASH = "hash";
	public static final String COLUMN_NAME_SALT = "salt";
	public static final String COLUMN_NAME_PERMISSION_MASK = "permission";
	public static final String COLUMN_NAME_ENABLED = "enabled";
	public static final String COLUMN_NAME_CREATED = "created";

	public static final int COLUMN_INDEX_ID = 0;
	public static final int COLUMN_INDEX_USERNAME = 1;
	public static final int COLUMN_INDEX_HASH = 2;
	public static final int COLUMN_INDEX_SALT = 3;
	public static final int COLUMN_INDEX_PERMISSION_MASK = 4;
	public static final int COLUMN_INDEX_ENABLED = 5;
	public static final int COLUMN_INDEX_CREATED = 6;

	static private DbTable getSelf() {
		DbSchema schema = Database.schema;
		DbTable table = schema.addTable(TABLE_NAME);

		DbColumn id = table.addColumn(COLUMN_NAME_ID, Types.INTEGER, null);
		//id.primaryKey();
		id.unique();
		id.notNull();

		String key[] = new String[] { COLUMN_NAME_ID};
		table.primaryKey(COLUMN_NAME_ID, key);

		table.addColumn(COLUMN_NAME_USERNAME, Types.VARCHAR, 128).unique();
		table.addColumn(COLUMN_NAME_HASH, Types.VARCHAR, 128);
		table.addColumn(COLUMN_NAME_SALT, Types.VARCHAR, 32);
		table.addColumn(COLUMN_NAME_PERMISSION_MASK, Types.BIGINT, null);
		table.addColumn(COLUMN_NAME_ENABLED, Types.BOOLEAN, null);
		table.addColumn(COLUMN_NAME_CREATED, Types.TIMESTAMP_WITH_TIMEZONE, null);

		return table;
	}

	public static boolean createTable(Connection con) throws SQLException {
		String q = new CreateTableQuery(TableUser.table, true).validate().toString();
		q = q.replace(COLUMN_NAME_ID + ")", COLUMN_NAME_ID + " AUTOINCREMENT)");

		var statment = con.createStatement();
		if (!statment.execute(q)) {
			return addNewUser(con, RecordUser.getDefaultUser());
		}

		return false;
	}

	public static boolean addNewUser(Connection con, RecordUser newUser) throws SQLException {
		var statment = con.createStatement();

		InsertQuery iq = new InsertQuery(table);
		if (newUser.getID() != Database.NEW_RECORD_ID)
		{
			iq.addColumn(table.getColumns().get(COLUMN_INDEX_ID), newUser.getID());
		}
		iq.addColumn(table.getColumns().get(COLUMN_INDEX_USERNAME), newUser.getUsername());
		iq.addColumn(table.getColumns().get(COLUMN_INDEX_HASH), newUser.getHash());
		iq.addColumn(table.getColumns().get(COLUMN_INDEX_SALT), newUser.getSalt());
		iq.addColumn(table.getColumns().get(COLUMN_INDEX_PERMISSION_MASK), newUser.getPermissionMask());
		iq.addColumn(table.getColumns().get(COLUMN_INDEX_ENABLED), newUser.getEnabled());
		iq.addColumn(table.getColumns().get(COLUMN_INDEX_CREATED), newUser.getCreated());

		String sql = iq.validate().toString();
		if (!statment.execute(sql)) {
			return true;
		}
		return false;
	}

	public static RecordUser getUserByName(Connection con, String username) throws SQLException {
		var statment = con.createStatement();

		SelectQuery sq = new SelectQuery();
		sq.addAllTableColumns(table);
		sq.addCondition(BinaryCondition.like(table.getColumns().get(COLUMN_INDEX_USERNAME), username));

		String sql = sq.validate().toString();
		statment.execute(sql);
		var results = statment.getResultSet();
		if (!results.next()) {
			throw new SQLException("User not found: [" + username + "]");
		}
		RecordUser user = fromResultSet(results);
		if (results.next()) {
			throw new SQLException("Multiple users have same name: [" + username + "]");
		}
		return user;
	}

	public static RecordUser fromResultSet(ResultSet result) throws SQLException {
		final int id = result.getInt(COLUMN_NAME_ID);
		final String username = result.getString(COLUMN_NAME_USERNAME);
		final String hash = result.getString(COLUMN_NAME_HASH);
		final String salt = result.getString(COLUMN_NAME_SALT);
		final long permission = result.getLong(COLUMN_NAME_PERMISSION_MASK);
		final boolean enabled = result.getBoolean(COLUMN_NAME_ENABLED);
		final String createdStr = result.getString(COLUMN_NAME_CREATED);
		final LocalDateTime created = LocalDateTime.parse(createdStr);
		return RecordUser.of(id, username, hash, salt, permission, enabled, created);
	}

}
