package lto.manager.common.database.tables.records;

public class RecordTapeType {
	private int id;
	private String type;
	private String designation;
	private String designationWORM;

	public RecordTapeType(int id, String type, String designation, String designationWORM) {
		this.id = id;
		this.type = type;
		this.designation = designation;
		this.designationWORM = designationWORM;
	}

	public static RecordTapeType of(int id, String type, String designation, String designationWORM) {
		return new RecordTapeType(id, type, designation, designationWORM);
	}

	public int getID() { return id; }
	public void setID(int id) { this.id = id; }
	public String getType() { return type; }
	public void setType(String type) { this.type = type; }
	public String getDesignation() { return designation; }
	public String getDesignationWORM() { return designationWORM; }
	public long calcCapacityBytes() {
		String generation = type.replaceAll("\\D+", "");
		final long bytesPerGiB = 1000 * 1000 * 1000;
		long bytes = 0;
		switch (generation) {
		case "1": bytes = 100 * bytesPerGiB; break;
		case "2": bytes = 200 * bytesPerGiB; break;
		case "3": bytes = 400 * bytesPerGiB; break;
		case "4": bytes = 800 * bytesPerGiB; break;
		case "5": bytes = 1500 * bytesPerGiB; break;
		case "6": bytes = 2500 * bytesPerGiB; break;
		case "7": bytes = 6000 * bytesPerGiB; break;
		case "78": bytes = 9000 * bytesPerGiB; break;
		case "8": bytes = 1200 * bytesPerGiB; break;
		case "9": bytes = 1800 * bytesPerGiB; break;
		default:
			throw new IllegalArgumentException("Unexpected value: " + generation);
		}
		return bytes;
	}
}