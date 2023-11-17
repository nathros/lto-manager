package lto.manager.common.database.tables.records;

public class RecordTapeType {
	private Integer id;
	private String type;
	private String designation;
	private String designationWORM;
	private long capacity;

	public RecordTapeType(Integer id, String type, String designation, String designationWORM, long size) {
		this.id = id;
		this.type = type;
		this.designation = designation;
		this.designationWORM = designationWORM;
		this.capacity = size;
	}

	public static RecordTapeType of(Integer id, String type, String designation, String designationWORM, long size) {
		return new RecordTapeType(id, type, designation, designationWORM, size);
	}

	public Integer getID() { return id; }
	public void setID(int id) { this.id = id; }
	public String getType() { return type; }
	public void setType(String type) { this.type = type; }
	public String getDesignation() { return designation; }
	public String getDesignationWORM() { return designationWORM; }
	public long getCapacity() { return capacity; }
}