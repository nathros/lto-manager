package lto.manager.common.database.tables.records;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

import lto.manager.common.database.Database;
import lto.manager.common.database.tables.TableTape;

public class RecordFile {
	private int id;
	private String fileNameVirtual;
	private String filePathVirtual;
	private String fileNamePhysical;
	private String filePathPhysical;
	private long size;
	private LocalDateTime created;
	private LocalDateTime modified;
	private int tapeID;
	private int crc32;
	private String customIcon;
	private boolean isDirectory;

	public RecordFile(int id, String fileNameVirt, String filePathVirt, String fileNamePhy, String filePathPhy, long size, LocalDateTime created, LocalDateTime modified, int tapeID, int crc32, String customIcon) {
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
		this.customIcon = customIcon;
		setIsDirectory();
	}

	public static RecordFile of(int id, String fileNameVirt, String filePathVirt, String fileNamePhy, String filePathPhy, long size, LocalDateTime created, LocalDateTime modified, int tapeID, int crc32, String customIcon) {
		return new RecordFile(id, fileNameVirt, filePathVirt, fileNamePhy, filePathPhy, size, created, modified, tapeID, crc32, customIcon);
	}

	public static RecordFile of(String fileNameVirt, String filePathVirt, String fileNamePhy, String filePathPhy, long size, LocalDateTime created, LocalDateTime modified, int tapeID, int crc32, String customIcon) {
		return new RecordFile(Database.NEW_RECORD_ID, fileNameVirt, filePathVirt, fileNamePhy, filePathPhy, size, created, modified, tapeID, crc32, customIcon);
	}

	public static RecordFile newDirectory(String fileNameVirt, String filePathVirt, String fileNamePhy, String filePathPhy) {
		final LocalDateTime now = LocalDateTime.now();
		return new RecordFile(Database.NEW_RECORD_ID, fileNameVirt, filePathVirt, fileNamePhy, filePathPhy, 0, now, now, TableTape.DIR_TAPE_ID, 0, null);
	}

	public static RecordFile of(String tarIndexRecord) {
		String[] elements = tarIndexRecord.split("\0");
		if (elements.length != 7) {
			throw new IllegalArgumentException(
					"Index record string has incorrect length != 7, is " + tarIndexRecord.length());
		}
		String filePath = elements[1];
		String fileName = elements[2];
		long fileSize = Long.parseLong(elements[3]);
		long time = Long.parseLong(elements[4]);
		Instant instant = Instant.ofEpochMilli(time);
		ZoneId zoneId = ZoneId.systemDefault();
		LocalDateTime created = instant.atZone(zoneId).toLocalDateTime();
		time = Long.parseLong(elements[5]);
		instant = Instant.ofEpochMilli(time);
		LocalDateTime modified = instant.atZone(zoneId).toLocalDateTime();
		int crc32 = Integer.parseInt(elements[6]);
		return new RecordFile(Database.NEW_RECORD_ID, "", "", fileName, filePath, fileSize, created, modified, Database.NEW_RECORD_ID, crc32, "");
	}

	private void setIsDirectory() {
		isDirectory = fileNameVirtual.endsWith("/");
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
			return fileNameVirtual.substring(0, fileNameVirtual.length() - 1);
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
	public long getFileSize() { return size;	}
	public void setFileSize(long size) { this.size = size; }
	public LocalDateTime getCreatedDateTime() { return created;	}
	public void setCreatedDateTime(LocalDateTime created) { this.created = created; }
	public LocalDateTime getModifiedDateTime() { return modified;	}
	public void setModifiedDateTime(LocalDateTime modified) { this.modified = modified; }
	public int getTapeID() { return tapeID;	}
	public void setTapeID(int tapeID) { this.tapeID = tapeID; }
	public int getCRC32() { return crc32; }
	public void setCRC32(int crc32) { this.crc32 = crc32; }
	public String getCRC32StrHex() { return Integer.toHexString(crc32); }
	public String getCustomIcon() { return customIcon; }
	public void setCustomIcon(String newIcon) { customIcon = newIcon; }
	public boolean isDirectory() { return isDirectory; }
	public String getAbsolutePath() {
		if (isDirectory) {
			return filePathVirtual + getFileNameCut();
		} else {
			return filePathVirtual + fileNameVirtual;
		}
	}
	@Override
	public String toString() {
		return filePathVirtual + fileNameVirtual;
	}

	public String toIndexRecordString(int fileMarkerIndex) {
		String result = "";
		final String SEPERATOR = "\0";
		result.concat(filePathPhysical).concat(SEPERATOR);
		result.concat(fileNamePhysical).concat(SEPERATOR);

		ZoneId zoneId = ZoneId.systemDefault();
		long epoch = created.atZone(zoneId).toEpochSecond();
		String tmp = String.valueOf(epoch);
		result.concat(tmp).concat(SEPERATOR);

		epoch = modified.atZone(zoneId).toEpochSecond();
		tmp = String.valueOf(epoch);
		result.concat(tmp).concat(SEPERATOR);

		tmp = String.valueOf(crc32);
		result.concat(tmp).concat(SEPERATOR);
		return result;
	}
}
