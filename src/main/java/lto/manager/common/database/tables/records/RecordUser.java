package lto.manager.common.database.tables.records;

import java.time.LocalDateTime;

import org.springframework.security.crypto.bcrypt.BCrypt;

import lto.manager.common.database.Database;

public class RecordUser {
	private int id;
	private String username;
	private String hash;
	private String salt;
	private long permissionMask;
	private boolean enabled;
	private LocalDateTime dateAdded;

	public RecordUser(int id, String username, String password, long permissionMask, boolean enabled, LocalDateTime dateAdded) {
		this.id = id;
		this.username = username;
		this.salt = generateSalt();
		this.hash = hashFunction(password, salt);
		this.permissionMask = permissionMask;
		this.enabled = enabled;
		this.dateAdded = dateAdded;
	}

	public RecordUser(int id, String username, String hash, String salt, long permissionMask, boolean enabled, LocalDateTime dateAdded) {
		this.id = id;
		this.username = username;
		this.hash = hash;
		this.salt = salt;
		this.permissionMask = permissionMask;
		this.enabled = enabled;
		this.dateAdded = dateAdded;
	}

	public static RecordUser of(int id, String username, String hash, String salt, long permissionMask, boolean enabled, LocalDateTime dateAdded) {
		return new RecordUser(id, username, hash, salt, permissionMask, enabled, dateAdded);
	}

	public static RecordUser getDefaultUser() {
		return new RecordUser(Database.NEW_RECORD_ID, "root", "root", Long.MAX_VALUE, true, LocalDateTime.now());
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

	public int getID() { return id;}
	public void setID(int id) { this.id = id; }
	public String getUsername() { return username;}
	public void setUsername(String username) { this.username = username; }
	public String getHash() { return hash;}
	public void setHash(String hash) { this.hash = hash; }
	public String getSalt() { return salt;}
	public void setSalt(String salt) { this.salt = salt; }
	public long getPermissionMask() { return permissionMask;}
	public void setPermissionMask(long permissionMask) { this.permissionMask = permissionMask; }
	public boolean getEnabled() { return enabled;}
	public void setEnabled(boolean enabled) { this.enabled = enabled; }
	public LocalDateTime getCreated() { return dateAdded; }
	public void setCreated(LocalDateTime dateAdded) { this.dateAdded = dateAdded; }
}