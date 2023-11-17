package lto.manager.common.database.tables.records;

import java.time.LocalDateTime;

import lto.manager.common.database.tables.TableTape;

public class RecordTape {
	private int id;
	private RecordManufacturer manufacturer;
	private RecordTapeType type;
	private String barcode;
	private String serial;
	private RecordTapeFormatType format;
	private long usedSpace;
	private LocalDateTime dateAdded;
	public static final int CALC_SIZE = -1;
	public static final int EMPTY = -1;

	public RecordTape(int id, RecordManufacturer manufacturer, RecordTapeType type, String barcode,
			String serial, long usedSpace, RecordTapeFormatType format, LocalDateTime dateAdded) {
		this.id = id;
		this.manufacturer = manufacturer;
		this.type = type;
		this.barcode = barcode;
		this.serial = serial;
		this.usedSpace = usedSpace;
		this.format = format;
		if (dateAdded == null) this.dateAdded = LocalDateTime.now();
		else this.dateAdded = dateAdded;
	}

	public static RecordTape of(Integer id, RecordManufacturer manufacturer, RecordTapeType type, String barcode,
			String serial, long usedSpace, RecordTapeFormatType format, LocalDateTime dateAdded) {
		if (id == null) id = TableTape.NO_ID;
		return new RecordTape(id, manufacturer, type, barcode, serial, usedSpace, format, dateAdded);
	}

	public static RecordTape of(Integer id, RecordManufacturer manufacturer, RecordTapeType type, String barcode,
			String serial, long totalSpace, long usedSpace, RecordTapeFormatType format, LocalDateTime dateAdded) {
		if (id == null) id = TableTape.NO_ID;
		return new RecordTape(id, manufacturer, type, barcode, serial, usedSpace, format, dateAdded);
	}

	public static RecordTape getBlank() {
		RecordManufacturer blankManufacturer = RecordManufacturer.of(null, "");
		RecordTapeType blankType = RecordTapeType.of(null, "", "", "", 0);
		return new RecordTape(TableTape.DIR_TAPE_ID, blankManufacturer, blankType, "BLANK", "BLANK", 0, RecordTapeFormatType.values()[0], null);
	}

	public enum RecordTapeFormatType {
		LTFS, TAR;

		public static RecordTapeFormatType fromInteger(int index) {
			if (index > RecordTapeFormatType.values().length) return null;
			else return RecordTapeFormatType.values()[index];
	    }
	}

	public int getID() { return id; }
	public void setID(int id) { this.id = id; }
	public RecordManufacturer getManufacturer() { return manufacturer; }
	public void setManufacturer(RecordManufacturer manufacturer) { this.manufacturer = manufacturer; }
	public RecordTapeFormatType getFormat() { return format; }
	public void setFormat(RecordTapeFormatType format) { this.format = format; }
	public RecordTapeType getTapeType() { return type; }
	public void setTapeType(RecordTapeType type) { this.type = type; }
	public String getBarcode() { return barcode; }
	public void setBarcode(String barcode) { this.barcode = barcode; }
	public String getSerial() { return serial; }
	public void setTapeType(String serial) { this.serial = serial; }
	public long getTotalSpace() { return type.getCapacity(); }
	public float getTotalSpaceGB() { return (float) type.getCapacity() / (1024 * 1024 * 1024); }
	public long getUsedSpace() { return usedSpace; }
	public float getUsedSpaceGB() { return (float) usedSpace / (1024 * 1024 * 1024); }
	public void setUsedSpace(int remainingSpace) { this.usedSpace = remainingSpace; }
	public LocalDateTime getDateAdded() { return dateAdded; }
	public void setDateAdded(LocalDateTime dateAdded) { this.dateAdded = dateAdded; }
}