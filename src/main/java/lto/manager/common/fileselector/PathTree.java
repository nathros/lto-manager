package lto.manager.common.fileselector;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import lto.manager.common.log.Log;

public class PathTree {
	private List<PathTree> children;
	private File file;
	private Path path;
	private LocalDateTime creationDateTime;
	private LocalDateTime modifiedDateTime;
	private int depth;
	private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

	public PathTree(String filePath, int depth, int maxDepth) {
		file = new File(filePath);
		path = Paths.get(file.getAbsolutePath());
		this.depth = depth;

		try {
			BasicFileAttributeView basicfile = Files.getFileAttributeView(path, BasicFileAttributeView.class, LinkOption.NOFOLLOW_LINKS);
	        BasicFileAttributes attr = basicfile.readAttributes();
	        long dateCreate = attr.creationTime().toMillis();
	        Instant instantCrate = Instant.ofEpochMilli(dateCreate);
	        creationDateTime = LocalDateTime.ofInstant(instantCrate, ZoneId.systemDefault());

	        long dateMod = attr.lastModifiedTime().toMillis();
	        Instant instantMod = Instant.ofEpochMilli(dateMod);
	        modifiedDateTime = LocalDateTime.ofInstant(instantMod, ZoneId.systemDefault());
		} catch (IOException e) {
			Log.l.severe("Failed to get file attributes for: " + filePath);
			e.printStackTrace();
		}

		if (file.isDirectory()) {
			children = new ArrayList<PathTree>();
			List<PathTree> dir = new ArrayList<PathTree>();
			List<PathTree> singleFile = new ArrayList<PathTree>();
			if ((depth + 1) <= maxDepth) {
				for (File f: file.listFiles()) { // Separate files and directories
					if (f.isDirectory()) {
						dir.add(new PathTree(f.getAbsolutePath(), depth + 1, maxDepth));
					} else {
						singleFile.add(new PathTree(f.getAbsolutePath(), depth + 1, maxDepth));
					}
				}
			}
			children.addAll(dir); // Make sure directories are always first
			children.addAll(singleFile);
		}
	}

	public PathTree(String filePath) {
		this(filePath, 0, 1);
	}

	public File getFile() {
		return file;
	}

	public long getFileSizeBytes() {
		try {
			long size = Files.size(path);
			return size;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return -1;
	}

	public LocalDateTime getCreationDateTime() {
        return creationDateTime;
	}

	public LocalDateTime getModifiedDateTime() {
        return modifiedDateTime;
	}

	public List<PathTree> getChildren() {
		return children;
	}

	public int getDepth() {
		return depth;
	}

	public String getFileSizeHR() {
		long size = file.length();
		double dSize = size;
		int count = 0;
		while (dSize > 1024) {
			dSize /= 1024;
			count++;
		}
		switch (count) {
		case 0: return String.format("%.0f bytes", dSize);
		case 1: return String.format("%.2f KB", dSize);
		case 2: return String.format("%.2f MB", dSize);
		case 3: return String.format("%.2f GB", dSize);
		default: return String.format("%.2f TB", dSize);
		}
	}

	public String getModifiedDateTimeStr() {
		return modifiedDateTime.format(formatter);
	}

	public long getModifiedDateTimeLong() {
		ZonedDateTime zdt = ZonedDateTime.of(modifiedDateTime, ZoneId.systemDefault());
        return zdt.toInstant().toEpochMilli();
	}

	@Override
    public String toString() {
        return file.getAbsolutePath();
    }
}
