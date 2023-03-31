package lto.manager.web.resource;

public class JS {
	public static String fnFileViewListChangeDir(String path, boolean isVirtual) { return "hostChangeDir('" + path + "'," + isVirtual + ")"; }
	public static String fnFileViewListChangeDirManual(boolean isVirtual) { return "hostChangeDirManual(this," + isVirtual + ")"; }
	public static String fnFileViewSelectPathEditBox() { return "selectPathEditBox(this)"; }
	public static String fnFileViewKeyDownEditBox() { return "keydownPathEditBox(this, event)"; }
	public static String fnFileViewExpandDir(String path, boolean isVirtual) { return "expandDir(this,'" + path + "'," + isVirtual + ")"; }
	public static String fnFileViewSort(String field) { return "sort(this,'" + field + "')"; }

}
