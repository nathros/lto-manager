package lto.manager.common.database.tables.records;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

import lto.manager.common.database.Database;
import lto.manager.common.database.tables.TableRoles;

public class RecordRole {
	private Integer id;
	private String name;
	private String description;
	private BitSet permission; // Bit mask

	public RecordRole(Integer id, String name, String description, BitSet permission) {
		this.id = id;
		this.name = name;
		this.description = description;
		this.permission = permission;
	}

	public static RecordRole of(Integer id, String name, String description, BitSet permission) {
		return new RecordRole(id, name, description, permission);
	}

	public static RecordRole of(String name, String description, BitSet permission) {
		return new RecordRole(Database.NEW_RECORD_ID, name, description, permission);
	}

	public static List<RecordRole> getDefaultRoles() {
		List<RecordRole> roles = new ArrayList<RecordRole>();
		BitSet all = new BitSet(1024);
		all.set(0, 1023);
		BitSet subset = new BitSet(1024);
		subset.set(0, 127);
		roles.add(RecordRole.of(TableRoles.ROLE_ID_ADMIN, "Admin", "Administrator of the whole application", all));
		roles.add(RecordRole.of(TableRoles.ROLE_ID_VIEWER, "Viewer", "Can only view library and files", subset));
		return roles;
	}

	public Integer getID() { return id; }
	public void setID(int id) { this.id = id; }
	public String getName() { return name; }
	public void setName(String name) { this.name = name; }
	public String getDescription() { return description; }
	public void setDescription(String description) { this.description = description; }
	public BitSet getPermission() { return permission; }
	public void setName(BitSet permission) { this.permission = permission; }
}