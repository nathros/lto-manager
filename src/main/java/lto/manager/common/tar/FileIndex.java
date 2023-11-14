package lto.manager.common.tar;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

public record FileIndex(int fileMarkerIndex, String filePath, String fileName, long fileSize, LocalDateTime created,
		LocalDateTime modified, int crc32) {

	public static FileIndex of(String IndexRecord) {
		String[] elements = IndexRecord.split("\0");
		if (elements.length != 7) {
			throw new IllegalArgumentException(
					"Index record string has incorrect length != 7, is " + IndexRecord.length());
		}
		int fileMarkerIndex = Integer.parseInt(elements[0]);

		String filePath = elements[1];
		String fileName = elements[2];
		long fileSize = Long.parseLong(elements[3]);

		long time = Long.parseLong(elements[4]);
		Instant instant = Instant.ofEpochMilli(time);
		ZoneId zoneId = ZoneId.systemDefault();
		LocalDateTime created = instant.atZone(zoneId).toLocalDateTime();

		time = Long.parseLong(elements[5]);
		instant = Instant.ofEpochMilli(time);
		LocalDateTime modified = instant.atZone(zoneId).toLocalDateTime();

		int crc32 = Integer.parseInt(elements[6]);

		return new FileIndex(fileMarkerIndex, filePath, fileName, fileSize, created, modified, crc32);
	}

	public String toIndexRecordString() {
		String result = "";
		final String SEPERATOR = "\0";
		String tmp = String.valueOf(fileMarkerIndex);
		result.concat(tmp).concat(SEPERATOR);
		result.concat(filePath).concat(SEPERATOR);
		result.concat(fileName).concat(SEPERATOR);

		ZoneId zoneId = ZoneId.systemDefault();
		long epoch = created.atZone(zoneId).toEpochSecond();
		tmp = String.valueOf(epoch);
		result.concat(tmp).concat(SEPERATOR);

		epoch = modified.atZone(zoneId).toEpochSecond();
		tmp = String.valueOf(epoch);
		result.concat(tmp).concat(SEPERATOR);

		tmp = String.valueOf(crc32);
		result.concat(tmp).concat(SEPERATOR);
		return result;
	}

}
