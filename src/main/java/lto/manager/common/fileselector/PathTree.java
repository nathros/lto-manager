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
import java.util.ArrayList;
import java.util.List;

import lto.manager.common.log.Log;

public class PathTree {
	private List<PathTree> children;
	private File file;
	private Path path;
	private LocalDateTime creationDateTime;
	private LocalDateTime modifiedDateTime;

	public PathTree(String filePath) {
		file = new File(filePath);
		path = Paths.get(file.getAbsolutePath());

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
			for (File f: file.listFiles()) {
				children.add(new PathTree(f.getAbsolutePath()));
			}
		}
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

	@Override
    public String toString() {
        return file.getAbsolutePath();
    }
}
