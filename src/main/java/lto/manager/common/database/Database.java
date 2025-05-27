package lto.manager.common.database;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import com.healthmarketscience.sqlbuilder.dbspec.basic.DbSchema;
import com.healthmarketscience.sqlbuilder.dbspec.basic.DbSpec;

import lto.manager.common.Util;
import lto.manager.common.database.jobs.BackupJob;
import lto.manager.common.database.jobs.JobBase;
import lto.manager.common.database.tables.TableFile;
import lto.manager.common.database.tables.TableJobs;
import lto.manager.common.database.tables.TableJobsMetadata;
import lto.manager.common.database.tables.TableLabelPreset;
import lto.manager.common.database.tables.TableManufacturer;
import lto.manager.common.database.tables.TableNotification;
import lto.manager.common.database.tables.TableOptions;
import lto.manager.common.database.tables.TableRoles;
import lto.manager.common.database.tables.TableTape;
import lto.manager.common.database.tables.TableTapeType;
import lto.manager.common.database.tables.TableUser;
import lto.manager.common.database.tables.TableVersion;
import lto.manager.common.database.tables.records.RecordFile;
import lto.manager.common.database.tables.records.RecordLabelPreset;
import lto.manager.common.database.tables.records.RecordManufacturer;
import lto.manager.common.database.tables.records.RecordNotification;
import lto.manager.common.database.tables.records.RecordNotification.RecordNotificationType;
import lto.manager.common.database.tables.records.RecordOptions;
import lto.manager.common.database.tables.records.RecordOptions.OptionsSetting;
import lto.manager.common.database.tables.records.RecordRole;
import lto.manager.common.database.tables.records.RecordTape;
import lto.manager.common.database.tables.records.RecordTapeType;
import lto.manager.common.database.tables.records.RecordUser;
import lto.manager.common.log.Log;

public class Database {
	public static Connection connection;
	public static DbSpec spec = new DbSpec();
	public static DbSchema schema = spec.addDefaultSchema();
	public static final int NEW_RECORD_ID = -1;

	public static boolean createTables() {
		boolean op = true;
		try {
			op = (op && TableVersion.createTable(connection));
			op = (op && TableTapeType.createTable(connection));
			op = (op && TableManufacturer.createTable(connection));
			op = (op && TableTape.createTable(connection));
			op = (op && TableFile.createTable(connection));
			op = (op && TableOptions.createTable(connection));
			op = (op && TableJobs.createTable(connection));
			op = (op && TableJobsMetadata.createTable(connection));
			op = (op && TableRoles.createTable(connection));
			op = (op && TableUser.createTable(connection));
			op = (op && TableNotification.createTable(connection));
			op = (op && TableLabelPreset.createTable(connection));
			Options.refreshCache(); // Cache is created but is empty
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		return op;
	}

	public static Connection openDatabase(String fileName) {
		try {
			File dbFile = new File(fileName);
			boolean newDatabase = !dbFile.exists();

			// Class.forName("org.sqlite.JDBC");
			String url = "jdbc:sqlite:" + fileName;
			Connection con = DriverManager.getConnection(url);
			connection = con;
			if (newDatabase) {
				Log.info("Database does not exist, creating new");
				if (!createTables()) {
					Log.severe("Failed to create database");
				}
			}
			// Enforce foreign keys check on insert
			con.createStatement().executeUpdate("PRAGMA foreign_keys = ON;");
			Log.info("Successfully opened database: " + fileName);
			return con;
		} catch (Exception e1) {
			Log.severe("Failed to open database: " + fileName);
			e1.printStackTrace();
		}
		return null;
	}

	public static boolean close() throws SQLException {
		if (connection.isClosed()) {
			return false;
		} else {
			connection.close();
			return true;
		}
	}

	public static ResultSet executeRawQuery(final String query) throws SQLException {
		var statment = connection.createStatement();
		statment.execute(query);
		return statment.getResultSet();
	}

	public static DBStatus addTape(RecordTape newTape) throws SQLException {
		return TableTape.addTape(connection, newTape);
	}

	public static boolean DelTape(int id) throws SQLException {
		return TableTape.delTape(connection, id);
	}

	public static RecordTape getTapeAtID(int id) throws SQLException {
		return TableTape.getTapeAtID(connection, id);
	}

	public static List<RecordTape> getAllTapes() throws SQLException {
		return TableTape.getAllTapes(connection);
	}

	public static List<RecordTape> getTapeAtIDRange(int StartID, int endID) throws SQLException {
		return TableTape.getTapeAtIDRange(connection, StartID, endID);
	}

	public static boolean addTapeManufacturer(String name) throws SQLException {
		return TableManufacturer.addNewManufacturer(connection, name);
	}

	public static List<RecordManufacturer> getAllTapeManufacturers() throws SQLException {
		return TableManufacturer.getAll(connection);
	}

	public static boolean addNewType(String name) throws SQLException {
		return TableTapeType.addNewType(connection, name, "", "", 0);
	}

	public static List<RecordTapeType> getAllTapeTypes() throws SQLException {
		return TableTapeType.getAll(connection);
	}

	public static List<File> getFilesOnTape(int tapeID) throws SQLException, IOException {
		return TableFile.getFilesOnTape(connection, tapeID);
	}

	public static boolean updateOption(OptionsSetting setting, String value) throws SQLException {
		return TableOptions.updateOption(connection, setting, value);
	}

	public static String getOption(OptionsSetting setting) throws SQLException {
		return TableOptions.getOption(connection, setting);
	}

	public static boolean resetOptions() throws SQLException {
		return TableOptions.reset(connection);
	}

	public static List<RecordOptions> getAllOptions() throws SQLException {
		return TableOptions.getAllOptions(connection);
	}

	public static JobBase getJobAtID(int id) throws Exception {
		var job = TableJobs.getAtID(Database.connection, id);
		var meta = TableJobsMetadata.getAllMetadataByJob(Database.connection, id);
		switch (job.getType()) {
		case BACKUP: {
			BackupJob backupJob = new BackupJob(job, meta);
			return backupJob;
		}
		default:
			return null;
		}
	}

	public static boolean addVirtualDir(String basePath, String fileName) throws SQLException {
		basePath = Util.virtualDirSeperatorsAdd(basePath);
		fileName = Util.virtualDirSeperatorsAdd(fileName);
		RecordFile newDirRecord = RecordFile.newDirectory(fileName, basePath, fileName, basePath);
		return TableFile.addFile(connection, TableTape.DIR_TAPE_ID, newDirRecord);
	}

	public static boolean delVirtualDir(String basePath) throws SQLException, IOException {
		basePath = Util.virtualDirSeperatorsAdd(basePath);
		final var files = TableFile.getFilesInDir(connection, basePath);
		if (files.size() > 1) {
			throw new IOException("Directory is not empty");
		}
		return TableFile.deleteFile(connection, files.get(0).getID());
	}

	public static boolean renameVirtualDir(String basePath, String newFileName) throws SQLException, IOException {
		newFileName = Util.virtualDirSeperatorsAdd(newFileName);
		List<RecordFile> files = TableFile.getFilesInDirRecursive(connection, basePath);
		if (files.size() == 0) {
			throw new IOException("Directory " + basePath + " does not exist ");
		}
		final RecordFile dir = files.get(0);
		final String originalName = dir.getVirtualFileName();
		final String checkPath = Util.replaceLast(basePath, originalName, newFileName);
		if (TableFile.getFilesInDirRecursive(connection, checkPath).size() > 0) {
			throw new IOException("Directory " + checkPath + " already exists");
		}

		dir.setVirtualFileName(newFileName);
		for (int i = 1; i < files.size(); i++) {
			var file = files.get(i);
			String originalPath = file.getVirtualFilePath();
			String newPath = Util.replaceLast(originalPath, originalName, newFileName);
			file.setVirtualFilePath(newPath);
		}
		return TableFile.updateVirtualFiles(connection, files);
	}

	public static boolean changeVirtualDirIcon(String basePath, String icon) throws SQLException, IOException {
		final List<RecordFile> files = TableFile.getFilesInDir(connection, basePath);
		if (files.size() == 0) {
			throw new IOException("Directory " + basePath + " does not exist");
		}
		RecordFile changeFile = files.get(0);
		changeFile.setCustomIcon(icon);
		return TableFile.updateVirtualFileIcon(connection, changeFile.getID(), icon);
	}

	public static RecordUser getUserByName(String username, boolean includePermissions) throws IOException, SQLException {
		return TableUser.getUserByName(connection, username, includePermissions);
	}

	public static RecordUser getUserByID(int id, boolean includePermissions) throws SQLException, IOException {
		return TableUser.getUserByID(connection, id, includePermissions);
	}

	public static boolean deleteUser(int id) throws SQLException, IOException {
		return TableUser.deleteUser(connection, id);
	}

	public static boolean addUser(RecordUser user) throws SQLException, IOException {
		return TableUser.addNewUser(connection, user);
	}

	public static boolean updateUser(RecordUser user) throws SQLException, IOException {
		return TableUser.updateUser(connection, user);
	}

	public static List<RecordUser> getUsersByRole(int roleID, boolean includePermissions) throws SQLException, IOException {
		return TableUser.getUserByRole(connection, roleID, includePermissions);
	}

	public static List<RecordUser> getAllUsers(boolean includePermissions) throws SQLException, IOException {
		return TableUser.getAllUsers(connection, includePermissions);
	}

	public static RecordRole getRole(int roleID) throws SQLException, IOException {
		return TableRoles.getRole(connection, roleID);
	}

	public static List<RecordRole> getAllRoles() throws SQLException, IOException {
		return TableRoles.getAll(Database.connection);
	}

	public static boolean deleteRole(int id) throws SQLException, IOException {
		return TableRoles.deleteRole(Database.connection, id);
	}

	public static List<RecordLabelPreset> getUserLabelPresets(int id) throws SQLException, IOException {
		return TableLabelPreset.getAllForUser(Database.connection, id, true);
	}

	public static boolean addUserLabelPreset(RecordLabelPreset newPreset) throws SQLException, IOException {
		return TableLabelPreset.addPreset(Database.connection, newPreset);
	}

	public static boolean deleteUserLabelPreset(int userID, String name) throws SQLException, IOException {
		return TableLabelPreset.deletePreset(Database.connection, userID, name);
	}

	public static RecordLabelPreset getUserLabelPreset(int userID, String name) throws SQLException, IOException {
		return TableLabelPreset.getPreset(Database.connection, userID, name, true);
	}

	public static boolean addNotification(RecordNotification notification) throws SQLException, IOException {
		return TableNotification.addNewNotification(Database.connection, notification);
	}

	public static boolean addSingletonNotification(RecordNotification notification) throws SQLException, IOException {
		return TableNotification.addSingletonNotification(Database.connection, notification);
	}

	public static boolean removeNotificationByType(RecordNotificationType type) throws SQLException, IOException {
		return TableNotification.removeNotificationByType(Database.connection, type);
	}

	public static List<RecordNotification> getNotificationForUser(RecordUser user) throws SQLException, IOException {
		return TableNotification.getNotificationForUser(Database.connection, user);
	}
}
