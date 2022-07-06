package lto.	manager.common.database;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import lto.manager.common.database.tables.TableVersion;

public class Database {
	private Connection connection;

	public boolean createTables() {
		try {
			if (!TableVersion.createTable(connection)) {
				return false;
			}

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
