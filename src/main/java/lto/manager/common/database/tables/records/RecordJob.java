package lto.manager.common.database.tables.records;

import java.time.LocalDateTime;

public class RecordJob {
	public enum RecordJobType {
		BACKUP("Backup"), RESTORE("Restore"), DELETE("Delete");

		private final String val;

	    private RecordJobType(String val) {
	        this.val = val;
	    }

	    @Override
	    public String toString() {
	        return val;
	    }
	}

	public enum RecordJobStatus {
		UNDEFINED, COMPLETED, IN_PROGRESS, WAITING, STOPPED_ON_ERROR
	}

	private int id;
	private String name;
	private RecordJobType type;
	private RecordJobStatus status;
	private LocalDateTime start;
	private LocalDateTime end;
	private String comment;

	public RecordJob(int id, String name, RecordJobType type, RecordJobStatus status, LocalDateTime start, LocalDateTime end, String comment)
	{
		this.id = id;
		this.name = name;
		this.type = type;
		this.status = status;
		this.start = start;
		this.end = end;
		this.comment = comment;
	}

	public static RecordJob of(int id, String name, RecordJobType type, RecordJobStatus status, LocalDateTime start, LocalDateTime end, String comment) {
		return new RecordJob(id, name, type, status, start, end, comment);
	}

	public int getID() { return id; }
	public void setID(int id) { this.id = id; }
	public String getName() { return name; }
	public void setName(String name) { this.name = name; }
	public RecordJobType getType() { return type; }
	public RecordJobStatus getStatus() { return status; }
	public LocalDateTime getStartDateTime() { return start; }
	public LocalDateTime getEndDateTime() { return end; }
	public String getComment() { return comment; }
}