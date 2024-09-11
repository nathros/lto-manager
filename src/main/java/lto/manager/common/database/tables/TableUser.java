package lto.manager.common.database.tables;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.sqlite.SQLiteErrorCode;
import org.sqlite.SQLiteException;

import com.healthmarketscience.sqlbuilder.BinaryCondition;
import com.healthmarketscience.sqlbuilder.CreateTableQuery;
import com.healthmarketscience.sqlbuilder.DeleteQuery;
import com.healthmarketscience.sqlbuilder.InsertQuery;
import com.healthmarketscience.sqlbuilder.SelectQuery;
import com.healthmarketscience.sqlbuilder.SelectQuery.JoinType;
import com.healthmarketscience.sqlbuilder.UpdateQuery;
import com.healthmarketscience.sqlbuilder.dbspec.basic.DbColumn;
import com.healthmarketscience.sqlbuilder.dbspec.basic.DbJoin;
import com.healthmarketscience.sqlbuilder.dbspec.basic.DbSchema;
import com.healthmarketscience.sqlbuilder.dbspec.basic.DbTable;

import lto.manager.common.database.Database;
import lto.manager.common.database.tables.records.RecordRole;
import lto.manager.common.database.tables.records.RecordUser;

public class TableUser {
	public static final DbTable table = getSelf();
	private static DbJoin roleJoin;

	public static final String TABLE_NAME = "table_users";

	public static final String COLUMN_NAME_ID = "id_user";
	public static final String COLUMN_NAME_ROLE = "id_role";
	public static final String COLUMN_NAME_USERNAME = "username";
	public static final String COLUMN_NAME_HASH = "hash";
	public static final String COLUMN_NAME_SALT = "salt";
	public static final String COLUMN_NAME_ENABLED = "enabled";
	public static final String COLUMN_NAME_CREATED = "created";
	public static final String COLUMN_NAME_LANGUAGE = "language";
	public static final String COLUMN_NAME_AVATAR = "avatar";

	public static final int COLUMN_INDEX_ID = 0;
	public static final int COLUMN_INDEX_ROLE = 1;
	public static final int COLUMN_INDEX_USERNAME = 2;
	public static final int COLUMN_INDEX_HASH = 3;
	public static final int COLUMN_INDEX_SALT = 4;
	public static final int COLUMN_INDEX_ENABLED = 5;
	public static final int COLUMN_INDEX_CREATED = 6;
	public static final int COLUMN_INDEX_LANGUAGE = 7;
	public static final int COLUMN_INDEX_AVATAR = 8;

	public static final int MAX_LENGTH_USERNAME = 128;
	public static final int MAX_LENGTH_AVATAR = 128;

	static private DbTable getSelf() {
		DbSchema schema = Database.schema;
		DbTable table = schema.addTable(TABLE_NAME);

		DbColumn id = table.addColumn(COLUMN_NAME_ID, Types.INTEGER, null);
		// id.primaryKey();
		id.unique();
		id.notNull();

		String key[] = new String[] { COLUMN_NAME_ID };
		table.primaryKey(COLUMN_NAME_ID, key);

		DbColumn roleForeignColumn = table.addColumn(COLUMN_NAME_ROLE, Types.INTEGER, null);
		roleForeignColumn.notNull();
		DbTable tableRole = TableRoles.table;
		DbColumn columns[] = new DbColumn[] { roleForeignColumn };
		DbColumn columnsRef[] = new DbColumn[] { tableRole.getColumns().get(TableRoles.COLUMN_INDEX_ID) };
		table.foreignKey(TableRoles.COLUMN_NAME_ID, columns, tableRole, columnsRef);

		table.addColumn(COLUMN_NAME_USERNAME, Types.VARCHAR, MAX_LENGTH_USERNAME).unique();
		table.addColumn(COLUMN_NAME_HASH, Types.VARCHAR, 128).notNull();
		table.addColumn(COLUMN_NAME_SALT, Types.VARCHAR, 32).notNull();
		table.addColumn(COLUMN_NAME_ENABLED, Types.BOOLEAN, null);
		table.addColumn(COLUMN_NAME_CREATED, Types.TIMESTAMP_WITH_TIMEZONE, null);
		table.addColumn(COLUMN_NAME_LANGUAGE, Types.INTEGER, null);
		table.addColumn(COLUMN_NAME_AVATAR, Types.VARCHAR, MAX_LENGTH_AVATAR);

		roleJoin = Database.spec.addJoin(null, TABLE_NAME, null, TableRoles.TABLE_NAME, TableRoles.COLUMN_NAME_ID);

		return table;
	}

	public static boolean createTable(Connection con) throws SQLException {
		String q = new CreateTableQuery(TableUser.table, true).validate().toString();
		q = q.replace(COLUMN_NAME_ID + ")", COLUMN_NAME_ID + " AUTOINCREMENT)");

		var statment = con.createStatement();
		if (!statment.execute(q)) {
			if (addNewUser(con, RecordUser.getDefaultUser()))
			{
				return addNewUser(con, RecordUser.getAnonymousUser());
			}
		}

		return false;
	}

	public static boolean addNewUser(Connection con, RecordUser newUser) throws SQLException {
		var statment = con.createStatement();

		InsertQuery iq = new InsertQuery(table);
		if (newUser.getID() != Database.NEW_RECORD_ID) {
			iq.addColumn(table.getColumns().get(COLUMN_INDEX_ID), newUser.getID());
		}
		iq.addColumn(table.getColumns().get(COLUMN_INDEX_ROLE), newUser.getRole().getID());
		iq.addColumn(table.getColumns().get(COLUMN_INDEX_USERNAME), newUser.getUsername());
		iq.addColumn(table.getColumns().get(COLUMN_INDEX_HASH), newUser.getHash());
		iq.addColumn(table.getColumns().get(COLUMN_INDEX_SALT), newUser.getSalt());
		iq.addColumn(table.getColumns().get(COLUMN_INDEX_ENABLED), newUser.getEnabled());
		iq.addColumn(table.getColumns().get(COLUMN_INDEX_CREATED), newUser.getCreated());
		iq.addColumn(table.getColumns().get(COLUMN_INDEX_LANGUAGE), newUser.getLanguage());
		iq.addColumn(table.getColumns().get(COLUMN_INDEX_AVATAR), newUser.getAvatar());

		String sql = iq.validate().toString();
		if (!statment.execute(sql)) {
			return true;
		}
		return false;
	}

	public static boolean deleteUser(Connection con, int id)
			throws SQLException, IOException {
		if (RecordUser.DEFAULT_ID == id) {
			throw new IllegalArgumentException("Cannot delete default user");
		}
		if (RecordUser.ANONYMOUS_ID == id) {
			throw new IllegalArgumentException("Cannot delete Anonymous user");
		}
		var statment = con.createStatement();

		DeleteQuery dq = new DeleteQuery(table);
		dq.addCondition(BinaryCondition.equalTo(table.getColumns().get(COLUMN_INDEX_ID), id));

		String sql = dq.validate().toString();
		statment.execute(sql);
		if (!statment.execute(sql)) {
			return true;
		}

		return true;
	}

	public static boolean updateUser(Connection con, RecordUser user)
			throws SQLException, IOException {
		var statment = con.createStatement();

		UpdateQuery uq = new UpdateQuery(table);
		uq.addCondition(BinaryCondition.equalTo(table.getColumns().get(COLUMN_INDEX_ID), user.getID()));
		uq.addSetClause(table.getColumns().get(COLUMN_INDEX_ROLE), user.getRole().getID());
		uq.addSetClause(table.getColumns().get(COLUMN_INDEX_USERNAME), user.getUsername());
		uq.addSetClause(table.getColumns().get(COLUMN_INDEX_HASH), user.getHash());
		uq.addSetClause(table.getColumns().get(COLUMN_INDEX_SALT), user.getSalt());
		uq.addSetClause(table.getColumns().get(COLUMN_INDEX_ENABLED), user.getEnabled());
		uq.addSetClause(table.getColumns().get(COLUMN_INDEX_CREATED), user.getCreated());
		uq.addSetClause(table.getColumns().get(COLUMN_INDEX_LANGUAGE), user.getLanguage());
		uq.addSetClause(table.getColumns().get(COLUMN_INDEX_AVATAR), user.getAvatar());

		String sql = uq.validate().toString();
		statment.execute(sql);
		if (!statment.execute(sql)) {
			return true;
		}

		return true;
	}

	public static RecordUser getUserByName(Connection con, String username, boolean includePermissions)
			throws SQLException, IOException {
		var statment = con.createStatement();

		SelectQuery sq = new SelectQuery();
		sq.addAllTableColumns(table);
		sq.addCondition(BinaryCondition.like(table.getColumns().get(COLUMN_INDEX_USERNAME), username));
		if (includePermissions) {
			sq.addAllTableColumns(TableRoles.table);
			sq.addJoins(JoinType.INNER, roleJoin);
		}

		String sql = sq.validate().toString();
		statment.execute(sql);
		var results = statment.getResultSet();
		if (!results.next()) {
			throw new SQLiteException("User not found: [" + username + "]", SQLiteErrorCode.SQLITE_NOTFOUND);
		}
		RecordUser user = fromResultSet(results, includePermissions);
		if (results.next()) {
			throw new SQLiteException("Multiple users have same name: [" + username + "]", SQLiteErrorCode.SQLITE_CONSTRAINT_UNIQUE);
		}
		return user;
	}

	public static RecordUser getUserByID(Connection con, int id, boolean includePermissions)
			throws SQLException, IOException {
		var statment = con.createStatement();

		SelectQuery sq = new SelectQuery();
		sq.addAllTableColumns(table);
		sq.addCondition(BinaryCondition.like(table.getColumns().get(COLUMN_INDEX_ID), id));
		if (includePermissions) {
			sq.addAllTableColumns(TableRoles.table);
			sq.addJoins(JoinType.INNER, roleJoin);
		}

		String sql = sq.validate().toString();
		statment.execute(sql);
		var results = statment.getResultSet();
		if (!results.next()) {
			throw new SQLiteException("User not found: ID[" + id + "]", SQLiteErrorCode.SQLITE_NOTFOUND);
		}
		RecordUser user = fromResultSet(results, includePermissions);
		return user;
	}

	public static List<RecordUser> getUserByRole(Connection con, int roleID, boolean includePermissions)
			throws SQLException, IOException {
		var statment = con.createStatement();

		SelectQuery sq = new SelectQuery();
		sq.addAllTableColumns(table);
		sq.addCondition(BinaryCondition.equalTo(table.getColumns().get(COLUMN_INDEX_ROLE), roleID));
		if (includePermissions) {
			sq.addAllTableColumns(TableRoles.table);
			sq.addJoins(JoinType.INNER, roleJoin);
		}

		List<RecordUser> users = new ArrayList<RecordUser>();
		String sql = sq.validate().toString();
		statment.execute(sql);
		var results = statment.getResultSet();
		while (results.next()) {
			users.add(fromResultSet(results, includePermissions));
		}
		return users;
	}

	public static List<RecordUser> getAllUsers(Connection con, boolean includePermissions)
			throws SQLException, IOException {
		var statment = con.createStatement();

		SelectQuery sq = new SelectQuery();
		sq.addAllTableColumns(table);
		if (includePermissions) {
			sq.addAllTableColumns(TableRoles.table);
			sq.addJoins(JoinType.INNER, roleJoin);
		}

		List<RecordUser> users = new ArrayList<RecordUser>();
		String sql = sq.validate().toString();
		statment.execute(sql);
		var results = statment.getResultSet();
		while (results.next()) {
			users.add(fromResultSet(results, includePermissions));
		}
		return users;
	}

	public static RecordUser fromResultSet(ResultSet result, boolean includePermissions)
			throws SQLException, IOException {
		final int id = result.getInt(COLUMN_NAME_ID);
		final String username = result.getString(COLUMN_NAME_USERNAME);
		final String hash = result.getString(COLUMN_NAME_HASH);
		final String salt = result.getString(COLUMN_NAME_SALT);
		final boolean enabled = result.getBoolean(COLUMN_NAME_ENABLED);
		final String createdStr = result.getString(COLUMN_NAME_CREATED);
		final LocalDateTime created = LocalDateTime.parse(createdStr);
		final int language = result.getInt(COLUMN_NAME_LANGUAGE);
		final String avatar = result.getString(COLUMN_NAME_AVATAR);
		RecordUser user = RecordUser.of(id, username, hash, salt, enabled, created, language, avatar);
		if (includePermissions) {
			RecordRole role = TableRoles.fromResultSet(result);
			user.setRole(role);
		}
		return user;
	}

}
