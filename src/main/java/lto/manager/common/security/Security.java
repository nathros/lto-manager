package lto.manager.common.security;

import java.io.IOException;
import java.sql.SQLException;
import java.util.UUID;
import java.util.concurrent.CompletionException;

import lto.manager.common.database.Database;
import lto.manager.common.database.tables.records.RecordUser;
import lto.manager.common.log.Log;
import lto.manager.common.state.State;

public class Security {
	public static UUID loginUser(final String username, final String password) throws Exception {
		try {
			final RecordUser user = Database.getUserByName(username, false);
			final String calculatedHash = user.getHashedPassword(password);
			final String hash = user.getHash();
			if (calculatedHash.equals(hash)) {
				if (!user.getEnabled()) {
					throw new CompletionException("User is disabled", null);
				}
				final UUID newID = UUID.randomUUID();
				State.addNewLoginSession(newID, user);
				return newID;
			}
			throw new IllegalAccessError("User not authorised");
		} catch (SQLException | IOException e) {
			Log.severe("Failed to login user [" + username + "] " + e.getMessage());
			throw e;
		}
	}
}
