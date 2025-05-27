package lto.manager.common.database.tables;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.healthmarketscience.sqlbuilder.BinaryCondition;
import com.healthmarketscience.sqlbuilder.ComboCondition;
import com.healthmarketscience.sqlbuilder.CreateTableQuery;
import com.healthmarketscience.sqlbuilder.DeleteQuery;
import com.healthmarketscience.sqlbuilder.InsertQuery;
import com.healthmarketscience.sqlbuilder.SelectQuery;
import com.healthmarketscience.sqlbuilder.UpdateQuery;
import com.healthmarketscience.sqlbuilder.dbspec.basic.DbColumn;
import com.healthmarketscience.sqlbuilder.dbspec.basic.DbSchema;
import com.healthmarketscience.sqlbuilder.dbspec.basic.DbTable;

import lto.manager.common.database.Database;
import lto.manager.common.database.tables.records.RecordNotification;
import lto.manager.common.database.tables.records.RecordNotification.RecordNotificationLevel;
import lto.manager.common.database.tables.records.RecordNotification.RecordNotificationSource;
import lto.manager.common.database.tables.records.RecordNotification.RecordNotificationType;
import lto.manager.common.database.tables.records.RecordUser;

public class TableNotification {
	public static final DbTable table = getSelf();
	public static final String TABLE_NAME = "table_notification";

	public static final String COLUMN_NAME_ID = "id_notification";
	public static final String COLUMN_NAME_USER_ID = "user_id";
	public static final String COLUMN_NAME_CREATED_DATE_TIME = "created";
	public static final String COLUMN_NAME_LEVEL = "level";
	public static final String COLUMN_NAME_SOURCE = "source";
	public static final String COLUMN_NAME_TYPE = "type";
	public static final String COLUMN_NAME_LABEL = "label";
	public static final String COLUMN_NAME_MESSAGE = "message";
	public static final String COLUMN_NAME_CLEARED = "cleared";

	public static final int COLUMN_INDEX_ID = 0;
	public static final int COLUMN_INDEX_USER_ID = 1;
	public static final int COLUMN_INDEX_CREATED_DATE_TIME = 2;
	public static final int COLUMN_INDEX_LEVEL = 3;
	public static final int COLUMN_INDEX_SOURCE = 4;
	public static final int COLUMN_INDEX_TYPE = 5;
	public static final int COLUMN_INDEX_LABEL = 6;
	public static final int COLUMN_INDEX_MESSAGE = 7;
	public static final int COLUMN_INDEX_CLEARED = 8;

	static private DbTable getSelf() {
		DbSchema schema = Database.schema;
		DbTable table = schema.addTable(TABLE_NAME);

		DbColumn id = table.addColumn(COLUMN_NAME_ID, Types.INTEGER, null);
		// id.primaryKey();
		id.unique();
		id.notNull();

		String key[] = new String[] { COLUMN_NAME_ID };
		table.primaryKey(COLUMN_NAME_ID, key);

		DbColumn roleForeignColumn = table.addColumn(COLUMN_NAME_USER_ID, Types.INTEGER, null);
		DbColumn columns[] = new DbColumn[] { roleForeignColumn };
		DbTable tableUser = TableUser.table;
		DbColumn columnsRef[] = new DbColumn[] { tableUser.getColumns().get(TableRoles.COLUMN_INDEX_ID) };
		table.foreignKey(TableRoles.COLUMN_NAME_ID, columns, tableUser, columnsRef);

		table.addColumn(COLUMN_NAME_CREATED_DATE_TIME, Types.TIMESTAMP_WITH_TIMEZONE, null).notNull();
		table.addColumn(COLUMN_NAME_LEVEL, Types.INTEGER, null).notNull();
		table.addColumn(COLUMN_NAME_SOURCE, Types.INTEGER, null).notNull();
		table.addColumn(COLUMN_NAME_TYPE, Types.INTEGER, null).notNull();
		table.addColumn(COLUMN_NAME_LABEL, Types.VARCHAR, 256).notNull();
		table.addColumn(COLUMN_NAME_MESSAGE, Types.VARCHAR, 1024).notNull();
		table.addColumn(COLUMN_NAME_CLEARED, Types.BOOLEAN, null).notNull();

		return table;
	}

	public static boolean createTable(Connection con) throws SQLException {
		String q = new CreateTableQuery(TableNotification.table, true).validate().toString();
		q = q.replace(COLUMN_NAME_ID + ")", COLUMN_NAME_ID + " AUTOINCREMENT)");
		var statment = con.createStatement();
		if (!statment.execute(q)) {
			return true;
		}
		return false;
	}

	public static boolean addNewNotification(Connection con, RecordNotification notification) throws SQLException {
		var statment = con.createStatement();
		InsertQuery iq = new InsertQuery(table);
		final RecordUser user = notification.getUser();
		if (user != null) {
			iq.addColumn(table.getColumns().get(COLUMN_INDEX_USER_ID), user.getID());
		} else {
			iq.addColumn(table.getColumns().get(COLUMN_INDEX_USER_ID), null);
		}
		iq.addColumn(table.getColumns().get(COLUMN_INDEX_CREATED_DATE_TIME),
				Timestamp.valueOf(notification.getCreated()));
		iq.addColumn(table.getColumns().get(COLUMN_INDEX_LEVEL), notification.getLevel().ordinal());
		iq.addColumn(table.getColumns().get(COLUMN_INDEX_SOURCE), notification.getSource().ordinal());
		iq.addColumn(table.getColumns().get(COLUMN_INDEX_TYPE), notification.getType().ordinal());
		iq.addColumn(table.getColumns().get(COLUMN_INDEX_LABEL), notification.getLabel());
		iq.addColumn(table.getColumns().get(COLUMN_INDEX_MESSAGE), notification.getMessage());
		iq.addColumn(table.getColumns().get(COLUMN_INDEX_CLEARED), notification.getCleared());

		String sql = iq.validate().toString();
		if (!statment.execute(sql)) {
			return true;
		}
		return false;
	}

	public static boolean updateNotification(Connection con, RecordNotification notification) throws SQLException {
		var statment = con.createStatement();
		UpdateQuery uq = new UpdateQuery(table);

		uq.addSetClause(table.getColumns().get(COLUMN_INDEX_ID), notification.getID());
		final RecordUser user = notification.getUser();
		if (user != null) {
			uq.addSetClause(table.getColumns().get(COLUMN_INDEX_USER_ID), user.getID());
		} else {
			uq.addSetClause(table.getColumns().get(COLUMN_INDEX_USER_ID), null);
		}
		uq.addSetClause(table.getColumns().get(COLUMN_INDEX_CREATED_DATE_TIME),
				Timestamp.valueOf(notification.getCreated()));
		uq.addSetClause(table.getColumns().get(COLUMN_INDEX_LEVEL), notification.getLevel().ordinal());
		uq.addSetClause(table.getColumns().get(COLUMN_INDEX_SOURCE), notification.getSource().ordinal());
		uq.addSetClause(table.getColumns().get(COLUMN_INDEX_TYPE), notification.getType().ordinal());
		uq.addSetClause(table.getColumns().get(COLUMN_INDEX_LABEL), notification.getLabel());
		uq.addSetClause(table.getColumns().get(COLUMN_INDEX_MESSAGE), notification.getMessage());
		uq.addSetClause(table.getColumns().get(COLUMN_INDEX_CLEARED), notification.getCleared());

		String sql = uq.validate().toString();
		if (!statment.execute(sql)) {
			return true;
		}
		return false;
	}

	public static boolean addSingletonNotification(Connection con, RecordNotification notification)
			throws SQLException, IOException {
		// For notification that only single one of type can exist

		List<Integer> existing = getNotificationByTypePK(con, notification.getType());

		if (existing.size() > 0) {
			notification.setID(existing.get(0));
			return updateNotification(con, notification);
		} else {
			return addNewNotification(con, notification);
		}
	}

	public static boolean removeNotificationByType(Connection con, RecordNotificationType type) throws SQLException {
		var statment = con.createStatement();

		DeleteQuery dq = new DeleteQuery(table);
		dq.addCondition(BinaryCondition.equalTo(table.getColumns().get(COLUMN_INDEX_TYPE), type.ordinal()));

		String sql = dq.validate().toString();

		if (!statment.execute(sql)) {
			return true;
		}

		return true;
	}

	public static List<RecordNotification> getNotificationByType(Connection con, RecordNotificationType type)
			throws SQLException, IOException {
		var statment = con.createStatement();

		SelectQuery sq = new SelectQuery();
		sq.addAllTableColumns(table);
		sq.addCondition(BinaryCondition.equalTo(table.getColumns().get(COLUMN_INDEX_TYPE), type.ordinal()));

		final String sql = sq.validate().toString();
		ResultSet result = statment.executeQuery(sql);

		List<RecordNotification> foundNotications = new ArrayList<RecordNotification>();
		while (result.next()) {
			foundNotications.add(fromResultSet(result));
		}
		return foundNotications;
	}

	public static List<Integer> getNotificationByTypePK(Connection con, RecordNotificationType type)
			throws SQLException, IOException {
		var statment = con.createStatement();

		SelectQuery sq = new SelectQuery();
		sq.addColumns(table.getColumns().get(COLUMN_INDEX_ID));
		sq.addCondition(BinaryCondition.equalTo(table.getColumns().get(COLUMN_INDEX_TYPE), type.ordinal()));

		final String sql = sq.validate().toString();
		ResultSet result = statment.executeQuery(sql);

		List<Integer> results = new ArrayList<Integer>();
		while (result.next()) {
			results.add(result.getInt(COLUMN_NAME_ID));
		}
		return results;
	}

	public static List<RecordNotification> getNotificationForUser(Connection con, RecordUser user)
			throws SQLException, IOException {
		var statment = con.createStatement();

		SelectQuery sq = new SelectQuery();
		sq.addAllTableColumns(table);
		if (user == null) {
			sq.addCondition(BinaryCondition.equalTo(table.getColumns().get(COLUMN_INDEX_USER_ID), null));
		} else {
			final var c1 = new BinaryCondition(" IS ", table.getColumns().get(COLUMN_INDEX_USER_ID), null);
			final var c2 = BinaryCondition.equalTo(table.getColumns().get(COLUMN_INDEX_USER_ID), user.getID());
			sq.addCondition(ComboCondition.or(c1, c2));
		}

		final String sql = sq.validate().toString();
		ResultSet result = statment.executeQuery(sql);

		List<RecordNotification> foundNotications = new ArrayList<RecordNotification>();
		while (result.next()) {
			foundNotications.add(fromResultSet(result));
		}
		return foundNotications;
	}

	public static RecordNotification fromResultSet(ResultSet result) throws SQLException, IOException {
		final int id = result.getInt(COLUMN_NAME_ID);
		RecordUser user = null;
		final int userID = result.getInt(COLUMN_NAME_USER_ID);
		if (userID != 0) {
			user = RecordUser.of(userID);
		}
		final LocalDateTime created = result.getTimestamp(COLUMN_NAME_CREATED_DATE_TIME).toLocalDateTime();
		final RecordNotificationLevel level = RecordNotificationLevel.values()[result.getInt(COLUMN_NAME_LEVEL)];
		final RecordNotificationSource source = RecordNotificationSource.values()[result.getInt(COLUMN_NAME_SOURCE)];
		final RecordNotificationType type = RecordNotificationType.values()[result.getInt(COLUMN_NAME_TYPE)];
		final String label = result.getString(COLUMN_NAME_LABEL);
		final String message = result.getString(COLUMN_NAME_MESSAGE);
		final boolean cleared = result.getBoolean(COLUMN_NAME_CLEARED);
		return RecordNotification.of(id, user, created, level, source, type, label, message, cleared);
	}

}
