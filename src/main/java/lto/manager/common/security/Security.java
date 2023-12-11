package lto.manager.common.security;

import java.sql.SQLException;
import java.util.UUID;

import lto.manager.common.database.Database;
import lto.manager.common.database.tables.records.RecordUser;
import lto.manager.common.log.Log;
import lto.manager.common.state.State;

public class Security {
	public static UUID loginUser(final String username, final String password) {
		try {
			final RecordUser user = Database.getUserByName(username);
			final String calculatedHash = user.getHashedPassword(password);
			final String hash = user.getHash();
			if (calculatedHash.equals(hash)) {
				final UUID newID = UUID.randomUUID();
				State.addNewLoginSession(newID, user);
				return newID;
			}
		} catch (SQLException e) {
			Log.severe("Failed to login user [" + username + "] " + e.getMessage());
		}
		return null;
	}
}
