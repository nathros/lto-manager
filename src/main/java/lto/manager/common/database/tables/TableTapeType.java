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

	public static final int COLUMN_INDEX_ID = 0;
	public static final int COLUMN_INDEX_TYPE = 1;
	public static final int COLUMN_INDEX_DESIGNATION = 2;
	public static final int COLUMN_INDEX_DESIGNATION_WORM = 3;
	public static final int COLUMN_INDEX_CAPACITY = 4;

	private static DbTable getSelf() {
		DbSchema schema = Database.schema;
		DbTable table = schema.addTable(TABLE_NAME);

		DbColumn id = table.addColumn(COLUMN_NAME_ID, Types.INTEGER, null);
		//id.primaryKey();
		id.unique();
		id.notNull();

		String key[] = new String[] { COLUMN_NAME_ID};
		table.primaryKey(COLUMN_NAME_ID, key);
		table.addColumn(COLUMN_NAME_TYPE, Types.VARCHAR, 128);
		table.addColumn(COLUMN_NAME_DESIGNATION, Types.VARCHAR, 2);
		table.addColumn(COLUMN_NAME_DESIGNATION_WORM, Types.VARCHAR, 2);
		table.addColumn(COLUMN_NAME_CAPACITY, Types.BIGINT, null);

		return table;
	}

	public static boolean createTable(Connection con) throws SQLException {
		String q = new CreateTableQuery(TableTapeType.table, true).validate().toString();
		q = q.replace(COLUMN_NAME_ID + ")", COLUMN_NAME_ID + " AUTOINCREMENT)"); // TODO better way of autoincrement

		var statment = con.createStatement();

		final long bytesPerGiB = 1000 * 1000 * 1000;
		final long[] tapeSizeGB = {100, 200, 400, 800, 1500, 2500, 6000, 12000, 18000};
		if (!statment.execute(q)) {
			if (addNewType(con, "LTO-7 Type M8", "M8", "", 9000 * bytesPerGiB)) { // Prepopulate values
				char letter = 'T';
				for (int i = 1; i < 10; i++) {
					String worm = "";
					if (i > 3) {
						worm = "L" + letter;
						letter++;
					}
					if (!addNewType(con, "LTO-" + i, "L" + i, worm, tapeSizeGB[i - 1] * bytesPerGiB)) return false;
				}
				return true;
			}
		}
		return false;
	}

	public static boolean addNewType(Connection con, String name, String designation, String designationWORM, long capacity) throws SQLException {
		var statment = con.createStatement();

		InsertQuery iq = new InsertQuery(table);
		iq.addColumn(table.getColumns().get(COLUMN_INDEX_TYPE), name);
		iq.addColumn(table.getColumns().get(COLUMN_INDEX_DESIGNATION), designation);
		iq.addColumn(table.getColumns().get(COLUMN_INDEX_DESIGNATION_WORM), designationWORM);
		iq.addColumn(table.getColumns().get(COLUMN_INDEX_CAPACITY), capacity);

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
		uq.addOrderings(table.getColumns().get(COLUMN_INDEX_TYPE));
		String sql = uq.validate().toString();
		ResultSet result = statment.executeQuery(sql);

		List<RecordTapeType> list = new ArrayList<RecordTapeType>();
		while (result.next()) {
			int id = result.getInt(COLUMN_NAME_ID);
			String type = result.getString(COLUMN_NAME_TYPE);
			String des = result.getString(COLUMN_NAME_DESIGNATION);
			String worm = result.getString(COLUMN_NAME_DESIGNATION_WORM);
			long capacity = result.getLong(TableTapeType.COLUMN_NAME_CAPACITY);
			RecordTapeType tmp = RecordTapeType.of(id, type, des, worm, capacity);
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
		long capacity = result.getLong(TableTapeType.COLUMN_NAME_CAPACITY);
		RecordTapeType tmp = RecordTapeType.of(id, manu, des, worm, capacity);

		return tmp;
	}
}
