package lto.manager.common.database.tables;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import com.healthmarketscience.sqlbuilder.CreateTableQuery;
import com.healthmarketscience.sqlbuilder.SelectQuery;
import com.healthmarketscience.sqlbuilder.dbspec.basic.DbColumn;
import com.healthmarketscience.sqlbuilder.dbspec.basic.DbSchema;
import com.healthmarketscience.sqlbuilder.dbspec.basic.DbSpec;
import com.healthmarketscience.sqlbuilder.dbspec.basic.DbTable;

import lto.manager.common.database.tables.TableTapeType.RecordTapeType;

public class TableTape {
	public static DbTable table = getSelf();
	public static final String TABLE_NAME = "table_tape";
	public static final String COLUMN_NAME_ID = "id_tape";
	public static final String COLUMN_NAME_TYPE = "tape_type";
	public static final String COLUMN_NAME_BARCODE = "barcode";
	public static final String COLUMN_NAME_SERIAL = "serial";
	public static final String COLUMN_NAME_MANUFACTURER = "id_manufacturer";
	public static final String COLUMN_NAME_TOTAL_SPACE = "bytes_total";
	public static final String COLUMN_NAME_SPACE_REMAINING = "bytes_remaining";
	public static final String COLUMN_NAME_DATE_ADDED = "date_added";

	public static final int COLUMN_INDEX_ID = 0;
	public static final int COLUMN_INDEX_TYPE = 1;
	public static final int COLUMN_INDEX_BARCODE = 2;
	public static final int COLUMN_INDEX_SERIAL = 3;
	public static final int COLUMN_INDEX_MANUFACTURER = 4;
	public static final int COLUMN_INDEX_TOTAL_SPACE = 5;
	public static final int COLUMN_INDEX_SPACE_REMAINING = 6;
	public static final int COLUMN_INDEX_DATE_ADDED = 7;

	static DbTable getSelf() {
		DbSpec spec = new DbSpec();
		DbSchema schema = spec.addDefaultSchema();
		DbTable table = schema.addTable(TABLE_NAME);

		DbColumn id = table.addColumn(COLUMN_NAME_ID, Types.INTEGER, null);
		id.unique();
		id.notNull();
		String key[] = new String[] { COLUMN_NAME_ID};
		table.primaryKey(COLUMN_NAME_ID, key);

		DbColumn tapeTypeForegnColumn = table.addColumn(COLUMN_NAME_TYPE, Types.INTEGER, null);
		DbTable tableTapeType =  TableTapeType.table;
		DbColumn columns[] = new DbColumn[] { tapeTypeForegnColumn};
		DbColumn columnsRef[] = new DbColumn[] { tableTapeType.getColumns().get(TableTapeType.COLUMN_INDEX_ID)};
		table.foreignKey(TableTapeType.COLUMN_NAME_ID, columns, tableTapeType, columnsRef);

		table.addColumn(COLUMN_NAME_BARCODE, Types.VARCHAR, 128);
		table.addColumn(COLUMN_NAME_SERIAL, Types.VARCHAR, 128);

		tapeTypeForegnColumn = table.addColumn(COLUMN_NAME_MANUFACTURER, Types.INTEGER, null);
		columns = new DbColumn[] { tapeTypeForegnColumn};
		tableTapeType = TableManufacturer.table;
		columnsRef = new DbColumn[] { tableTapeType.getColumns().get(TableManufacturer.COLUMN_INDEX_ID)};
		table.foreignKey(TableManufacturer.COLUMN_NAME_ID, columns, tableTapeType, columnsRef);

		table.addColumn(COLUMN_NAME_TOTAL_SPACE, Types.INTEGER, null);
		table.addColumn(COLUMN_NAME_SPACE_REMAINING, Types.INTEGER, null);
		table.addColumn(COLUMN_NAME_DATE_ADDED, Types.TIME, null);
		return table;
	}

	public static boolean createTable(Connection con) throws SQLException {
		String q = new CreateTableQuery(TableTape.table, true).validate().toString();
		q = q.replace(COLUMN_NAME_ID + ")", COLUMN_NAME_ID + " AUTOINCREMENT)"); // TODO better way of autoincrement

		var statment = con.createStatement();

		if (!statment.execute(q)) {
			return true;
		}

		return false;
	}


	public static List<RecordTapeType> getAll(Connection con) throws SQLException {
		var statment = con.createStatement();

		SelectQuery uq = new SelectQuery();
		uq.addAllTableColumns(table);
		String sql = uq.validate().toString();
		ResultSet result = statment.executeQuery(sql);

		List<RecordTapeType> list = new ArrayList<RecordTapeType>();
		while (result.next()) {
			int id = result.getInt(COLUMN_NAME_ID);
			String type = result.getString(COLUMN_NAME_ID);
			RecordTapeType tmp = RecordTapeType.of(id, type);
			list.add(tmp);
		}

		return list;
	}

}
