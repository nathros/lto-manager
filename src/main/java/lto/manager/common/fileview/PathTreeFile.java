package lto.manager.common.fileview;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

import lto.manager.common.database.tables.records.RecordFile;
import lto.manager.common.log.Log;

public class PathTreeFile {
	private long fileSize;
	private LocalDateTime creationDateTime;
	private LocalDateTime modifiedDateTime;
	private boolean isDirectory;
	private Path path;

	public PathTreeFile(File f) { // Physical file
		path = Paths.get(f.getAbsolutePath());
		try {
			fileSize = Files.size(path);
		} catch (NoSuchFileException ne) {
			setSelfAsEmptyFile(f.getAbsolutePath());
			return;
		} catch (IOException e) {
			e.printStackTrace();
		}
		isDirectory = f.isDirectory();
		try {
			BasicFileAttributeView basicfile = Files.getFileAttributeView(path, BasicFileAttributeView.class, LinkOption.NOFOLLOW_LINKS);
	        BasicFileAttributes attr = basicfile.readAttributes();
	        long dateCreate = attr.creationTime().toMillis();
	        Instant instantCrate = Instant.ofEpochMilli(dateCreate);
	        creationDateTime = LocalDateTime.ofInstant(instantCrate, ZoneId.systemDefault());

	        long dateMod = attr.lastModifiedTime().toMillis();
	        Instant instantMod = Instant.ofEpochMilli(dateMod);
	        modifiedDateTime = LocalDateTime.ofInstant(instantMod, ZoneId.systemDefault());
		} catch (Exception e) {
			Log.l.severe("Failed to get file attributes for: " + f.getAbsolutePath());
			e.printStackTrace();
		}
	}

	public PathTreeFile(RecordFile f) {
		this.fileSize = f.getFileSize();
		this.creationDateTime = f.getCreatedDateTime();
		this.modifiedDateTime = f.getModifiedDateTime();
		this.isDirectory = f.isDirectory();
		this.path = Paths.get(f.getAbsolutePath());
	}

	private PathTreeFile() {}

	private void setSelfAsEmptyFile(final String filePath) {
		var now = LocalDateTime.now();
		this.fileSize = PathTreeBase.MISSING_FILE;
		this.creationDateTime = now;
		this.modifiedDateTime = now;
		this.isDirectory = false;
		this.path = Paths.get(filePath);
	}

	public static PathTreeFile EmptyFile(final String filePath) {
		var empty = new PathTreeFile();
		empty.setSelfAsEmptyFile(filePath);
		return empty;
	}

	public long getFileSize() { return fileSize; }
	public LocalDateTime getCreationDateTime() { return creationDateTime; }
	public LocalDateTime getModifiedDateTime() { return modifiedDateTime; }
	public String getAbsolutePath() { return path.toAbsolutePath().toString(); }
	public boolean isDirectory() { return isDirectory; }
	public String getParent() {
		var parent = path.getParent();
		if (parent == null) return "";
		return parent.toString();
	}
	public String getName() {
		var name = path.getFileName();
		if (name == null) return "";
		return name.toString();
	}
}
