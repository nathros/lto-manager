package lto.manager.common.database.tables.records;

import java.time.LocalDateTime;

public class RecordFile {
	private int id;
	private String fileName;
	private String filePath;
	private int size;
	private LocalDateTime created;
	private LocalDateTime modified;
	private int tapeID;
	private int crc32;
	private boolean isDirectory;

	public RecordFile(int id, String fileName, String filePath, int size, LocalDateTime created, LocalDateTime modified, int tapeID, int crc32) {
		this.id = id;
		this.fileName = fileName;
		this.filePath = filePath;
		this.size = size;
		this.created = created;
		this.modified = modified;
		this.tapeID = tapeID;
		this.crc32 = crc32;
		setIsDirectory();
	}

	public static RecordFile of(int id, String fileName, String filePath, int size, LocalDateTime created, LocalDateTime modified, int tapeID, int crc32) {
		return new RecordFile(id, fileName, filePath, size, created, modified, tapeID, crc32);
	}

	private void setIsDirectory() {
		isDirectory = fileName.charAt(fileName.length() - 1) == '/';
	}

	public int getID() { return id;	}
	public void setID(int id) { this.id = id; }
	public String getFileName() { return fileName; }
	public String getFileNameCut() {
		if (isDirectory) {
			return fileName.substring(1);
		}
		return fileName;
	}
	public String getFileNameTrim() {
		if (isDirectory) {
			return fileName.substring(0, fileName.length() - 1).substring(1);
		}
		return fileName;
	}
	public void setFileName(String fileName) { this.fileName = fileName; }
	public String getFilePath() { return filePath; }
	public void setFilePath(String filePath) { this.filePath = filePath; }
	public int getFileSize() { return size;	}
	public void setFileSize(int size) { this.size = size; }
	public LocalDateTime getCreatedDateTime() { return created;	}
	public void setCreatedDateTime(LocalDateTime created) { this.created = created; }
	public LocalDateTime getModifiedDateTime() { return modified;	}
	public void setModifiedDateTime(LocalDateTime modified) { this.modified = modified; }
	public int getTapeID() { return tapeID;	}
	public void setTapeID(int tapeID) { this.tapeID = tapeID; }
	public int getCRC32() { return crc32; }
	public void setCRC32(int crc32) { this.crc32 = crc32; }
	public String getCRC32StrHex() { return Integer.toHexString(crc32); }
	public boolean isDirectory() { return isDirectory; }
	public String getAbsolutePath() {
		if (isDirectory) {
			return filePath + getFileNameCut();
		} else {
			return filePath + fileName;
		}
	}
}
