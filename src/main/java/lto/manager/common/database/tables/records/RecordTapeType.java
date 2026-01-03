package lto.manager.common.database.tables.records;

public class RecordTapeType {
	private Integer id;
	private String type;
	private String designation;
	private String designationWORM;
	private long capacity;
	private String colour;
	private String colourHP;
	private String colourWORM;
	private String colourWORMHP;

	public RecordTapeType(Integer id, String type, String designation, String designationWORM, long size, String colour,
			String colourHP, String colourWORM, String colourWORMHP) {
		this.id = id;
		this.type = type;
		this.designation = designation;
		this.designationWORM = designationWORM;
		this.capacity = size;
		this.colour = colour;
		this.colourHP = colourHP;
		this.colourWORM = colourWORM;
		this.colourWORMHP = colourWORMHP;
	}

	public static RecordTapeType of(Integer id, String type, String designation, String designationWORM, long size,
			String colour, String colourHP, String colourWORM, String colourWORMHP) {
		return new RecordTapeType(id, type, designation, designationWORM, size, colour, colourHP, colourWORM,
				colourWORMHP);
	}

	public static RecordTapeType lazy(Integer id) {
		return new RecordTapeType(id, null, null, null, 0, "", "", "", "");
	}

	public Integer getID() {
		return id;
	}

	public void setID(int id) {
		this.id = id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getDesignation() {
		return designation;
	}

	public String getDesignationWORM() {
		return designationWORM;
	}

	public long getCapacity() {
		return capacity;
	}

	public String getColour() {
		return colour;
	}

	public String getColourHP() {
		return colourHP;
	}

	public String getColourWORM() {
		return colourWORM;
	}

	public String getColourWORMHP() {
		return colourWORMHP;
	}
}