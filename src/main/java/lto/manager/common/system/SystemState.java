package lto.manager.common.system;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import lto.manager.common.database.Database;
import lto.manager.common.database.tables.records.RecordNotification;
import lto.manager.common.database.tables.records.RecordNotification.RecordNotificationType;
import lto.manager.common.log.Log;
import lto.manager.common.ltfs.CheckLTFSInstalled;

public class SystemState {
	public static void checkSystem() {
		boolean found = false;
		CheckLTFSInstalled check = new CheckLTFSInstalled();
		try {
			found = check.startBlocking(UUID.randomUUID().toString()).tryAcquire(1, TimeUnit.SECONDS);
			if (found == false || check.getVersion() == null) {
				Database.addSingletonNotification(RecordNotification.newMissingLTFS());
			} else {
				Database.removeNotificationByType(RecordNotificationType.LTFS_MISSING);
			}
		} catch (Exception e) {
			Log.severe(e.getMessage());
			e.printStackTrace();
		}
	}
}
