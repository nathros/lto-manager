package lto.manager.common.database.tables.records;

public class RecordManufacturer {
	private Integer id;
	private String manufacturer;

	public RecordManufacturer(Integer id, String manufacturer) {
		this.id = id;
		this.manufacturer = manufacturer;
	}

	public static RecordManufacturer of(Integer id, String manufacturer) {
		return new RecordManufacturer(id, manufacturer);
	}

	public Integer getID() { return id; }
	public void setID(int id) { this.id = id; }
	public String getManufacturer() { return manufacturer; }
	public void setManufacturer(String manufacturer) { this.manufacturer = manufacturer; }
}