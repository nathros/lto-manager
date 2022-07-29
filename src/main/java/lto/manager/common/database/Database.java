package lto.	manager.common.database;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

import com.healthmarketscience.sqlbuilder.dbspec.basic.DbSchema;
import com.healthmarketscience.sqlbuilder.dbspec.basic.DbSpec;

import lto.manager.common.database.tables.TableFile;
import lto.manager.common.database.tables.TableManufacturer;
import lto.manager.common.database.tables.TableManufacturer.RecordManufacturer;
import lto.manager.common.database.tables.TableTape;
import lto.manager.common.database.tables.TableTape.RecordTape;
import lto.manager.common.database.tables.TableTapeType;
import lto.manager.common.database.tables.TableTapeType.RecordTapeType;
import lto.manager.common.database.tables.TableVersion;

public class Database {
	public static Connection connection;
	public static DbSpec spec = new DbSpec();
	public static DbSchema schema = spec.addDefaultSchema();

	public static boolean createTables() {
		try {
			if (!TableVersion.createTable(connection)) {
				return false;
			}
			TableVersion.getVersion(connection);
			TableTapeType.createTable(connection);
			TableManufacturer.createTable(connection);
			TableTape.createTable(connection);
			TableFile.createTable(connection);

			//var manu = TableManufacturer.getAll(connection);
			//var type = TableTapeType.getAll(connection);
			//var now = LocalDateTime.now();
			//RecordTape t = new RecordTape(-1, manu.get(1), type.get(6), "#barcode#", "#serial#", 1000, 50, now);
			//TableTape.addTape(connection, t);
			//TableTape.getTapeAtID(connection, 1);
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public static Connection openDatabase(String fileName) {
		try {
			//try {
			//	Files.delete(Paths.get(fileName));  //TODO remove delete database
			//} catch (Exception e) { }

			File dbFile = new File(fileName);
			boolean newDatabase = !dbFile.exists();

			Class.forName("org.sqlite.JDBC");
			String url = "jdbc:sqlite:" + fileName;
			Connection con = DriverManager.getConnection(url);
			connection = con;
			if (newDatabase) {
				createTables();
			}
			con.createStatement().executeUpdate("PRAGMA foreign_keys = ON;");
            return con;
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		return null;
	}


	public static boolean addTape(RecordTape newTape) throws SQLException {
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
		return TableTapeType.addNewType(connection, name);
	}

	public static List<RecordTapeType> getAllTapeTypes() throws SQLException {
		return TableTapeType.getAll(connection);
	}
}
