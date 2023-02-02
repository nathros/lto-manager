package lto.manager.common.database.tables;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
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

import lto.manager.common.database.Database;
import lto.manager.common.database.tables.TableManufacturer.RecordManufacturer;
import lto.manager.common.database.tables.TableTapeType.RecordTapeType;

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
	public static final String COLUMN_NAME_TOTAL_SPACE = "bytes_total";
	public static final String COLUMN_NAME_SPACE_USED = "bytes_used";
	public static final String COLUMN_NAME_DATE_ADDED = "date_added";

	public static final int COLUMN_INDEX_ID = 0;
	public static final int COLUMN_INDEX_TYPE = 1;
	public static final int COLUMN_INDEX_BARCODE = 2;
	public static final int COLUMN_INDEX_SERIAL = 3;
	public static final int COLUMN_INDEX_MANUFACTURER = 4;
	public static final int COLUMN_INDEX_TOTAL_SPACE = 5;
	public static final int COLUMN_INDEX_SPACE_REMAINING = 6;
	public static final int COLUMN_INDEX_DATE_ADDED = 7;

	public static class RecordTape {
		private int id;
		private RecordManufacturer manufacturer;
		private RecordTapeType type;
		private String barcode;
		private String serial;
		private int totalSpace;
		private int usedSpace;
		private LocalDateTime dateAdded;

		public RecordTape(int id, RecordManufacturer manufacturer, RecordTapeType type, String barcode,
				String serial, int totalSpace, int usedSpace, LocalDateTime dateAdded) {
			this.id = id;
			this.manufacturer = manufacturer;
			this.type = type;
			this.barcode = barcode;
			this.serial = serial;
			this.totalSpace = totalSpace;
			this.usedSpace = usedSpace;
			this.dateAdded = dateAdded;
		}

		public static RecordTape of(int id, RecordManufacturer manufacturer, RecordTapeType type, String barcode,
				String serial, int totalSpace, int usedSpace, LocalDateTime dateAdded) {
			return new RecordTape(id, manufacturer, type, barcode, serial, totalSpace, usedSpace, dateAdded);
		}

		public static RecordTape of(int id, int manufacturerID, int typeID, String barcode,
				String serial, int totalSpace, int usedSpace, LocalDateTime dateAdded) {
			RecordManufacturer rm = RecordManufacturer.of(manufacturerID, "");
			RecordTapeType t = RecordTapeType.of(typeID, "", "", "");
			return new RecordTape(id, rm, t, barcode, serial, totalSpace, usedSpace, dateAdded);
		}

		public int getID() { return id; }
		public void setID(int id) { this.id = id; }
		public RecordManufacturer getManufacturer() { return manufacturer; }
		public void setManufacturer(RecordManufacturer manufacturer) { this.manufacturer = manufacturer; }
		public RecordTapeType getTapeType() { return type; }
		public void setTapeType(RecordTapeType type) { this.type = type; }
		public String getBarcode() { return barcode; }
		public void setBarcode(String barcode) { this.barcode = barcode; }
		public String getSerial() { return serial; }
		public void setTapeType(String serial) { this.serial = serial; }
		public int getTotalSpace() { return totalSpace; }
		public float getTotalSpaceTB() { return (float) totalSpace / (1024 * 1024 * 1024); }
		public void setTotalSpace(int totalSpace) { this.totalSpace = totalSpace; }
		public int getUsedSpace() { return usedSpace; }
		public float getUsedSpaceTB() { return (float) usedSpace / (1024 * 1024 * 1024); }
		public void setUsedSpace(int remainingSpace) { this.usedSpace = remainingSpace; }
		public LocalDateTime getDateAdded() { return dateAdded; }
		public void setDateAdded(LocalDateTime dateAdded) { this.dateAdded = dateAdded; }
	}

	static DbTable getSelf() {
		DbSchema schema = Database.schema;
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
		table.addColumn(COLUMN_NAME_SPACE_USED, Types.INTEGER, null);
		table.addColumn(COLUMN_NAME_DATE_ADDED, Types.TIME, null);

		manufacturerJoin = Database.spec.addJoin(null, TABLE_NAME, null, TableManufacturer.TABLE_NAME, TableManufacturer.COLUMN_NAME_ID);
		tapeTypeJoin = Database.spec.addJoin(null, TABLE_NAME, null, TableTapeType.TABLE_NAME, TableTapeType.COLUMN_NAME_ID);

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

	public static boolean addTape(Connection con, RecordTape newTape) throws SQLException {
		var statment = con.createStatement();

		InsertQuery iq = new InsertQuery(table);
		iq.addColumn(table.getColumns().get(COLUMN_INDEX_TYPE), newTape.getTapeType().getID());
		iq.addColumn(table.getColumns().get(COLUMN_INDEX_BARCODE), newTape.getBarcode());
		iq.addColumn(table.getColumns().get(COLUMN_INDEX_SERIAL), newTape.getSerial());
		iq.addColumn(table.getColumns().get(COLUMN_INDEX_MANUFACTURER), newTape.getManufacturer().getID());
		iq.addColumn(table.getColumns().get(COLUMN_INDEX_TOTAL_SPACE), newTape.getTotalSpace());
		iq.addColumn(table.getColumns().get(COLUMN_INDEX_SPACE_REMAINING), newTape.getUsedSpace());
		iq.addColumn(table.getColumns().get(COLUMN_INDEX_DATE_ADDED), newTape.getDateAdded());

		String sql = iq.validate().toString();

		if (!statment.execute(sql)) {
			return true;
		}

		return true;
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
			int i = result.getInt(TableManufacturer.COLUMN_NAME_ID);
			String name = result.getString(TableManufacturer.COLUMN_NAME_NAME);
			RecordManufacturer rm = RecordManufacturer.of(i, name);

			i = result.getInt(TableTapeType.COLUMN_NAME_ID);
			name = result.getString(TableTapeType.COLUMN_NAME_TYPE);
			String des = result.getString(TableTapeType.COLUMN_NAME_DESIGNATION);
			String worm = result.getString(TableTapeType.COLUMN_NAME_DESIGNATION_WORM);
			RecordTapeType tt = RecordTapeType.of(i, name, des, worm);

			i = result.getInt(COLUMN_NAME_ID);
			String barcode = result.getString(COLUMN_NAME_BARCODE);
			String serial = result.getString(COLUMN_NAME_SERIAL);
			int space = result.getInt(COLUMN_NAME_TOTAL_SPACE);
			int left = result.getInt(COLUMN_NAME_SPACE_USED);
			//Time time = result.getTime(COLUMN_NAME_DATE_ADDED);
			tape = RecordTape.of(i, rm, tt, barcode, serial, space, left, null);
		}

		return tape;
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
			int i = result.getInt(TableManufacturer.COLUMN_NAME_ID);
			String name = result.getString(TableManufacturer.COLUMN_NAME_NAME);
			RecordManufacturer rm = RecordManufacturer.of(i, name);

			i = result.getInt(TableTapeType.COLUMN_NAME_ID);
			name = result.getString(TableTapeType.COLUMN_NAME_TYPE);
			String des = result.getString(TableTapeType.COLUMN_NAME_DESIGNATION);
			String worm = result.getString(TableTapeType.COLUMN_NAME_DESIGNATION_WORM);
			RecordTapeType tt = RecordTapeType.of(i, name, des, worm);

			i = result.getInt(COLUMN_NAME_ID);
			String barcode = result.getString(COLUMN_NAME_BARCODE);
			String serial = result.getString(COLUMN_NAME_SERIAL);
			int space = result.getInt(COLUMN_NAME_TOTAL_SPACE);
			int left = result.getInt(COLUMN_NAME_SPACE_USED);
			//Time time = result.getTime(COLUMN_NAME_DATE_ADDED);
			tape.add(RecordTape.of(i, rm, tt, barcode, serial, space, left, null));
		}

		return tape;
	}

}
