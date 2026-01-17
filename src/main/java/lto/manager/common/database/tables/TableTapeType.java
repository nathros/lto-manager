package lto.manager.common.database.tables;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import com.healthmarketscience.sqlbuilder.BinaryCondition;
import com.healthmarketscience.sqlbuilder.CreateTableQuery;
import com.healthmarketscience.sqlbuilder.InsertQuery;
import com.healthmarketscience.sqlbuilder.SelectQuery;
import com.healthmarketscience.sqlbuilder.dbspec.basic.DbColumn;
import com.healthmarketscience.sqlbuilder.dbspec.basic.DbSchema;
import com.healthmarketscience.sqlbuilder.dbspec.basic.DbTable;

import lto.manager.common.database.Database;
import lto.manager.common.database.tables.records.RecordTapeType;

public class TableTapeType {
	public static final DbTable table = getSelf();
	public static final String TABLE_NAME = "table_tape_type";
	public static final String COLUMN_NAME_ID = "id_tape_type";
	public static final String COLUMN_NAME_TYPE = "type";
	public static final String COLUMN_NAME_DESIGNATION = "des";
	public static final String COLUMN_NAME_DESIGNATION_WORM = "worm";
	public static final String COLUMN_NAME_CAPACITY = "capacity_bytes";
	public static final String COLUMN_NAME_COLOUR_TYP = "col_typ";
	public static final String COLUMN_NAME_COLOUR_HP = "col_hp";
	public static final String COLUMN_NAME_COLOUR_WORM_TYP = "col_worm_typ";
	public static final String COLUMN_NAME_COLOUR_WORM_HP = "col_worm_hp";

	public static final int COLUMN_INDEX_ID = 0;
	public static final int COLUMN_INDEX_TYPE = 1;
	public static final int COLUMN_INDEX_DESIGNATION = 2;
	public static final int COLUMN_INDEX_DESIGNATION_WORM = 3;
	public static final int COLUMN_INDEX_CAPACITY = 4;
	public static final int COLUMN_INDEX_COLOUR_TYP = 5;
	public static final int COLUMN_INDEX_COLOUR_HP = 6;
	public static final int COLUMN_INDEX_COLOUR_WORM_TYP = 7;
	public static final int COLUMN_INDEX_COLOUR_WORM_HP = 8;

	private static DbTable getSelf() {
		DbSchema schema = Database.schema;
		DbTable table = schema.addTable(TABLE_NAME);

		DbColumn id = table.addColumn(COLUMN_NAME_ID, Types.INTEGER, null);
		// id.primaryKey();
		id.unique();
		id.notNull();

		String key[] = new String[] { COLUMN_NAME_ID };
		table.primaryKey(COLUMN_NAME_ID, key);
		table.addColumn(COLUMN_NAME_TYPE, Types.VARCHAR, 128);
		table.addColumn(COLUMN_NAME_DESIGNATION, Types.VARCHAR, 2);
		table.addColumn(COLUMN_NAME_DESIGNATION_WORM, Types.VARCHAR, 2);
		table.addColumn(COLUMN_NAME_CAPACITY, Types.BIGINT, null);
		table.addColumn(COLUMN_NAME_COLOUR_TYP, Types.VARCHAR, 16);
		table.addColumn(COLUMN_NAME_COLOUR_HP, Types.VARCHAR, 16);
		table.addColumn(COLUMN_NAME_COLOUR_WORM_TYP, Types.VARCHAR, 16);
		table.addColumn(COLUMN_NAME_COLOUR_WORM_HP, Types.VARCHAR, 16);

		return table;
	}

	public static boolean createTable(Connection con) throws SQLException {
		String q = new CreateTableQuery(TableTapeType.table, true).validate().toString();
		q = q.replace(COLUMN_NAME_ID + ")", COLUMN_NAME_ID + " AUTOINCREMENT)"); // TODO better way of autoincrement

		var statment = con.createStatement();
		if (statment.execute(q)) {
			return false; // Failed to create table
		}

		final long bytesPerGiB = 1000 * 1000 * 1000;

		if (!addNewType(con, "LTO-1", "L1", "", 100 * bytesPerGiB, "black", "blue", "", ""))
			return false;
		if (!addNewType(con, "LTO-2", "L2", "", 200 * bytesPerGiB, "purple", "red-dark", "", ""))
			return false;
		if (!addNewType(con, "LTO-3", "L3", "LT", 400 * bytesPerGiB, "blue-grey", "yellow", "blue-grey", "yellow"))
			return false;
		if (!addNewType(con, "LTO-4", "L4", "LU", 800 * bytesPerGiB, "green-dark", "green", "green-dark", "green"))
			return false;
		if (!addNewType(con, "LTO-5", "L5", "LV", 1500 * bytesPerGiB, "red-dark", "blue-light", "red-dark", "blue-light"))
			return false;
		if (!addNewType(con, "LTO-6", "L6", "LW", 2500 * bytesPerGiB, "black", "purple", "black", "purple"))
			return false;
		if (!addNewType(con, "LTO-7", "L7", "LX", 6000 * bytesPerGiB, "purple", "blue-stale", "purple", "blue-stale"))
			return false;
		if (!addNewType(con, "LTO-7 Type M8", "M8", "", 9000 * bytesPerGiB, "purple", "blue-stale", "", ""))
			return false;
		if (!addNewType(con, "LTO-8", "L8", "LY", 12000 * bytesPerGiB, "red-dark", "green", "red-dark", "green"))
			return false;
		if (!addNewType(con, "LTO-9", "L9", "LZ", 18000 * bytesPerGiB, "green-dark", "blue-light", "green-dark", "blue-light"))
			return false;
		if (!addNewType(con, "LTO-10 30TB", "LA", "LH", 30000 * bytesPerGiB, "black", "purple", "black", "purple"))
			return false;
		if (!addNewType(con, "LTO-10 40TB", "PA", "PH", 40000 * bytesPerGiB, "black", "purple", "black", "purple"))
			return false;
		return true;
	}

	private static boolean addNewType(Connection con, String name, String designation, String designationWORM,
			long capacity, String colour, String colourHP, String colourWORM, String colourWORMHP) throws SQLException {
		var statment = con.createStatement();

		InsertQuery iq = new InsertQuery(table);
		iq.addColumn(table.getColumns().get(COLUMN_INDEX_TYPE), name);
		iq.addColumn(table.getColumns().get(COLUMN_INDEX_DESIGNATION), designation);
		iq.addColumn(table.getColumns().get(COLUMN_INDEX_DESIGNATION_WORM), designationWORM);
		iq.addColumn(table.getColumns().get(COLUMN_INDEX_CAPACITY), capacity);
		iq.addColumn(table.getColumns().get(COLUMN_INDEX_COLOUR_TYP), colour);
		iq.addColumn(table.getColumns().get(COLUMN_INDEX_COLOUR_HP), colourHP);
		iq.addColumn(table.getColumns().get(COLUMN_INDEX_COLOUR_WORM_TYP), colourWORM);
		iq.addColumn(table.getColumns().get(COLUMN_INDEX_COLOUR_WORM_HP), colourWORMHP);

		String sql = iq.validate().toString();
		if (!statment.execute(sql)) {
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
			String type = result.getString(COLUMN_NAME_TYPE);
			String des = result.getString(COLUMN_NAME_DESIGNATION);
			String worm = result.getString(COLUMN_NAME_DESIGNATION_WORM);
			long capacity = result.getLong(COLUMN_NAME_CAPACITY);
			String colour = result.getString(COLUMN_NAME_COLOUR_TYP);
			String colourHP = result.getString(COLUMN_NAME_COLOUR_HP);
			String colourWORM = result.getString(COLUMN_NAME_COLOUR_WORM_TYP);
			String colourWORMHP = result.getString(COLUMN_NAME_COLOUR_WORM_HP);
			RecordTapeType tmp = RecordTapeType.of(id, type, des, worm, capacity, colour, colourHP, colourWORM,
					colourWORMHP);
			list.add(tmp);
		}

		return list;
	}

	public static RecordTapeType getAtID(Connection con, int id) throws SQLException {
		var statment = con.createStatement();

		SelectQuery uq = new SelectQuery();
		uq.addAllTableColumns(table);
		uq.addCondition(BinaryCondition.equalTo(COLUMN_NAME_ID, id));
		String sql = uq.validate().toString();
		ResultSet result = statment.executeQuery(sql);

		id = result.getInt(COLUMN_NAME_ID);
		String manu = result.getString(COLUMN_NAME_TYPE);
		String des = result.getString(COLUMN_NAME_DESIGNATION);
		String worm = result.getString(COLUMN_NAME_DESIGNATION_WORM);
		long capacity = result.getLong(COLUMN_NAME_CAPACITY);
		String colour = result.getString(COLUMN_NAME_COLOUR_TYP);
		String colourHP = result.getString(COLUMN_NAME_COLOUR_HP);
		String colourWORM = result.getString(COLUMN_NAME_COLOUR_WORM_TYP);
		String colourWORMHP = result.getString(COLUMN_NAME_COLOUR_WORM_HP);
		RecordTapeType tmp = RecordTapeType.of(id, manu, des, worm, capacity, colour, colourHP, colourWORM,
				colourWORMHP);

		return tmp;
	}
}
