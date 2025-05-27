package lto.manager.common.database.tables.records;

import java.time.LocalDateTime;

import lto.manager.common.database.Database;

public class RecordNotification {
	public enum RecordNotificationLevel {
		ERROR("Error"), WARNING("Warning"), INFO("info");

		private final String val;

		private RecordNotificationLevel(String val) {
			this.val = val;
		}

		@Override
		public String toString() {
			return val;
		}
	}

	public enum RecordNotificationSource {
		SYSTEM("System"), UPDATER("Updater");

		private final String val;

		private RecordNotificationSource(String val) {
			this.val = val;
		}

		@Override
		public String toString() {
			return val;
		}
	}

	public enum RecordNotificationType {
		GENERAL, FAILURE, LTFS_MISSING
	}

	private int id;
	private RecordUser user;
	private LocalDateTime created;
	private RecordNotificationLevel level;
	private RecordNotificationSource source;
	private RecordNotificationType type;
	private String label;
	private String message;
	private boolean cleared;

	public RecordNotification(int id, RecordUser user, LocalDateTime created, RecordNotificationLevel level,
			RecordNotificationSource source, RecordNotificationType type, String label, String message,
			boolean cleared) {
		this.id = id;
		this.user = user;
		this.created = created;
		this.level = level;
		this.source = source;
		this.type = type;
		this.label = label;
		this.message = message;
		this.cleared = cleared;
	}

	public static RecordNotification of(int id, RecordUser user, LocalDateTime created, RecordNotificationLevel level,
			RecordNotificationSource source, RecordNotificationType type, String label, String message,
			boolean cleared) {
		return new RecordNotification(Database.NEW_RECORD_ID, user, created, level, source, type, label, message, cleared);
	}

	public static RecordNotification newFailureFetch() {
		return RecordNotification.of(0, null, LocalDateTime.now(), RecordNotificationLevel.ERROR,
				RecordNotificationSource.SYSTEM, RecordNotificationType.FAILURE, "Failure", "Failed to fetch notifications", false);
	}

	public static RecordNotification newMissingLTFS() {
		return RecordNotification.of(0, null, LocalDateTime.now(), RecordNotificationLevel.WARNING,
				RecordNotificationSource.SYSTEM, RecordNotificationType.LTFS_MISSING, "LTFS not found", "Some features will be unavailable", false);
	}

	public int getID() {
		return id;
	}

	public void setID(int id) {
		this.id = id;
	}

	public RecordUser getUser() {
		return user;
	}

	public void setUser(RecordUser user) {
		this.user = user;
	}

	public LocalDateTime getCreated() {
		return created;
	}

	public void setCreated(LocalDateTime created) {
		this.created = created;
	}

	public RecordNotificationLevel getLevel() {
		return level;
	}

	public void setLevel(RecordNotificationLevel level) {
		this.level = level;
	}

	public RecordNotificationSource getSource() {
		return source;
	}

	public void setSource(RecordNotificationSource source) {
		this.source = source;
	}

	public RecordNotificationType getType() {
		return type;
	}

	public void setType(RecordNotificationType type) {
		this.type = type;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public boolean getCleared() {
		return cleared;
	}

	public void setCleared(boolean cleared) {
		this.cleared = cleared;
	}
}