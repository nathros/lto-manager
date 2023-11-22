package lto.manager.common.state;

import java.util.HashMap;
import java.util.UUID;

import lto.manager.common.database.tables.records.RecordUser;

public class State {
	private static HashMap<UUID, RecordUser> loginSessions = new HashMap<UUID, RecordUser>();


}
