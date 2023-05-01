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
import com.healthmarketscience.sqlbuilder.dbspec.basic.DbColumn;
import com.healthmarketscience.sqlbuilder.dbspec.basic.DbSchema;
import com.healthmarketscience.sqlbuilder.dbspec.basic.DbTable;

import lto.manager.common.database.Database;
import lto.manager.common.database.jobs.BackupJob;
import lto.manager.common.database.jobs.JobBase;
import lto.manager.common.database.tables.records.RecordJob;
import lto.manager.common.database.tables.records.RecordJob.RecordJobStatus;
import lto.manager.common.database.tables.records.RecordJob.RecordJobType;
import lto.manager.common.log.Log;

public class TableJobs {
	public static final DbTable table = getSelf();
	public static final String TABLE_NAME = "table_jobs";
	public static final String COLUMN_NAME_ID = "id_jobs";
	public static final String COLUMN_NAME_NAME = "name";
	public static final String COLUMN_NAME_TYPE = "type";
	public static final String COLUMN_NAME_STATUS = "status";
	public static final String COLUMN_NAME_START_DATE_TIME = "start";
	public static final String COLUMN_NAME_COMPLETED_DATE_TIME = "end";
	public static final String COLUMN_NAME_COMMENT = "comment";

	public static final int COLUMN_INDEX_ID = 0;
	public static final int COLUMN_INDEX_NAME = 1;
	public static final int COLUMN_INDEX_TYPE = 2;
	public static final int COLUMN_INDEX_STATUS = 3;
	public static final int COLUMN_INDEX_START_DATE_TIME = 4;
	public static final int COLUMN_INDEX_END_DATE_TIME = 5;
	public static final int COLUMN_INDEX_COMMENT = 6;

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
		table.addColumn(COLUMN_NAME_TYPE, Types.INTEGER, null);
		table.addColumn(COLUMN_NAME_STATUS, Types.INTEGER, null);
		table.addColumn(COLUMN_NAME_START_DATE_TIME, Types.TIMESTAMP_WITH_TIMEZONE, null);
		table.addColumn(COLUMN_NAME_COMPLETED_DATE_TIME, Types.TIMESTAMP_WITH_TIMEZONE, null);
		table.addColumn(COLUMN_NAME_COMMENT, Types.VARCHAR, 1024);

		return table;
	}

	public static boolean createTable(Connection con) throws SQLException {
		String q = new CreateTableQuery(TableJobs.table, true).validate().toString();
		q = q.replace(COLUMN_NAME_ID + ")", COLUMN_NAME_ID + " AUTOINCREMENT)");
		var statment = con.createStatement();
		if (!statment.execute(q)) {
			return true;
		}
		return false;
	}

	public static boolean addNewJob(Connection con, RecordJob job) throws SQLException {
		var statment = con.createStatement();
		InsertQuery iq = new InsertQuery(table);
		iq.addColumn(table.getColumns().get(COLUMN_INDEX_NAME), job.getName());
		iq.addColumn(table.getColumns().get(COLUMN_INDEX_TYPE), job.getType());
		iq.addColumn(table.getColumns().get(COLUMN_INDEX_STATUS), job.getStatus());
		if (job.getStartDateTime() != null)
			iq.addColumn(table.getColumns().get(COLUMN_INDEX_START_DATE_TIME), Timestamp.valueOf(job.getStartDateTime()));
		if (job.getEndDateTime() != null)
			iq.addColumn(table.getColumns().get(COLUMN_INDEX_END_DATE_TIME), Timestamp.valueOf(job.getEndDateTime()));
		iq.addColumn(table.getColumns().get(COLUMN_INDEX_COMMENT), job.getComment());
		String sql = iq.validate().toString();
		if (!statment.execute(sql)) {
			return true;
		}
		return false;
	}

	public static List<RecordJob> getAtAll(Connection con) throws SQLException {
		var statment = con.createStatement();

		SelectQuery uq = new SelectQuery();
		uq.addAllTableColumns(table);
		String sql = uq.validate().toString();
		ResultSet result = statment.executeQuery(sql);

		List<RecordJob> jobs = new ArrayList<RecordJob>();
		while (result.next()) {
			jobs.add(parseResult(result));
		}
		return jobs;
	}

	private static RecordJob parseResult(ResultSet result) throws SQLException {
		String name = result.getString(COLUMN_NAME_NAME);
		int id = result.getInt(COLUMN_NAME_ID);
		RecordJobType type = RecordJobType.values()[result.getInt(COLUMN_NAME_TYPE)];
		RecordJobStatus status = RecordJobStatus.values()[result.getInt(COLUMN_NAME_STATUS)];
		LocalDateTime start = null;
		LocalDateTime end = null;
		try {
			start = result.getTimestamp(COLUMN_NAME_START_DATE_TIME).toLocalDateTime();
		} catch (NullPointerException e) { // Do nothing
		} catch (Exception e) {
			Log.l.severe("Table jobs get record start timestamp error: " + e.getMessage());
		}
		try {
			end = result.getTimestamp(COLUMN_NAME_COMPLETED_DATE_TIME).toLocalDateTime();
		} catch (NullPointerException e) { // Do nothing
		} catch (Exception e) {
			Log.l.severe("Table jobs get record end timestamp error: " + e.getMessage());
		}
		String comment = result.getString(COLUMN_NAME_COMMENT);
		RecordJob job = RecordJob.of(id, name, type, status, start, end, comment);
		return job;
	}

	public static RecordJob getAtID(Connection con, int id) throws SQLException {
		var statment = con.createStatement();
		SelectQuery uq = new SelectQuery();
		uq.addAllTableColumns(table);
		uq.addCondition(BinaryCondition.equalTo(table.getColumns().get(COLUMN_INDEX_ID), id));
		String sql = uq.validate().toString();
		ResultSet result = statment.executeQuery(sql);
		if (result.next()) {
			RecordJob job = parseResult(result);
			return job;
		}
		return null;
	}

	public static boolean addNewJob(Connection connection, JobBase job) throws SQLException {
		if (addNewJob(connection, job.getRecordJob())) {
			int jobID = TableSequence.getLastInsertID(connection);
			return TableJobsMetadata.addAllMetadata(connection, jobID, job.getRecordJobMetadata());
		}
		return false;
	}

	public BackupJob getBackupJob(Connection connection, int id) throws SQLException {
		RecordJob job = getAtID(connection, id);
		var sets = TableJobsMetadata.getAllMetadataByJob(connection, id);
		BackupJob ret = new BackupJob(job, sets);
		return ret;
	}

	public static boolean deleteJob(Connection connection, int id) throws SQLException {
		TableJobsMetadata.deleteMetadata(connection, id);
		var statment = connection.createStatement();
		DeleteQuery dq = new DeleteQuery(table);
		dq.addCondition(BinaryCondition.equalTo(table.getColumns().get(COLUMN_INDEX_ID), id));
		String sql = dq.validate().toString();
		if (!statment.execute(sql)) {
			return false;
		}
		return true;
	}
}
