package lto.manager.common.database.tables;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.healthmarketscience.sqlbuilder.BinaryCondition;
import com.healthmarketscience.sqlbuilder.ComboCondition;
import com.healthmarketscience.sqlbuilder.CreateIndexQuery;
import com.healthmarketscience.sqlbuilder.CreateTableQuery;
import com.healthmarketscience.sqlbuilder.InsertQuery;
import com.healthmarketscience.sqlbuilder.SelectQuery;
import com.healthmarketscience.sqlbuilder.dbspec.Constraint.Type;
import com.healthmarketscience.sqlbuilder.dbspec.basic.DbColumn;
import com.healthmarketscience.sqlbuilder.dbspec.basic.DbConstraint;
import com.healthmarketscience.sqlbuilder.dbspec.basic.DbSchema;
import com.healthmarketscience.sqlbuilder.dbspec.basic.DbTable;

import lto.manager.common.database.Database;
import lto.manager.common.database.tables.records.RecordFile;

public class TableFile {
	public static DbTable table = getSelf();
	public static final String TABLE_NAME = "table_file";
	public static final String COLUMN_NAME_ID = "id_file";
	public static final String COLUMN_NAME_FILE_NAME_VIRTUAL = "file_name_virt";
	public static final String COLUMN_NAME_FILE_PATH_VIRTUAL = "file_path_virt";
	public static final String COLUMN_NAME_FILE_NAME_PHYSICAL = "file_name_phy";
	public static final String COLUMN_NAME_FILE_PATH_PHYSICAL = "file_path_phy";
	public static final String COLUMN_NAME_FILE_SIZE = "file_size";
	public static final String COLUMN_NAME_FILE_DATE_CREATE = "file_created";
	public static final String COLUMN_NAME_FILE_DATE_MODIFY = "file_modified";
	public static final String COLUMN_NAME_FILE_TAPE_LOC = "file_tape_id";
	public static final String COLUMN_NAME_FILE_CRC32 = "file_crc32";
	public static final String COLUMN_NAME_FILE_CUSTOM_ICON = "file_custom_icon";

	public static final int COLUMN_INDEX_ID = 0;
	public static final int COLUMN_INDEX_FILE_NAME_VIRTUAl = 1;
	public static final int COLUMN_INDEX_FILE_PATH_VIRTUAL = 2;
	public static final int COLUMN_INDEX_FILE_NAME_PHYSICAL = 3;
	public static final int COLUMN_INDEX_FILE_PATH_PHYSICAL = 4;
	public static final int COLUMN_INDEX_FILE_SIZE = 5;
	public static final int COLUMN_INDEX_FILE_DATE_CREATE = 6;
	public static final int COLUMN_INDEX_FILE_DATE_MODFIY = 7;
	public static final int COLUMN_INDEX_FILE_TAPE_LOC = 8;
	public static final int COLUMN_INDEX_FILE_CRC32 = 9;
	public static final int COLUMN_INDEX_FILE_CUSTOM_ICON = 10;

	static DbTable getSelf() {
		DbSchema schema = Database.schema;
		DbTable table = schema.addTable(TABLE_NAME);

		DbColumn id = table.addColumn(COLUMN_NAME_ID, Types.INTEGER, null);
		//id.primaryKey();
		id.unique();
		id.notNull();

		String key[] = new String[] { COLUMN_NAME_ID};
		table.primaryKey(COLUMN_NAME_ID, key);
		var nameColumn = table.addColumn(COLUMN_NAME_FILE_NAME_VIRTUAL, Types.VARCHAR, 256);
		nameColumn.notNull();
		var pathColumn = table.addColumn(COLUMN_NAME_FILE_PATH_VIRTUAL, Types.VARCHAR, 4096);
		pathColumn.notNull();
		table.addColumn(COLUMN_NAME_FILE_NAME_PHYSICAL, Types.VARCHAR, 256);
		table.addColumn(COLUMN_NAME_FILE_PATH_PHYSICAL, Types.VARCHAR, 4096);
		table.addColumn(COLUMN_NAME_FILE_SIZE, Types.INTEGER, null);
		table.addColumn(COLUMN_NAME_FILE_DATE_CREATE, Types.TIMESTAMP_WITH_TIMEZONE, null);
		table.addColumn(COLUMN_NAME_FILE_DATE_MODIFY, Types.TIMESTAMP_WITH_TIMEZONE, null);

		DbColumn tapeTypeForegnColumn = table.addColumn(COLUMN_NAME_FILE_TAPE_LOC, Types.INTEGER, null);
		DbColumn columns[] = new DbColumn[] { tapeTypeForegnColumn};
		DbTable tableTape =  TableTape.table;
		DbColumn columnsRef[] = new DbColumn[] { tableTape.getColumns().get(TableTape.COLUMN_INDEX_ID)};
		table.foreignKey(TableTape.COLUMN_NAME_ID, columns, tableTape, columnsRef);

		table.addColumn(COLUMN_NAME_FILE_CRC32, Types.INTEGER, null);
		table.addColumn(COLUMN_NAME_FILE_CUSTOM_ICON, Types.VARCHAR, 32);

		// Make path and filename part of unique pair
		var unique = new DbConstraint(table, "path_name_pair", Type.UNIQUE, nameColumn, pathColumn);
		table.addConstraint(unique);
		return table;
	}

	public static boolean createTable(Connection con) throws SQLException {
		String q = new CreateTableQuery(TableFile.table, true).validate().toString();
		q = q.replace(COLUMN_NAME_ID + ")", COLUMN_NAME_ID + " AUTOINCREMENT)");

		var statment = con.createStatement();

		boolean result = statment.execute(q);
		if (result) return false;
		q = new CreateIndexQuery(TableFile.table, "index_" + COLUMN_NAME_FILE_PATH_VIRTUAL)
			.addColumns(TableFile.table.getColumns().get(COLUMN_INDEX_FILE_PATH_VIRTUAL))
			.validate().toString();
		result = statment.execute(q);
		if (result) return false;

		LocalDateTime now = LocalDateTime.now();
		RecordFile rootDir = RecordFile.of("/", "/", "", "", 0, now, now, 0, 0, "folder-root");
		try {
			addFiles(con, 0, Arrays.asList(rootDir));
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public static boolean addFiles(Connection con, int tapeID, List<String> files, String workingDir) throws SQLException, IOException {
		var statment = con.createStatement();
		try {
			int index = workingDir.lastIndexOf("/");
			workingDir = workingDir.substring(0, index);
		} catch (Exception e) {
			workingDir = "/";
		}

		for (String fileStr: files) {
			InsertQuery iq = new InsertQuery(table);
			File file = new File(fileStr);

			String abs;
			String name;
			if (file.isDirectory()) {
				name  = "/" + file.getName() + "/";
				abs = file.getParentFile().getAbsolutePath();
			} else {
				name = file.getName();
				abs = file.getParentFile().getAbsolutePath();
			}
			String virtualPath;
			try {
				virtualPath = abs.substring(workingDir.length());
			} catch (Exception e) {
				virtualPath = "";
			}
			iq.addColumn(table.getColumns().get(COLUMN_INDEX_FILE_NAME_VIRTUAl), name);
			iq.addColumn(table.getColumns().get(COLUMN_INDEX_FILE_PATH_VIRTUAL), virtualPath + "/");
			iq.addColumn(table.getColumns().get(COLUMN_INDEX_FILE_SIZE), Files.size(file.toPath()));
			iq.addColumn(table.getColumns().get(COLUMN_INDEX_FILE_DATE_CREATE), "");
			iq.addColumn(table.getColumns().get(COLUMN_INDEX_FILE_DATE_MODFIY), "");
			iq.addColumn(table.getColumns().get(COLUMN_INDEX_FILE_TAPE_LOC), tapeID);
			iq.addColumn(table.getColumns().get(COLUMN_INDEX_FILE_CRC32), 0);
			iq.addColumn(table.getColumns().get(COLUMN_INDEX_FILE_CUSTOM_ICON), "");
			String sql = iq.validate().toString();
			if (statment.execute(sql)) {
				return false;
			}
		}

		return true;
	}

	public static boolean addFiles(Connection con, int tapeID, List<RecordFile> files) throws SQLException, IOException {
		var statment = con.createStatement();

		for (RecordFile file: files) {
			InsertQuery iq = new InsertQuery(table);
			if (file.getID() != Database.NEW_RECORD_ID) iq.addColumn(table.getColumns().get(COLUMN_INDEX_ID), file.getID());
			iq.addColumn(table.getColumns().get(COLUMN_INDEX_FILE_NAME_VIRTUAl), file.getVirtualFileName());
			iq.addColumn(table.getColumns().get(COLUMN_INDEX_FILE_PATH_VIRTUAL), file.getVirtualFilePath());
			iq.addColumn(table.getColumns().get(COLUMN_INDEX_FILE_NAME_PHYSICAL), file.getPhysicalFileName());
			iq.addColumn(table.getColumns().get(COLUMN_INDEX_FILE_PATH_PHYSICAL), file.getPhysicalFilePath());
			iq.addColumn(table.getColumns().get(COLUMN_INDEX_FILE_SIZE), file.getFileSize());
			iq.addColumn(table.getColumns().get(COLUMN_INDEX_FILE_DATE_CREATE), file.getCreatedDateTime());
			iq.addColumn(table.getColumns().get(COLUMN_INDEX_FILE_DATE_MODFIY), file.getModifiedDateTime());
			iq.addColumn(table.getColumns().get(COLUMN_INDEX_FILE_TAPE_LOC), tapeID);
			iq.addColumn(table.getColumns().get(COLUMN_INDEX_FILE_CRC32), file.getCRC32());
			iq.addColumn(table.getColumns().get(COLUMN_INDEX_FILE_CUSTOM_ICON), file.getCustomIcon());
			String sql = iq.validate().toString();
			if (statment.execute(sql)) {
				return false;
			}
		}

		return true;
	}


	public static List<File> getFilesOnTape(Connection con, int tapeID) throws SQLException, IOException {
		var statment = con.createStatement();

		List<File> files = new ArrayList<File>();

		SelectQuery uq = new SelectQuery();
		uq.addAllTableColumns(table);
		uq.addCondition(BinaryCondition.equalTo(table.getColumns().get(COLUMN_INDEX_FILE_TAPE_LOC), tapeID));
		uq.addOrderings(table.getColumns().get(COLUMN_INDEX_ID));
		String sql = uq.validate().toString();

		ResultSet result = statment.executeQuery(sql);

		while (result.next()) {
			String name = result.getString(COLUMN_NAME_FILE_NAME_VIRTUAL);
			String path = result.getString(COLUMN_NAME_FILE_PATH_VIRTUAL);

			if (name == null) name = "";

			files.add(new File(path + File.separator + name));
		}

		return files;
	}

	public static List<RecordFile> getFilesInDir(Connection con, String dir) throws SQLException, IOException {
		var statment = con.createStatement();

		List<RecordFile> files = new ArrayList<RecordFile>();

		SelectQuery uq = new SelectQuery();
		String like = String.format("%s", dir);

		String parentName = dir;
		String parentPath = dir;
		if (!dir.equals("/")) {
			parentName = "/" + Paths.get(dir).getFileName().toString() + "/";
			parentPath = Paths.get(dir).getParent().toString() + "/";
			if (parentPath.equals("//")) parentPath = "/";
		}

		var andConditions = ComboCondition.and(
				BinaryCondition.equalTo(table.getColumns().get(COLUMN_INDEX_FILE_PATH_VIRTUAL), parentPath),
				BinaryCondition.equalTo(table.getColumns().get(COLUMN_INDEX_FILE_NAME_VIRTUAl), parentName));
		var orConditions = ComboCondition.or(andConditions,
				BinaryCondition.like(table.getColumns().get(COLUMN_INDEX_FILE_PATH_VIRTUAL), like));

		uq.addAllTableColumns(table);
		uq
			.addCondition(orConditions)
			.addOrderings(table.getColumns().get(COLUMN_INDEX_FILE_PATH_VIRTUAL))
			.addOrderings(table.getColumns().get(COLUMN_INDEX_FILE_NAME_VIRTUAl));
		String sql = uq.validate().toString();

		ResultSet resultChildren = statment.executeQuery(sql);

		while (resultChildren.next()) {
			int id = resultChildren.getInt(COLUMN_NAME_ID);
			String nameV = resultChildren.getString(COLUMN_NAME_FILE_NAME_VIRTUAL);
			String pathV = resultChildren.getString(COLUMN_NAME_FILE_PATH_VIRTUAL);
			String nameP = resultChildren.getString(COLUMN_NAME_FILE_NAME_PHYSICAL);
			String pathP = resultChildren.getString(COLUMN_NAME_FILE_PATH_PHYSICAL);
			int size = resultChildren.getInt(COLUMN_NAME_FILE_SIZE);
			String createdStr = resultChildren.getString(COLUMN_NAME_FILE_DATE_CREATE);
			LocalDateTime created = LocalDateTime.parse(createdStr);
			String modifiedStr = resultChildren.getString(COLUMN_NAME_FILE_DATE_MODIFY);
			LocalDateTime modified = LocalDateTime.parse(modifiedStr);
			int tapeID = resultChildren.getInt(COLUMN_NAME_FILE_TAPE_LOC);
			int crc32 = resultChildren.getInt(COLUMN_NAME_FILE_CRC32);
			String icon = resultChildren.getString(COLUMN_NAME_FILE_CUSTOM_ICON);
			files.add(RecordFile.of(id, nameV, pathV, nameP, pathP, size, created, modified, tapeID, crc32, icon));
		}

		return files;
	}

}
