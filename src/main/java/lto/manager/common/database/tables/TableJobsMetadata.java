package lto.manager.common.database.tables;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import com.healthmarketscience.sqlbuilder.BinaryCondition;
import com.healthmarketscience.sqlbuilder.CreateTableQuery;
import com.healthmarketscience.sqlbuilder.SelectQuery;
import com.healthmarketscience.sqlbuilder.dbspec.basic.DbColumn;
import com.healthmarketscience.sqlbuilder.dbspec.basic.DbSchema;
import com.healthmarketscience.sqlbuilder.dbspec.basic.DbTable;

import lto.manager.common.database.Database;

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
		q = q.replace(COLUMN_NAME_ID + ")", COLUMN_NAME_ID + " AUTOINCREMENT)"); // TODO better way of autoincrement
		var statment = con.createStatement();
		if (!statment.execute(q)) {
			return true;
		}
		return false;
	}

	public static ResultSet getAllMetadataByJob(Connection con, int jobID) throws SQLException {
		var statment = con.createStatement();
		SelectQuery uq = new SelectQuery();
		uq.addAllTableColumns(table);
		uq.addCondition(BinaryCondition.equalTo(COLUMN_NAME_JOB_ID, jobID));
		String sql = uq.validate().toString();
		ResultSet result = statment.executeQuery(sql);
		return result;
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
}
