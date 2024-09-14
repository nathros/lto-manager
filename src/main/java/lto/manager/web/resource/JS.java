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
	public static String fnFileNewVirtualDir(String path) { return "newVirtualDir('" + path + "',this.previousElementSibling.value);"; }
	public static String fnFileCheckBoxChange() { return "recalculateSelectedFileSize();"; }
	public static String fnGetDirIcons() { return "getDirIcons(this);"; }
	public static String fnSetDirIcon() { return "setDirIcon(this);"; }

	public static final String INPUT_UPPERCASE = "this.value=this.value.toUpperCase()";

	public static String tableSort() { return "tableSort(this)"; }
	public static String tableSort(String id) { return "tableSort(this, '" + id + "')"; }
	public static String tableFilterShow(String id) { return "tableFilterShow('" + id + "',this);"; }
	public static String tableFilter() { return "tableFilterInput(this);"; }
	public static String tableFilter(String id) { return "tableFilterInput(this,'" + id + "');"; }

	public static String commonHideToast() { return "hideToast();"; }
	public static String confirmToast(String url) { return "toastConfirm('" + url + "')"; }

	public static String generateLTOLabel(final String path) { return "generateBarcode('" + path + "')"; }

	public static String confirmToastA(final String message) { return "return confirmToast(this.href, '" + message + "')"; }

}
