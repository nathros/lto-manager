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
	private int destTapeID;
	public static final String KEY_DEST = "BACKUP_DESTINATION";
	public static final String KEY_DEST_ID = "BACKUP_DESTINATION_TAPE_ID";
	public static final String KEY_SOURCE_ROOT = "BACKUP_SOURCE_ROOT";
	public static final String KEY_SOURCE = "BACKUP_SOURCE";

	public BackupJob(RecordJob job, List<RecordJobMetadata> metadata) throws Exception {
		this.job = job;
		this.sourcePhysicalFiles = new ArrayList<String>();
		boolean foundDest = false;
		boolean foundDestID = false;
		boolean foundSourceRootDir = false;
		boolean foundKeySource = false;
		this.metadata = metadata;
		for (var meta: metadata) {
			final String key = meta.getKey();
			final String value = meta.getValue();

			switch (key) {
			case KEY_SOURCE: {
				sourcePhysicalFiles.add(meta.getValue());
				foundKeySource = true;
				break;
			}

			case KEY_DEST: {
				if (!foundDest) {
					foundDest = true;
					desinationVirtualDir = value;
				} else {
					Log.warning("Backup job found multiple destinations (" + KEY_DEST + "), ignoring new: " + value);
				}
				break;
			}

			case KEY_DEST_ID: {
				if (!foundDestID) {
					this.destTapeID = Integer.parseInt(value);
					foundDestID = true;
				} else {
					Log.warning("Backup job found multiple destinations (" + KEY_DEST_ID + "), ignoring new: " + value);
				}
				break;
			}

			case KEY_SOURCE_ROOT: {
				if (!foundSourceRootDir) {
					this.sourceRootDir = value;
					foundSourceRootDir = true;
				} else {
					Log.warning("Backup job found multiple destinations (" + KEY_SOURCE_ROOT + "), ignoring new: " + value);
				}
				break;
			}

			default: Log.warning("Backup job found unknown metadata key: " + key + " value: " + value);
			}
		}

		if (!foundDest) {
			throw new Exception("Missing: " + KEY_DEST + " from backup job");
		}
		if (!foundDestID) {
			throw new Exception("Missing: " + KEY_DEST_ID + " from backup job");
		}
		if (!foundSourceRootDir) {
			throw new Exception("Missing: " + KEY_SOURCE_ROOT + " from backup job");
		}
		if (!foundKeySource) {
			throw new Exception("Missing: " + KEY_SOURCE + " from backup job");
		}
	}

	public BackupJob(RecordJob job, List<String> sourceFiles, String desinationDir, String sourceRootDir, int tapeID) {
		this.job = job;
		this.sourcePhysicalFiles = sourceFiles;
		this.sourceRootDir = sourceRootDir;
		this.destTapeID = tapeID;
		this.desinationVirtualDir = desinationDir;
		metadata.add(RecordJobMetadata.of(0, KEY_DEST, desinationVirtualDir));
		metadata.add(RecordJobMetadata.of(0, KEY_DEST_ID, String.valueOf(destTapeID)));
		metadata.add(RecordJobMetadata.of(0, KEY_SOURCE_ROOT, this.sourceRootDir));
		for (var file: sourceFiles) {
			metadata.add(RecordJobMetadata.of(0, KEY_SOURCE, file));
		}
	}

	public String getDesinationPath() { return desinationVirtualDir; }
	public int getDesinationTapeID() { return destTapeID; }
	public String getSourceRootPath() { return sourceRootDir; }
	public List<String> getSourceFiles() { return sourcePhysicalFiles; }
}
