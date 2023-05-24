package lto.	manager.common.database;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

import com.healthmarketscience.sqlbuilder.dbspec.basic.DbSchema;
import com.healthmarketscience.sqlbuilder.dbspec.basic.DbSpec;

import lto.manager.common.database.tables.TableFile;
import lto.manager.common.database.tables.TableJobs;
import lto.manager.common.database.tables.TableJobsMetadata;
import lto.manager.common.database.tables.TableManufacturer;
import lto.manager.common.database.tables.TableOptions;
import lto.manager.common.database.tables.TableTape;
import lto.manager.common.database.tables.TableTapeType;
import lto.manager.common.database.tables.TableVersion;
import lto.manager.common.database.tables.records.RecordManufacturer;
import lto.manager.common.database.tables.records.RecordOptions;
import lto.manager.common.database.tables.records.RecordOptions.OptionsSetting;
import lto.manager.common.database.tables.records.RecordTape;
import lto.manager.common.database.tables.records.RecordTapeType;
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

			//Class.forName("org.sqlite.JDBC");
			String url = "jdbc:sqlite:" + fileName;
			Connection con = DriverManager.getConnection(url);
			connection = con;
			if (newDatabase) {
				Log.l.info("Database does not exist, creating new");
				if (!createTables()) {
					Log.l.severe("Failed to create database");
				}
			}
			// Enforce foreign keys check on insert
			con.createStatement().executeUpdate("PRAGMA foreign_keys = ON;");
			Log.l.info("Successfully opened database: " + fileName);
            return con;
		} catch (Exception e1) {
			Log.l.severe("Failed to open database: " + fileName);
			e1.printStackTrace();
		}
		return null;
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

	public static boolean addFilesToTape(int tapeID, List<String> files, String workingDir) throws SQLException, IOException {
		return TableFile.addFiles(connection, tapeID, files, workingDir);
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

	public static List<RecordOptions> getAllOptions() throws SQLException {
		return TableOptions.getAllOptions(connection);
	}
}
