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
}