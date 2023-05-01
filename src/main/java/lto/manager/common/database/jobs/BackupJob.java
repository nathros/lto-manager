package lto.manager.common.database.jobs;

import java.util.ArrayList;
import java.util.List;

import lto.manager.common.database.tables.records.RecordJob;
import lto.manager.common.database.tables.records.RecordJobMetadata;
import lto.manager.common.log.Log;

public class BackupJob extends JobBase {
	private String desinationVirtualDir;
	private String sourceRootDir;
	private List<String> sourcePhysicalFiles;
	public static final String KEY_DEST = "BACKUP_DESTINATION";
	public static final String KEY_SOURCE_ROOT = "BACKUP_SOURCE_ROOT";
	public static final String KEY_SOURCE = "BACKUP_SOURCE";

	public BackupJob(RecordJob job, List<RecordJobMetadata> metadata) {
		this.job = job;
		this.sourcePhysicalFiles = new ArrayList<String>();
		boolean foundDest = false;
		for (var meta: metadata) {
			if (meta.getKey().equals(KEY_SOURCE)) {
				sourcePhysicalFiles.add(meta.getKey());
			} else if (meta.getKey().equals(KEY_DEST)) {
				if (!foundDest) {
					foundDest = false;
					desinationVirtualDir = meta.getValue();
				} else {
					Log.l.warning("Backup job found multiple destinations (" + desinationVirtualDir + "), ignoring new: " + meta.getValue());
				}
			} else {
				Log.l.warning("Backup job found unknown metadata key: " + meta.getKey() + " value: " + meta.getValue());
			}
		}
	}

	public BackupJob(RecordJob job, List<String> sourceFiles, String desinationDir, String sourceRootDir) {
		this.job = job;
		this.sourcePhysicalFiles = sourceFiles;
		this.sourceRootDir = sourceRootDir;
		metadata.add(RecordJobMetadata.of(0, KEY_DEST, desinationDir));
		metadata.add(RecordJobMetadata.of(0, KEY_SOURCE_ROOT, sourceRootDir));
		for (var file: sourceFiles) {
			metadata.add(RecordJobMetadata.of(0, KEY_SOURCE, file));
		}
		this.desinationVirtualDir = desinationDir;
	}

	public String getDesinationPath() { return desinationVirtualDir; }
	public String getSourceRootPath() { return sourceRootDir; }
	public List<String> getSourceFiles() { return sourcePhysicalFiles; }
}
