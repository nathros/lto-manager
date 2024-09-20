package lto.manager.common.database.tables.records;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

import org.apache.pdfbox.util.Hex;

import lto.manager.common.database.Database;
import lto.manager.common.database.tables.TableRoles;

public class RecordRole {
	private Integer id;
	private String name;
	private String description;
	private BitSet permission; // Bit mask

	public static final int ROLE_ID_ADMIN = 1;
	public static final int ROLE_ID_VIEWER = 2;

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

	public static RecordRole of(Integer id) {
		return new RecordRole(id, null, null, null);
	}

	public static List<RecordRole> getDefaultRoles() {
		List<RecordRole> roles = new ArrayList<RecordRole>();
		BitSet all = new BitSet(TableRoles.PERMISSION_LEN_INDEX);
		all.set(0, TableRoles.PERMISSION_LEN_INDEX); // Enable all
		roles.add(RecordRole.of(ROLE_ID_ADMIN, "Admin", "Administrator of the whole application", all));

		BitSet subset = new BitSet(TableRoles.PERMISSION_LEN_INDEX);
		roles.add(RecordRole.of(ROLE_ID_VIEWER, "Viewer", "Can only view library and files", subset));
		return roles;
	}

	public Integer getID() { return id; }
	public void setID(int id) { this.id = id; }
	public String getName() { return name; }
	public void setName(String name) { this.name = name; }
	public String getDescription() { return description; }
	public void setDescription(String description) { this.description = description; }
	public BitSet getPermission() { return permission; }
	public String getPermissionHexString() {
		final byte[] byteArrayData = permission.toByteArray();
		final String hexData = Hex.getString(byteArrayData);
		if (hexData.length() < TableRoles.PERMISSION_LEN) {
			final String hexDataPadded = "0".repeat(TableRoles.PERMISSION_LEN - hexData.length()) + hexData;
			return hexDataPadded;
		} else if (hexData.length() < TableRoles.PERMISSION_LEN) {
			return hexData.substring(0, TableRoles.PERMISSION_LEN);
		}
		return hexData;
	}
	public void setPermission(BitSet permission) { this.permission = permission; }
	public boolean hasPermission(Permission index) {
		return permission.get(index.getValue());
	}

	public enum Permission {
		ADMIN(0),

		SYSTEM_USERS_READ(100),
		SYSTEM_USERS_CREATE(101),
		SYSTEM_USERS_EDIT(102),
		SYSTEM_USERS_DELETE(103),

		MAX(TableRoles.PERMISSION_LEN_INDEX);

		private final int value;

		Permission(final int newValue) {
            value = newValue;
        }

		public int getValue() { return value; }
	}
}