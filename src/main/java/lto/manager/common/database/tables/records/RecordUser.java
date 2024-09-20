package lto.manager.common.database.tables.records;

import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.security.crypto.bcrypt.BCrypt;

import lto.manager.common.database.Database;
import lto.manager.common.database.tables.records.RecordRole.Permission;

public class RecordUser {
	private int id;
	private RecordRole role;
	private String username;
	private String description;
	private String hash;
	private String salt;
	private boolean enabled;
	private LocalDateTime dateAdded;
	private int language;
	private String avatar;

	public static int DEFAULT_ID = 1;
	public static int ANONYMOUS_ID = 2;
	public static int GUEST_ID = 3;
	public static DecimalFormat df = new DecimalFormat("000"); // Faster than String.format("%03d", x);

	private static DateTimeFormatter dateTimeDF = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

	public RecordUser(int id, String username, String description, String password, boolean enabled,
			LocalDateTime dateAdded, int language, String avatar) {
		this.id = id;
		this.username = username;
		this.description = description;
		this.salt = generateSalt();
		this.hash = hashFunction(password, salt);
		this.enabled = enabled;
		this.dateAdded = dateAdded;
		this.language = language;
		this.avatar = avatar;
	}

	public RecordUser(int id, String username, String description, String hash, String salt, boolean enabled,
			LocalDateTime dateAdded, int language, String avatar) {
		this.id = id;
		this.username = username;
		this.description = description;
		this.hash = hash;
		this.salt = salt;
		this.enabled = enabled;
		this.dateAdded = dateAdded;
		this.language = language;
		this.avatar = avatar;
	}

	public RecordUser(int id) {
		this.id = id;
	}

	public static RecordUser of(int id, String username, String description, String hash, String salt, boolean enabled,
			LocalDateTime dateAdded, int language, String avatar) {
		return new RecordUser(id, username, description, hash, salt, enabled, dateAdded, language, avatar);
	}

	public static RecordUser of(int id, RecordRole role, String username, String description, String hash, String salt, boolean enabled,
			LocalDateTime dateAdded, int language, String avatar) {
		var user = new RecordUser(id, username, description, hash, salt, enabled, dateAdded, language, avatar);
		user.setRole(role);
		return user;
	}

	public static RecordUser of(int id) {
		return new RecordUser(id); // For lazy loading
	}

	public static RecordUser getDefaultUser() {
		return new RecordUser(DEFAULT_ID, "root", "Superuser", "root", true, LocalDateTime.now(), 0, "default.svg")
				.setRole(RecordRole.getDefaultRoles().get(RecordRole.ROLE_ID_ADMIN - 1));
	}

	public static RecordUser getAnonymousUser() {
		return new RecordUser(ANONYMOUS_ID, "anonymous", "Only used when login is disabled", "anonymous", true,
				LocalDateTime.now(), 0, "default.svg")
				.setRole(RecordRole.getDefaultRoles().get(RecordRole.ROLE_ID_ADMIN - 1));
	}

	public static RecordUser getGuestUser() {
		return new RecordUser(GUEST_ID, "guest", "Has view only rights", "guest", /*FIXEME temp*/true, LocalDateTime.now(), 0, "default.svg")
				.setRole(RecordRole.getDefaultRoles().get(RecordRole.ROLE_ID_VIEWER - 1));
	}

	public static RecordUser getBlank() {
		return new RecordUser(Database.NEW_RECORD_ID, "", "", "", true, LocalDateTime.now(), 0, "default.svg")
				.setRole(RecordRole.getDefaultRoles().get(RecordRole.ROLE_ID_ADMIN - 1));
	}

	private static String generateSalt() {
		final String salt = BCrypt.gensalt();
		return salt;
	}

	public static String hashFunction(String password, String salt) {
		final String hash = BCrypt.hashpw(password, salt);
		return hash;
	}

	public String getHashedPassword(String password) {
		return hashFunction(password, salt);
	}

	public int getID() {
		return id;
	}

	public void setID(int id) {
		this.id = id;
	}

	public RecordRole getRole() {
		return role;
	}

	public RecordUser setRole(RecordRole role) {
		this.role = role;
		return this;
	}

	public String getUsername() {
		return username;
	}

	public RecordUser setUsername(String username) {
		this.username = username;
		return this;
	}

	public String getDescription() {
		return description;
	}

	public RecordUser setDescription(String description) {
		this.description = description;
		return this;
	}

	public String getHash() {
		return hash;
	}

	public RecordUser setHash(String hash) {
		this.hash = hash;
		return this;
	}

	public RecordUser setPassword(String password) {
		this.hash = hashFunction(password, salt);
		return this;
	}

	public String getSalt() {
		return salt;
	}

	public RecordUser setSalt(String salt) {
		this.salt = salt;
		return this;
	}

	public boolean getEnabled() {
		return enabled;
	}

	public RecordUser setEnabled(boolean enabled) {
		this.enabled = enabled;
		return this;
	}

	public LocalDateTime getCreated() {
		return dateAdded;
	}

	public String getCreatedFormatted() {
		return dateTimeDF.format(dateAdded);
	}

	public RecordUser setCreated(LocalDateTime dateAdded) {
		this.dateAdded = dateAdded;
		return this;
	}

	public int getLanguage() {
		return language;
	}

	public String getLanguageFormatted() {
		return df.format(language);
	}

	public RecordUser setLanguage(int language) {
		this.language = language;
		return this;
	}

	public String getAvatar() {
		return avatar;
	}

	public RecordUser setAvatar(String avatar) {
		this.avatar = avatar;
		return this;
	}

	public boolean hasAccess(Permission permission) {
		if (role == null) {
			return false; // This has not been set
		} else {
			return role.hasPermission(permission);
		}
	}
}