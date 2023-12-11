package lto.manager.common.tar;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

import lto.manager.common.database.tables.records.RecordFile;
import lto.manager.common.log.Log;

public class FileIndexParser {
	public static final int INDEX_FILE_SIZE_BYTES = 1024 * 1024 * 128;

	public static List<RecordFile> parseIndexFile(String filePath) {
		List<RecordFile> filesOnTape = new ArrayList<RecordFile>();

		try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
			String line;
			while ((line = br.readLine()) != null) {
				filesOnTape.add(RecordFile.of(line));
			}
		} catch (FileNotFoundException e) {
			Log.severe("Failed to read tar index file: " + filePath + " error: " + e.getMessage());
			return null;
		} catch (IOException e) {
			Log.severe("Failed with error in tar index file: " + filePath + " error: " + e.getMessage());
			return null;
		}
		return filesOnTape;
	}

	public static boolean createNewIndexFile(String filePath, List<RecordFile> fileList) {
		try {
			RandomAccessFile file = new RandomAccessFile(filePath, "rw");
			for (int i = 0; i < fileList.size(); i++) {
				file.writeBytes(fileList.get(i).toIndexRecordString(i));
				file.writeBytes("\n");
			}
			file.setLength(INDEX_FILE_SIZE_BYTES);
			file.close();
		} catch (IOException e) {
			Log.severe("Failed to create new tar index file: " + filePath + " error: " + e.getMessage());
			return false;
		}
		return true;
	}

}
