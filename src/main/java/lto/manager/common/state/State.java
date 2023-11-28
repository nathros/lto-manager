package lto.manager.common.state;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.UUID;

import lto.manager.common.database.tables.records.RecordUser;
import lto.manager.common.log.Log;
import lto.manager.common.security.LoginSession;

public class State {
	private static HashMap<UUID, LoginSession> loginSessions = new HashMap<UUID, LoginSession>();

	public static final HashMap<UUID, LoginSession> getAllSessions() { return loginSessions; }

	public static void addNewLoginSession(UUID id, RecordUser user) {
		Log.l.info("Added new login session: " + id.toString() + ", username: " + user.getUsername());
		loginSessions.put(id, new LoginSession(id, user, LocalDateTime.now().plusDays(31)));
	}

	public static boolean removeLoginSession(UUID id) {
		var result = loginSessions.remove(id);
		if (result == null) {
			Log.l.warning("Failed to remove missing login session: " + id.toString());
			return false;
		} else {
			Log.l.info("Removed login session: " + id.toString() + ", username: " + result.user().getUsername());
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
