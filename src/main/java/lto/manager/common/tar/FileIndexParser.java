package lto.manager.common.tar;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FileIndexParser {
	public static List<FileIndex> parseIndexFile(String filePath) {
		List<FileIndex> filesOnTape = new ArrayList<FileIndex>();

		try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
		    String line;
		    while ((line = br.readLine()) != null) {
		    	filesOnTape.add(FileIndex.of(line));
		    }
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return filesOnTape;
	}
}
