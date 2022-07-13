package lto.manager.common.database.tables;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;
import java.time.LocalDateTime;

import com.healthmarketscience.sqlbuilder.CreateTableQuery;
import com.healthmarketscience.sqlbuilder.dbspec.basic.DbColumn;
import com.healthmarketscience.sqlbuilder.dbspec.basic.DbSchema;
import com.healthmarketscience.sqlbuilder.dbspec.basic.DbTable;

import lto.manager.common.database.Database;

public class TableFile {
	public static DbTable table = getSelf();
	public static final String TABLE_NAME = "table_file";
	public static final String COLUMN_NAME_ID = "id_file";
	public static final String COLUMN_NAME_FILE_NAME = "file_name";
	public static final String COLUMN_NAME_FILE_PATH = "file_path";
	public static final String COLUMN_NAME_FILE_SIZE = "file_size";
	public static final String COLUMN_NAME_FILE_DATE = "file_created";
	public static final String COLUMN_NAME_FILE_TAPE_LOC = "file_tape_location";

	public static final int COLUMN_INDEX_ID = 0;
	public static final int COLUMN_INDEX_FILE_NAME = 1;
	public static final int COLUMN_INDEX_FILE_PATH = 2;
	public static final int COLUMN_INDEX_FILE_SIZE = 3;
	public static final int COLUMN_INDEX_FILE_DATE = 4;
	public static final int COLUMN_INDEX_FILE_TAPE_LOC = 5;

	public static class RecordFile {
		private int id;
		private String fileName;
		private String filePath;
		private int size;
		private LocalDateTime created;
		private int tapeID;

		public RecordFile(int id, String fileName, String filePath, int size, LocalDateTime created, int tapeID) {
			this.id = id;
			this.fileName = fileName;
			this.filePath = filePath;
			this.size = size;
			this.created = created;
			this.tapeID = tapeID;
		}

		public static RecordFile of(int id, String fileName, String filePath, int size, LocalDateTime created, int tapeID) {
			return new RecordFile(id, fileName, filePath, size, created, tapeID);
		}

		public int getID() { return id;	}
		public void setID(int id) { this.id = id; }
		public String getFileName() { return fileName; }
		public void setFileName(String fileName) { this.fileName = fileName; }
		public String getFilePath() { return filePath; }
		public void setFilePath(String filePath) { this.filePath = filePath; }
		public int getFileSize() { return size;	}
		public void setFileSize(int size) { this.size = size; }
		public LocalDateTime getCreatedDateTime() { return created;	}
		public void setCreatedDateTime(LocalDateTime created) { this.created = created; }
		public int getTapeID() { return tapeID;	}
		public void setTapeID(int tapeID) { this.tapeID = tapeID; }
	}

	static DbTable getSelf() {
		DbSchema schema = Database.schema;
		DbTable table = schema.addTable(TABLE_NAME);

		DbColumn id = table.addColumn(COLUMN_NAME_ID, Types.INTEGER, null);
		//id.primaryKey();
		id.unique();
		id.notNull();

		String key[] = new String[] { COLUMN_NAME_ID};
		table.primaryKey(COLUMN_NAME_ID, key);
		table.addColumn(COLUMN_NAME_FILE_NAME, Types.VARCHAR, 128);
		table.addColumn(COLUMN_NAME_FILE_PATH, Types.VARCHAR, 256);
		table.addColumn(COLUMN_NAME_FILE_SIZE, Types.INTEGER, null);
		table.addColumn(COLUMN_NAME_FILE_DATE, Types.TIMESTAMP, null);

		DbColumn tapeTypeForegnColumn = table.addColumn(COLUMN_NAME_FILE_TAPE_LOC, Types.INTEGER, null);
		DbColumn columns[] = new DbColumn[] { tapeTypeForegnColumn};
		DbTable tableTape =  TableTape.table;
		DbColumn columnsRef[] = new DbColumn[] { tableTape.getColumns().get(TableTape.COLUMN_INDEX_ID)};
		table.foreignKey(TableManufacturer.COLUMN_NAME_ID, columns, tableTape, columnsRef);
		return table;
	}

	public static boolean createTable(Connection con) throws SQLException {
		String q = new CreateTableQuery(TableFile.table, true).validate().toString();
		q = q.replace(COLUMN_NAME_ID + ")", COLUMN_NAME_ID + " AUTOINCREMENT)"); // TODO better way of autoincrement

		var statment = con.createStatement();

		if (!statment.execute(q)) {
			return true;
		}

		return false;
	}

}
