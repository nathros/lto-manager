package lto.manager.common.database.tables.records;

import java.time.LocalDateTime;

public class RecordFile {
	private int id;
	private String fileNameVirtual;
	private String filePathVirtual;
	private String fileNamePhysical;
	private String filePathPhysical;
	private int size;
	private LocalDateTime created;
	private LocalDateTime modified;
	private int tapeID;
	private int crc32;
	private boolean isDirectory;

	public RecordFile(int id, String fileNameVirt, String filePathVirt, String fileNamePhy, String filePathPhy, int size, LocalDateTime created, LocalDateTime modified, int tapeID, int crc32) {
		this.id = id;
		this.fileNameVirtual = fileNameVirt;
		this.filePathVirtual = filePathVirt;
		this.fileNamePhysical = fileNamePhy;
		this.filePathPhysical = filePathPhy;
		this.size = size;
		this.created = created;
		this.modified = modified;
		this.tapeID = tapeID;
		this.crc32 = crc32;
		setIsDirectory();
	}

	public static RecordFile of(int id, String fileNameVirt, String filePathVirt, String fileNamePhy, String filePathPhy, int size, LocalDateTime created, LocalDateTime modified, int tapeID, int crc32) {
		return new RecordFile(id, fileNameVirt, filePathVirt, fileNamePhy, filePathPhy, size, created, modified, tapeID, crc32);
	}

	private void setIsDirectory() {
		isDirectory = fileNameVirtual.charAt(fileNameVirtual.length() - 1) == '/';
	}

	public int getID() { return id;	}
	public void setID(int id) { this.id = id; }
	public String getFileName() { return fileNameVirtual; }
	public String getFileNameCut() {
		if (isDirectory) {
			return fileNameVirtual.substring(1);
		}
		return fileNameVirtual;
	}
	public String getFileNameTrim() {
		if (isDirectory) {
			return fileNameVirtual.substring(0, fileNameVirtual.length() - 1).substring(1);
		}
		return fileNameVirtual;
	}
	public void setVirtualFileName(String fileName) { this.fileNameVirtual = fileName; }
	public String getVirtualFilePath() { return filePathVirtual; }
	public String getVirtualFileName() { return fileNameVirtual; }
	public void setVirtualFilePath(String filePath) { this.filePathVirtual = filePath; }
	public void setPhysicalFileName(String fileName) { this.fileNamePhysical = fileName; }
	public String getPhysicalFilePath() { return filePathPhysical; }
	public String getPhysicalFileName() { return fileNamePhysical; }
	public void setPhysicalFilePath(String filePath) { this.filePathPhysical = filePath; }
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
			return filePathVirtual + getFileNameCut();
		} else {
			return filePathVirtual + fileNameVirtual;
		}
	}
}
