package lto.manager.common.state;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import lto.manager.common.ExternalProcess;
import lto.manager.common.database.Database;
import lto.manager.common.database.Options;
import lto.manager.common.database.tables.records.RecordOptions.OptionsSetting;
import lto.manager.common.database.tables.records.RecordUser;
import lto.manager.common.log.Log;
import lto.manager.common.security.LoginSession;

public class State {
	private static HashMap<UUID, LoginSession> loginSessions = new HashMap<UUID, LoginSession>();
	private static Timer backgroundCleanUp = null;

	public static final HashMap<UUID, LoginSession> getAllSessions() {
		return loginSessions;
	}

	public static void addNewLoginSession(UUID id, RecordUser user) {
		Log.info("Added new login session: " + id.toString() + ", username: " + user.getUsername());
		loginSessions.put(id, new LoginSession(id, user, LocalDateTime.now().plusDays(31)));
	}

	public static boolean removeLoginSession(UUID id) {
		var result = loginSessions.remove(id);
		if (result == null) {
			Log.warning("Failed to remove missing login session: " + id.toString());
			return false;
		} else {
			Log.info("Removed login session: " + id.toString() + ", username: " + result.user().getUsername());
			return true;
		}
	}

	public static boolean isLoginSessionValid(String id) {
		try {
			UUID uuid = UUID.fromString(id);
			final boolean valid = loginSessions.containsKey(uuid);
			return valid;
		} catch (IllegalArgumentException e) {
			return false;
		}
	}

	public static void startBackgroundCleanup() {
		if (backgroundCleanUp == null) {
			final Integer repeatEveryMinutes = Options.getData(OptionsSetting.BACKGROUND_CLEANUP_TIMER);
			Log.info("Background cleanup scheduled to run every " + repeatEveryMinutes + " minutes");

			backgroundCleanUp = new Timer();
			final long schedule = repeatEveryMinutes * 1000 * 60;
			backgroundCleanUp.scheduleAtFixedRate(new TimerTask() {
				@Override
				public void run() {
					final Integer repeatEveryMinutesCurrent = Options.getData(OptionsSetting.BACKGROUND_CLEANUP_TIMER);
					if (repeatEveryMinutesCurrent != repeatEveryMinutes) { // Setting has changed so restart with new value
						stopBackgroundCleanup();
						startBackgroundCleanup();
					} else {
						Log.fine("Started system cleanup");
						try {
							ExternalProcess.removeRetired(Integer.parseInt(Database.getOption(OptionsSetting.STALE_EXTERNAL_PROCESS_TIME)));
						} catch (Exception e) {
							Log.severe("Failed to start startRemoveRetired " + e.getMessage());
						}
						Log.fine("completed system cleanup");
					}
				}
			}, schedule, schedule);
		}
	}

	public static void stopBackgroundCleanup() {
		backgroundCleanUp.cancel();
		backgroundCleanUp = null;
	}
}
