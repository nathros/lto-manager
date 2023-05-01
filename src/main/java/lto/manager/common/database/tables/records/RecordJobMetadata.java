package lto.manager.common.database.tables.records;

import lto.manager.common.database.Database;

public class RecordJobMetadata {
	private int id;
	private int jobID;
	private String key;
	private String value;

	public RecordJobMetadata(int id, int JobID, String key, String value) {
		this.id = id;
		this.jobID = id;
		this.key = key;
		this.value = value;
	}

	public RecordJobMetadata(String key, String value) {
		this(Database.NEW_RECORD_ID, Database.NEW_RECORD_ID, key, value);
	}

	public static RecordJobMetadata of(int id, int jobID, String key, String value) {
		return new RecordJobMetadata(id, jobID, key, value);
	}

	public static RecordJobMetadata of(int jobID, String key, String value) {
		return new RecordJobMetadata(Database.NEW_RECORD_ID, jobID, key, value);
	}

	public int getID() { return id; }
	public int getJobID() { return jobID; }
	public void setJobID(int id) { jobID = id; }
	public String getKey() { return key; }
	public void setKey(String k) { key = k; }
	public String getValue() { return value; }
	public void setValue(String v) { value = v; }
}
