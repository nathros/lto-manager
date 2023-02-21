package lto.manager.common.database.tables.records;

import java.time.LocalDateTime;

import lto.manager.common.database.tables.TableTape;

public class RecordTape {
	private int id;
	private RecordManufacturer manufacturer;
	private RecordTapeType type;
	private String barcode;
	private String serial;
	private long totalSpace;
	private long usedSpace;
	private LocalDateTime dateAdded;
	public static final int CALC_SIZE = -1;

	public RecordTape(int id, RecordManufacturer manufacturer, RecordTapeType type, String barcode,
			String serial, long totalSpace, long usedSpace, LocalDateTime dateAdded) {
		this.id = id;
		this.manufacturer = manufacturer;
		this.type = type;
		this.barcode = barcode;
		this.serial = serial;
		this.totalSpace = totalSpace;
		this.usedSpace = usedSpace;
		this.dateAdded = dateAdded;
	}

	public static RecordTape of(Integer id, RecordManufacturer manufacturer, RecordTapeType type, String barcode,
			String serial, long usedSpace, LocalDateTime dateAdded) {
		if (id == null) id = TableTape.NO_ID;
		return new RecordTape(id, manufacturer, type, barcode, serial, type.calcCapacityBytes(), usedSpace, dateAdded);
	}

	public static RecordTape of(Integer id, RecordManufacturer manufacturer, RecordTapeType type, String barcode,
			String serial, long totalSpace, long usedSpace, LocalDateTime dateAdded) {
		if (id == null) id = TableTape.NO_ID;
		return new RecordTape(id, manufacturer, type, barcode, serial, totalSpace, usedSpace, dateAdded);
	}

	public int getID() { return id; }
	public void setID(int id) { this.id = id; }
	public RecordManufacturer getManufacturer() { return manufacturer; }
	public void setManufacturer(RecordManufacturer manufacturer) { this.manufacturer = manufacturer; }
	public RecordTapeType getTapeType() { return type; }
	public void setTapeType(RecordTapeType type) { this.type = type; }
	public String getBarcode() { return barcode; }
	public void setBarcode(String barcode) { this.barcode = barcode; }
	public String getSerial() { return serial; }
	public void setTapeType(String serial) { this.serial = serial; }
	public long getTotalSpace() { return totalSpace; }
	public float getTotalSpaceGB() { return (float) totalSpace / (1024 * 1024 * 1024); }
	public void setTotalSpace(int totalSpace) { this.totalSpace = totalSpace; }
	public long getUsedSpace() { return usedSpace; }
	public float getUsedSpaceGB() { return (float) usedSpace / (1024 * 1024 * 1024); }
	public void setUsedSpace(int remainingSpace) { this.usedSpace = remainingSpace; }
	public LocalDateTime getDateAdded() { return dateAdded; }
	public void setDateAdded(LocalDateTime dateAdded) { this.dateAdded = dateAdded; }
}