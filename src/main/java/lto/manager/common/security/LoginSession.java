package lto.manager.common.security;

import java.time.LocalDateTime;
import java.util.UUID;

import lto.manager.common.database.tables.records.RecordUser;

public record LoginSession(UUID uuid, RecordUser user, LocalDateTime created, LocalDateTime expiry) {

	public LoginSession(UUID uuid, RecordUser user, LocalDateTime expiry) {
		this(uuid, user, LocalDateTime.now(), expiry);
	}
}
