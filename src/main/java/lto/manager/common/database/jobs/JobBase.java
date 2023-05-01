package lto.manager.common.database.jobs;

import java.util.ArrayList;
import java.util.List;

import lto.manager.common.database.tables.records.RecordJob;
import lto.manager.common.database.tables.records.RecordJobMetadata;

public abstract class JobBase {
	protected RecordJob job;
	protected List<RecordJobMetadata> metadata = new ArrayList<RecordJobMetadata>();

	public RecordJob getRecordJob() { return job; }
	public List<RecordJobMetadata> getRecordJobMetadata() { return metadata; }
}
