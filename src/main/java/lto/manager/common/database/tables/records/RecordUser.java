package lto.manager.common.database.tables.records;

import java.text.DecimalFormat;
import java.time.LocalDateTime;

import org.springframework.security.crypto.bcrypt.BCrypt;

import lto.manager.common.database.tables.TableRoles;

public class RecordUser {
	private int id;
	private RecordRole role;
	private String username;
	private String hash;
	private String salt;
	private boolean enabled;
	private LocalDateTime dateAdded;
	private int language;
	private String avatar;

	public static int DEFAULT_ID = 1;
	public static DecimalFormat df = new DecimalFormat("000"); // Faster than String.format("%03d", x);

	public RecordUser(int id, String username, String password, boolean enabled, LocalDateTime dateAdded, int language,
			String avatar) {
		this.id = id;
		this.username = username;
		this.salt = generateSalt();
		this.hash = hashFunction(password, salt);
		this.enabled = enabled;
		this.dateAdded = dateAdded;
		this.language = language;
		this.avatar = avatar;
	}

	public RecordUser(int id, String username, String hash, String salt, boolean enabled, LocalDateTime dateAdded,
			int language, String avatar) {
		this.id = id;
		this.username = username;
		this.hash = hash;
		this.salt = salt;
		this.enabled = enabled;
		this.dateAdded = dateAdded;
		this.language = language;
		this.avatar = avatar;
	}

	public static RecordUser of(int id, String username, String hash, String salt, boolean enabled,
			LocalDateTime dateAdded, int language, String avatar) {
		return new RecordUser(id, username, hash, salt, enabled, dateAdded, language, avatar);
	}

	public static RecordUser of(int id, RecordRole role, String username, String hash, String salt, boolean enabled,
			LocalDateTime dateAdded, int language, String avatar) {
		var user = new RecordUser(id, username, hash, salt, enabled, dateAdded, language, avatar);
		user.setRole(role);
		return user;
	}

	public static RecordUser getDefaultUser() {
		return new RecordUser(DEFAULT_ID, "root", "root", true, LocalDateTime.now(), 0, "default.svg")
				.setRole(RecordRole.getDefaultRoles().get(TableRoles.ROLE_ID_ADMIN - 1));
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

	public String getHash() {
		return hash;
	}

	public RecordUser setHash(String hash) {
		this.hash = hash;
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
}