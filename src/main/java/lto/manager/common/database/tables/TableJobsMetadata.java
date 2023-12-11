package lto.manager.common.database.tables;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import com.healthmarketscience.sqlbuilder.BinaryCondition;
import com.healthmarketscience.sqlbuilder.CreateTableQuery;
import com.healthmarketscience.sqlbuilder.DeleteQuery;
import com.healthmarketscience.sqlbuilder.InsertQuery;
import com.healthmarketscience.sqlbuilder.SelectQuery;
import com.healthmarketscience.sqlbuilder.dbspec.basic.DbColumn;
import com.healthmarketscience.sqlbuilder.dbspec.basic.DbSchema;
import com.healthmarketscience.sqlbuilder.dbspec.basic.DbTable;

import lto.manager.common.database.Database;
import lto.manager.common.database.tables.records.RecordJobMetadata;
import lto.manager.common.log.Log;

public class TableJobsMetadata {
	public static final DbTable table = getSelf();
	public static final String TABLE_NAME = "table_jobs_metadata";

	public static final String COLUMN_NAME_ID = "id_jobs_metadata";
	public static final String COLUMN_NAME_JOB_ID = TableJobs.COLUMN_NAME_ID;
	public static final String COLUMN_NAME_KEY = "key";
	public static final String COLUMN_NAME_VALUE = "data";

	public static final int COLUMN_INDEX_ID = 0;
	public static final int COLUMN_INDEX_JOB_ID = 1;
	public static final int COLUMN_INDEX_KEY = 2;
	public static final int COLUMN_INDEX_VALUE = 3;

	static private DbTable getSelf() {
		DbSchema schema = Database.schema;
		DbTable table = schema.addTable(TABLE_NAME);

		DbColumn id = table.addColumn(COLUMN_NAME_ID, Types.INTEGER, null);
		//id.primaryKey();
		id.unique();
		id.notNull();

		String key[] = new String[] { COLUMN_NAME_ID};
		table.primaryKey(COLUMN_NAME_ID, key);

		DbColumn jobIDColumn = table.addColumn(COLUMN_NAME_JOB_ID, Types.INTEGER, null);
		DbColumn columns[] = new DbColumn[] { jobIDColumn };
		DbColumn columnsRef[] = new DbColumn[] { TableJobs.table.getColumns().get(TableJobs.COLUMN_INDEX_ID)};
		table.foreignKey(TableJobsMetadata.COLUMN_NAME_JOB_ID, columns, TableJobs.table, columnsRef);

		table.addColumn(COLUMN_NAME_KEY, Types.VARCHAR, 32);
		table.addColumn(COLUMN_NAME_VALUE, Types.VARCHAR, 4096);

		return table;
	}

	public static boolean createTable(Connection con) throws SQLException {
		String q = new CreateTableQuery(TableJobsMetadata.table, true).validate().toString();
		q = q.replace(COLUMN_NAME_ID + ")", COLUMN_NAME_ID + " AUTOINCREMENT)");
		var statment = con.createStatement();
		if (!statment.execute(q)) {
			return true;
		}
		return false;
	}

	private static RecordJobMetadata parseMetadata(ResultSet result) throws SQLException {
		int id = result.getInt(COLUMN_NAME_ID);
		int jobID = result.getInt(COLUMN_NAME_JOB_ID);
		String key = result.getString(COLUMN_NAME_KEY);
		String value = result.getString(COLUMN_NAME_VALUE);
		return RecordJobMetadata.of(id, jobID, key, value);
	}

	public static List<RecordJobMetadata> getAllMetadataByJob(Connection con, int jobID) throws SQLException {
		var statment = con.createStatement();
		SelectQuery uq = new SelectQuery();
		uq.addAllTableColumns(table);
		uq.addCondition(BinaryCondition.equalTo(table.getColumns().get(COLUMN_INDEX_JOB_ID), jobID));
		String sql = uq.validate().toString();
		ResultSet result = statment.executeQuery(sql);
		List<RecordJobMetadata> list = new ArrayList<RecordJobMetadata>();
		while (result.next()) {
			list.add(parseMetadata(result));
		}
		return list;
	}

	public static ResultSet getAllMetadataByJobAndKey(Connection con, int jobID, String key) throws SQLException {
		var statment = con.createStatement();
		SelectQuery uq = new SelectQuery();
		uq.addAllTableColumns(table);
		uq.addCondition(BinaryCondition.equalTo(COLUMN_NAME_JOB_ID, jobID));
		uq.addCondition(BinaryCondition.equalTo(COLUMN_NAME_KEY, key));
		String sql = uq.validate().toString();
		ResultSet result = statment.executeQuery(sql);
		return result;
	}

	public static boolean addMetadata(Connection con, int jobID, RecordJobMetadata meta) throws SQLException {
		var statment = con.createStatement();
		InsertQuery iq = new InsertQuery(table);
		iq.addColumn(table.getColumns().get(COLUMN_INDEX_JOB_ID), jobID);
		iq.addColumn(table.getColumns().get(COLUMN_INDEX_KEY), meta.getKey());
		iq.addColumn(table.getColumns().get(COLUMN_INDEX_VALUE), meta.getValue());
		String q = iq.validate().toString();
		return statment.execute(q);
	}

	public static boolean addAllMetadata(Connection con, int jobID, List<RecordJobMetadata> meta) throws SQLException {
		for (RecordJobMetadata item: meta) {
			if (addMetadata(con, jobID, item)) {
				Log.severe("Failed to insert metadata: " + item.toString());
				break;
			}
		}
		return false;
	}

	public static boolean deleteMetadata(Connection con, int jobID) throws SQLException {
		var statment = con.createStatement();
		DeleteQuery dq = new DeleteQuery(table);
		dq.addCondition(BinaryCondition.equalTo(table.getColumns().get(COLUMN_INDEX_JOB_ID), jobID));
		String sql = dq.validate().toString();
		if (!statment.execute(sql)) {
			return true;
		}
		return false;
	}
}
