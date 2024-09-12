package lto.manager.common.database.tables.records;

import lto.manager.common.database.Database;

public class RecordLabelPreset {
	private int id;
	private RecordUser user;
	private String name;
	private String config;


	public RecordLabelPreset(int id, RecordUser user, String name, String config) {
		this.id = id;
		this.user = user;
		this.name = name;
		this.config = config;
	}

	public static RecordLabelPreset of(int id, RecordUser user, String name, String config) {
		return new RecordLabelPreset(id, user, name, config);
	}

	public static RecordLabelPreset of(int userId, String name, String config) {
		return new RecordLabelPreset(Database.NEW_RECORD_ID, RecordUser.of(userId), name, config);
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getConfig() {
		return config;
	}

	public void setConfig(String config) {
		this.config = config;
	}

}