package lto.manager.common.database.tables;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.healthmarketscience.sqlbuilder.BinaryCondition;
import com.healthmarketscience.sqlbuilder.CreateTableQuery;
import com.healthmarketscience.sqlbuilder.DeleteQuery;
import com.healthmarketscience.sqlbuilder.InsertQuery;
import com.healthmarketscience.sqlbuilder.SelectQuery;
import com.healthmarketscience.sqlbuilder.SelectQuery.JoinType;
import com.healthmarketscience.sqlbuilder.dbspec.basic.DbColumn;
import com.healthmarketscience.sqlbuilder.dbspec.basic.DbJoin;
import com.healthmarketscience.sqlbuilder.dbspec.basic.DbSchema;
import com.healthmarketscience.sqlbuilder.dbspec.basic.DbTable;

import lto.manager.common.database.DBStatus;
import lto.manager.common.database.Database;
import lto.manager.common.database.tables.records.RecordManufacturer;
import lto.manager.common.database.tables.records.RecordTape;
import lto.manager.common.database.tables.records.RecordTape.RecordTapeFormatType;
import lto.manager.common.database.tables.records.RecordTapeType;
import lto.manager.common.log.Log;

public class TableTape {
	public static DbTable table = getSelf();
	private static DbJoin manufacturerJoin;
	private static DbJoin tapeTypeJoin;

	public static final String TABLE_NAME = "table_tape";

	public static final String COLUMN_NAME_ID = "id_tape";
	public static final String COLUMN_NAME_TYPE = TableTapeType.COLUMN_NAME_ID;
	public static final String COLUMN_NAME_BARCODE = "barcode";
	public static final String COLUMN_NAME_SERIAL = "serial";
	public static final String COLUMN_NAME_MANUFACTURER = TableManufacturer.COLUMN_NAME_ID;
	public static final String COLUMN_NAME_FORMAT_TYPE = "format_type";
	public static final String COLUMN_NAME_SPACE_USED = "bytes_used";
	public static final String COLUMN_NAME_DATE_ADDED = "date_added"; // TODO add last read and last write date time
	public static final String COLUMN_NAME_IS_WORM = "is_worm";
	public static final String COLUMN_NAME_IS_ENCRYPTED = "is_encrypted";
	public static final String COLUMN_NAME_IS_COMPRESSED = "is_compressed";

	public static final int COLUMN_INDEX_ID = 0;
	public static final int COLUMN_INDEX_TYPE = 1;
	public static final int COLUMN_INDEX_BARCODE = 2;
	public static final int COLUMN_INDEX_SERIAL = 3;
	public static final int COLUMN_INDEX_MANUFACTURER = 4;
	public static final int COLUMN_INDEX_FORMAT_TYPE = 5;
	public static final int COLUMN_INDEX_SPACE_REMAINING = 6;
	public static final int COLUMN_INDEX_DATE_ADDED = 7;
	public static final int COLUMN_INDEX_IS_WORM = 8;
	public static final int COLUMN_INDEX_IS_ENCRYPTED = 9;
	public static final int COLUMN_INDEX_IS_COMPRESSED = 10;

	public static final int NO_ID = -1;
	public static final int DIR_TAPE_ID = 0;
	public static final int MAX_LEN_BARCODE = 6;
	public static final int MAX_LEN_SERIAL = 128;

	static DbTable getSelf() {
		DbSchema schema = Database.schema;
		DbTable table = schema.addTable(TABLE_NAME);

		DbColumn id = table.addColumn(COLUMN_NAME_ID, Types.INTEGER, null);
		id.unique();
		id.notNull();
		String key[] = new String[] { COLUMN_NAME_ID };
		table.primaryKey(COLUMN_NAME_ID, key);

		DbColumn tapeTypeForegnColumn = table.addColumn(COLUMN_NAME_TYPE, Types.INTEGER, null);
		DbTable tableTapeType = TableTapeType.table;
		DbColumn columns[] = new DbColumn[] { tapeTypeForegnColumn };
		DbColumn columnsRef[] = new DbColumn[] { tableTapeType.getColumns().get(TableTapeType.COLUMN_INDEX_ID) };
		table.foreignKey(TableTapeType.COLUMN_NAME_ID, columns, tableTapeType, columnsRef);

		table.addColumn(COLUMN_NAME_BARCODE, Types.VARCHAR, MAX_LEN_BARCODE);
		table.addColumn(COLUMN_NAME_SERIAL, Types.VARCHAR, MAX_LEN_SERIAL);

		tapeTypeForegnColumn = table.addColumn(COLUMN_NAME_MANUFACTURER, Types.INTEGER, null);
		columns = new DbColumn[] { tapeTypeForegnColumn };
		tableTapeType = TableManufacturer.table;
		columnsRef = new DbColumn[] { tableTapeType.getColumns().get(TableManufacturer.COLUMN_INDEX_ID) };
		table.foreignKey(TableManufacturer.COLUMN_NAME_ID, columns, tableTapeType, columnsRef);

		table.addColumn(COLUMN_NAME_FORMAT_TYPE, Types.TINYINT, null);
		table.addColumn(COLUMN_NAME_SPACE_USED, Types.BIGINT, null);
		table.addColumn(COLUMN_NAME_DATE_ADDED, Types.TIME, null);

		manufacturerJoin = Database.spec.addJoin(null, TABLE_NAME, null, TableManufacturer.TABLE_NAME,
				TableManufacturer.COLUMN_NAME_ID);
		tapeTypeJoin = Database.spec.addJoin(null, TABLE_NAME, null, TableTapeType.TABLE_NAME,
				TableTapeType.COLUMN_NAME_ID);

		table.addColumn(COLUMN_NAME_IS_WORM, Types.BOOLEAN, null);
		table.addColumn(COLUMN_NAME_IS_ENCRYPTED, Types.BOOLEAN, null);
		table.addColumn(COLUMN_NAME_IS_COMPRESSED, Types.BOOLEAN, null);
		return table;
	}

	public static boolean createTable(Connection con) throws SQLException {
		String q = new CreateTableQuery(TableTape.table, true).validate().toString();
		q = q.replace(COLUMN_NAME_ID + ")", COLUMN_NAME_ID + " AUTOINCREMENT)");

		var statment = con.createStatement();

		if (!statment.execute(q)) {
			RecordTape blank = RecordTape.getBlank(); // Used for directories
			DBStatus status = addTape(con, blank);
			return status.success();
		}

		return false;
	}

	public static DBStatus addTape(Connection con, RecordTape newTape) throws SQLException {
		var statment = con.createStatement();

		InsertQuery iq = new InsertQuery(table);
		if (newTape.getID() != NO_ID)
			iq.addColumn(table.getColumns().get(COLUMN_INDEX_ID), newTape.getID());

		iq.addColumn(table.getColumns().get(COLUMN_INDEX_TYPE), newTape.getTapeType().getID());
		iq.addColumn(table.getColumns().get(COLUMN_INDEX_BARCODE), newTape.getBarcode());
		iq.addColumn(table.getColumns().get(COLUMN_INDEX_SERIAL), newTape.getSerial());
		iq.addColumn(table.getColumns().get(COLUMN_INDEX_MANUFACTURER), newTape.getManufacturer().getID());
		iq.addColumn(table.getColumns().get(COLUMN_INDEX_SPACE_REMAINING), newTape.getUsedSpace());
		iq.addColumn(table.getColumns().get(COLUMN_INDEX_DATE_ADDED), Timestamp.valueOf(newTape.getDateAdded()));
		iq.addColumn(table.getColumns().get(COLUMN_INDEX_IS_WORM), newTape.getIsWORM());
		iq.addColumn(table.getColumns().get(COLUMN_INDEX_IS_ENCRYPTED), newTape.getIsEncrypted());
		iq.addColumn(table.getColumns().get(COLUMN_INDEX_IS_COMPRESSED), newTape.getIsCompressed());

		String sql = iq.validate().toString();

		try {
			if (!statment.execute(sql)) {
				return DBStatus.OK();
			} else {
				return DBStatus.Error(null, "Failed to insert: " + sql);
			}
		} catch (Exception e) {
			Log.l.severe(e.getMessage() + " SQL: " + sql);
			if (e.getMessage().contains("foreign")) {
				return DBStatus.Error(e, "Missing tape type or manufacturer");
			}
			return DBStatus.Error(e, sql);
		}
	}

	public static boolean delTape(Connection con, int id) throws SQLException {
		var statment = con.createStatement();

		DeleteQuery dq = new DeleteQuery(table);
		dq.addCondition(BinaryCondition.equalTo(table.getColumns().get(COLUMN_INDEX_ID), id));

		String sql = dq.validate().toString();

		if (!statment.execute(sql)) {
			return true;
		}

		return true;
	}

	public static RecordTape getTapeAtID(Connection con, int id) throws SQLException {
		var statment = con.createStatement();
		SelectQuery uq = new SelectQuery();
		uq.addAllTableColumns(table);
		uq.addAllTableColumns(TableManufacturer.table);
		uq.addAllTableColumns(TableTapeType.table);
		uq.addJoins(JoinType.INNER, manufacturerJoin);
		uq.addJoins(JoinType.INNER, tapeTypeJoin);

		uq.addCondition(BinaryCondition.equalTo(table.getColumns().get(COLUMN_INDEX_ID), id));
		String sql = uq.validate().toString();
		ResultSet result = statment.executeQuery(sql);

		RecordTape tape = null;

		if (result.next()) {
			tape = fromResultSet(result);
		}

		return tape;
	}

	public static List<RecordTape> getAllTapes(Connection con) throws SQLException {
		var statment = con.createStatement();
		SelectQuery uq = new SelectQuery();
		uq.addAllTableColumns(table);
		uq.addAllTableColumns(TableManufacturer.table);
		uq.addAllTableColumns(TableTapeType.table);
		uq.addJoins(JoinType.INNER, manufacturerJoin);
		uq.addJoins(JoinType.INNER, tapeTypeJoin);
		String sql = uq.validate().toString();
		ResultSet result = statment.executeQuery(sql);

		List<RecordTape> tapes = new ArrayList<RecordTape>();
		while (result.next()) {
			tapes.add(fromResultSet(result));
		}

		return tapes;
	}

	public static List<RecordTape> getTapeAtIDRange(Connection con, int start, int end) throws SQLException {
		var statment = con.createStatement();
		SelectQuery uq = new SelectQuery();
		uq.addAllTableColumns(table);
		uq.addAllTableColumns(TableManufacturer.table);
		uq.addAllTableColumns(TableTapeType.table);
		uq.addJoins(JoinType.INNER, manufacturerJoin);
		uq.addJoins(JoinType.INNER, tapeTypeJoin);

		uq.addCondition(BinaryCondition.greaterThan(table.getColumns().get(COLUMN_INDEX_ID), start));
		uq.addCondition(BinaryCondition.lessThan(table.getColumns().get(COLUMN_INDEX_ID), end));
		String sql = uq.validate().toString();
		ResultSet result = statment.executeQuery(sql);

		List<RecordTape> tape = new ArrayList<RecordTape>();

		while (result.next()) {
			tape.add(fromResultSet(result));
		}

		return tape;
	}

	private static RecordTape fromResultSet(ResultSet result) throws SQLException {
		int i = result.getInt(TableManufacturer.COLUMN_NAME_ID);
		String name = result.getString(TableManufacturer.COLUMN_NAME_NAME);
		RecordManufacturer rm = RecordManufacturer.of(i, name);
		RecordTapeFormatType format = RecordTapeFormatType
				.fromInteger(result.getInt(TableTape.COLUMN_NAME_FORMAT_TYPE));
		i = result.getInt(TableTapeType.COLUMN_NAME_ID);
		name = result.getString(TableTapeType.COLUMN_NAME_TYPE);
		String des = result.getString(TableTapeType.COLUMN_NAME_DESIGNATION);
		String worm = result.getString(TableTapeType.COLUMN_NAME_DESIGNATION_WORM);
		long capacity = result.getLong(TableTapeType.COLUMN_NAME_CAPACITY);
		RecordTapeType tt = RecordTapeType.of(i, name, des, worm, capacity);

		i = result.getInt(COLUMN_NAME_ID);
		String barcode = result.getString(COLUMN_NAME_BARCODE);
		String serial = result.getString(COLUMN_NAME_SERIAL);
		long left = result.getLong(COLUMN_NAME_SPACE_USED);
		LocalDateTime time = null;
		try {
			time = result.getTimestamp(COLUMN_NAME_DATE_ADDED).toLocalDateTime();
		} catch (NullPointerException e) { // Do nothing
		} catch (Exception e) {
			Log.l.severe("Table tape get record " + i + "start timestamp error: " + e.getMessage());
		}
		boolean isWorm = result.getBoolean(COLUMN_NAME_IS_WORM);
		boolean isEncrypted = result.getBoolean(COLUMN_NAME_IS_ENCRYPTED);
		boolean isCompressed = result.getBoolean(COLUMN_NAME_IS_COMPRESSED);
		var tape = RecordTape.of(i, rm, tt, barcode, serial, left, format, time, isWorm, isEncrypted, isCompressed);
		return tape;
	}

}
