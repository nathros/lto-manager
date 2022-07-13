package lto.	manager.common.database;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.LocalDateTime;

import com.healthmarketscience.sqlbuilder.dbspec.basic.DbSchema;
import com.healthmarketscience.sqlbuilder.dbspec.basic.DbSpec;

import lto.manager.common.database.tables.TableFile;
import lto.manager.common.database.tables.TableManufacturer;
import lto.manager.common.database.tables.TableTape;
import lto.manager.common.database.tables.TableTape.RecordTape;
import lto.manager.common.database.tables.TableTapeType;
import lto.manager.common.database.tables.TableVersion;

public class Database {
	private Connection connection;
	public static DbSpec spec = new DbSpec();
	public static DbSchema schema = spec.addDefaultSchema();

	public boolean createTables() {
		try {
			if (!TableVersion.createTable(connection)) {
				return false;
			}
			TableVersion.getVersion(connection);
			TableTapeType.createTable(connection);
			TableManufacturer.createTable(connection);

			TableTape.createTable(connection);
			TableFile.createTable(connection);

			var manu = TableManufacturer.getAll(connection);
			var type = TableTapeType.getAll(connection);
			var now = LocalDateTime.now();
			RecordTape t = new RecordTape(-1, manu.get(1), type.get(6), "#barcode#", "#serial#", 1000, 50, now);
			TableTape.addTape(connection, t);
			TableTape.getTapeAtID(connection, 1);

		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public Connection openDatabase(String fileName) {
		try {
			try {
				Files.delete(Paths.get(fileName));  //TODO remove delete database
			} catch (Exception e) { }

			File dbFile = new File(fileName);
			boolean newDatabase = !dbFile.exists();

			Class.forName("org.sqlite.JDBC");
			String url = "jdbc:sqlite:" + fileName;
			try (Connection con = DriverManager.getConnection(url)) {
				connection = con;
				if (newDatabase) {
					createTables();
				}
	            return con;
	        } catch (SQLException e) {
	            System.out.println(e.getMessage());
	        }

		} catch (Exception e1) {
			e1.printStackTrace();
		}
		return null;
	}

}
