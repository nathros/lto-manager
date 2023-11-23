package lto.manager.common.state;

import java.util.HashMap;
import java.util.UUID;

import lto.manager.common.database.tables.records.RecordUser;
import lto.manager.common.log.Log;

public class State {
	private static HashMap<UUID, RecordUser> loginSessions = new HashMap<UUID, RecordUser>();

	public static final HashMap<UUID, RecordUser> getAllSessions() { return loginSessions; }

	public static void addNewLoginSession(UUID id, RecordUser user) {
		Log.l.info("Added new login session: " + id.toString() + ", username: " + user.getUsername());
		loginSessions.put(id, user);
	}

	public static boolean removeLoginSession(UUID id) {
		var result = loginSessions.remove(id);
		if (result == null) {
			Log.l.warning("Failed to remove missing login session: " + id.toString());
			return false;
		} else {
			Log.l.info("Removed login session: " + id.toString() + ", username: " + result.getUsername());
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
}
