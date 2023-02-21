package lto.manager.common.fileview;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public abstract class PathTreeBase {
	protected List<PathTreeBase> children;
	protected PathTreeFile file;
	protected int depth;
	protected static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

	public long getFileSizeBytes() {
		return file.getFileSize();
	}

	public LocalDateTime getCreationDateTime() {
        return file.getCreationDateTime();
	}

	public LocalDateTime getModifiedDateTime() {
        return file.getModifiedDateTime();
	}

	public List<PathTreeBase> getChildren() {
		return children;
	}

	public int getDepth() {
		return depth;
	}

	public String getFileSizeHR() {
		long size = file.getFileSize();
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
		return file.getModifiedDateTime().format(formatter);
	}

	public long getModifiedDateTimeLong() {
		ZonedDateTime zdt = ZonedDateTime.of(file.getModifiedDateTime(), ZoneId.systemDefault());
        return zdt.toInstant().toEpochMilli();
	}

	public String getAbsolutePath() { return file.getAbsolutePath(); }
	public String getParent() { return file.getParent(); }
	public boolean isDirectory() { return file.isDirectory(); }
	public String getName() { return file.getName(); }

	@Override
    public String toString() {
        return file.getAbsolutePath();
    }
}
