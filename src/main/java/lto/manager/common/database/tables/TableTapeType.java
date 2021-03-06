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

public class TableTapeType {
	public static final DbTable table = getSelf();
	public static final String TABLE_NAME = "table_tape_type";
	public static final String COLUMN_NAME_ID = "id_tape_type";
	public static final String COLUMN_NAME_TYPE = "type";

	public static final int COLUMN_INDEX_ID = 0;
	public static final int COLUMN_INDEX_TYPE = 1;

	public static class RecordTapeType {
		private int id;
		private String type;

		public RecordTapeType(int id, String type) {
			this.id = id;
			this.type = type;
		}

		public static RecordTapeType of(int id, String type) {
			return new RecordTapeType(id, type);
		}

		public int getID() { return id; }
		public void setID(int id) { this.id = id; }
		public String getType() { return type; }
		public void setType(String type) { this.type = type; }
	}

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

		return table;
	}

	public static boolean createTable(Connection con) throws SQLException {
		String q = new CreateTableQuery(TableTapeType.table, true).validate().toString();
		q = q.replace(COLUMN_NAME_ID + ")", COLUMN_NAME_ID + " AUTOINCREMENT)"); // TODO better way of autoincrement

		var statment = con.createStatement();

		if (!statment.execute(q)) {
			for (int i = 1; i < 10; i++) {
				if (addNewType(con, "LTO-" + i) == false) return false;
			}
			return addNewType(con, "LTO-7 Type M8");
		}

		return false;
	}

	public static boolean addNewType(Connection con, String name) throws SQLException {
		var statment = con.createStatement();

		InsertQuery iq = new InsertQuery(table);
		iq.addColumn(table.getColumns().get(COLUMN_INDEX_TYPE), name);
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
			RecordTapeType tmp = RecordTapeType.of(id, type);
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
		RecordTapeType tmp = RecordTapeType.of(id, manu);

		return tmp;
	}
}
