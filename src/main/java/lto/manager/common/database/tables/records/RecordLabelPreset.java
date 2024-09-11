package lto.manager.common.database.tables.records;

public class RecordLabelPreset {
	private int id;
	private RecordUser user;
	private String name;
	private String json;


	public RecordLabelPreset(int id, RecordUser user, String name, String json) {
		this.id = id;
		this.user = user;
		this.name = name;
		this.json = json;
	}

	public static RecordLabelPreset of(int id, RecordUser user, String name, String json) {
		return new RecordLabelPreset(id, user, name, json);
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

	public String getJSON() {
		return json;
	}

	public void setJSON(String json) {
		this.json = json;
	}

}