package lto.manager.web.resource;

public class JS {
	public static String fnFileViewListChangeDir(String path, boolean isVirtual) { return "hostChangeDir('" + path + "'," + isVirtual + ")"; }
	public static String fnFileViewListChangeDirManual(boolean isVirtual) { return "hostChangeDirManual(this," + isVirtual + ")"; }
	public static String fnFileViewSelectPathEditBox() { return "selectPathEditBox(this)"; }
	public static String fnFileViewKeyDownEditBox() { return "keydownPathEditBox(this, event)"; }
	public static String fnFileViewExpandDir(String path, boolean isVirtual) { return "expandDir(this,'" + path + "'," + isVirtual + ")"; }
	public static String fnFileViewSort(String field) { return "sort(this,'" + field + "')"; }
	public static String fnFileContextMenu(boolean isVirtual) { return "return contextMenu(this," + isVirtual + ",event);"; }
	public static String fnFileContextMenuHide(boolean isVirtual) { return "contextMenuHide(" + isVirtual + ");"; }
	public static String fnFileNewVirtualDir() { return "newVirtualDir(this);"; }
}
