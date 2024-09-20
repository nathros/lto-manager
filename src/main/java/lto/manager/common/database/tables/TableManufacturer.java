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
import lto.manager.common.database.tables.records.RecordManufacturer;

public class TableManufacturer {
	public static final DbTable table = getSelf();
	public static final String TABLE_NAME = "table_manufacturer";

	public static final String COLUMN_NAME_ID = "id_manufacturer";
	public static final String COLUMN_NAME_NAME = "name_manufacturer";

	public static final int COLUMN_INDEX_ID = 0;
	public static final int COLUMN_INDEX_TYPE = 1;

	static private DbTable getSelf() {
		DbSchema schema = Database.schema;
		DbTable table = schema.addTable(TABLE_NAME);

		DbColumn id = table.addColumn(COLUMN_NAME_ID, Types.INTEGER, null);
		//id.primaryKey();
		id.unique();
		id.notNull();

		String key[] = new String[] { COLUMN_NAME_ID};
		table.primaryKey(COLUMN_NAME_ID, key);
		table.addColumn(COLUMN_NAME_NAME, Types.VARCHAR, 128);

		return table;
	}

	public static boolean createTable(Connection con) throws SQLException {
		String q = new CreateTableQuery(TableManufacturer.table, true).validate().toString();
		q = q.replace(COLUMN_NAME_ID + ")", COLUMN_NAME_ID + " AUTOINCREMENT)");

		var statment = con.createStatement();

		List<String> manufacturer = new ArrayList<String>();
		manufacturer.add("Quantum");
		manufacturer.add("IBM");
		manufacturer.add("HP");
		manufacturer.add("Fujifilm");
		manufacturer.add("Spectra");
		manufacturer.add("Dell");
		manufacturer.add("TDK");
		manufacturer.add("SONY");
		manufacturer.add("Maxell");
		manufacturer.add("Overland Tandberg");
		if (!statment.execute(q)) {
			for (int i = 0; i < manufacturer.size(); i++) {
				if (addNewManufacturer(con, manufacturer.get(i)) == false) return false;
			}
			return true;
		}

		return false;
	}

	public static boolean addNewManufacturer(Connection con, String name) throws SQLException {
		var statment = con.createStatement();

		InsertQuery iq = new InsertQuery(table);
		iq.addColumn(table.getColumns().get(COLUMN_INDEX_TYPE), name);
		String sql = iq.validate().toString();
		if (!statment.execute(sql)) {
			return true;
		}
		return false;
	}

	public static List<RecordManufacturer> getAll(Connection con) throws SQLException {
		var statment = con.createStatement();

		SelectQuery uq = new SelectQuery();
		uq.addAllTableColumns(table);
		uq.addOrderings(table.getColumns().get(COLUMN_INDEX_TYPE));
		String sql = uq.validate().toString();
		ResultSet result = statment.executeQuery(sql);

		List<RecordManufacturer> list = new ArrayList<RecordManufacturer>();
		while (result.next()) {
			int id = result.getInt(COLUMN_NAME_ID);
			String manu = result.getString(COLUMN_NAME_NAME);
			RecordManufacturer tmp = RecordManufacturer.of(id, manu);
			list.add(tmp);
		}

		return list;
	}

	public static RecordManufacturer getAtID(Connection con, int id) throws SQLException {
		var statment = con.createStatement();

		SelectQuery uq = new SelectQuery();
		uq.addAllTableColumns(table);
		uq.addCondition(BinaryCondition.equalTo(COLUMN_NAME_ID, id));
		String sql = uq.validate().toString();
		ResultSet result = statment.executeQuery(sql);

		id = result.getInt(COLUMN_NAME_ID);
		String manu = result.getString(COLUMN_NAME_NAME);
		RecordManufacturer tmp = RecordManufacturer.of(id, manu);

		return tmp;
	}
}
