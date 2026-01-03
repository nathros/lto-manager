package lto.manager.web.resource;

public class JS {
	public static String fnFileViewListChangeDir(String path, boolean isVirtual) {
		return "hostChangeDir('" + path + "'," + isVirtual + ")";
	}

	public static String fnFileViewListChangeDirManual(boolean isVirtual) {
		return "hostChangeDirManual(this," + isVirtual + ")";
	}

	public static String fnFileViewSelectPathEditBox() {
		return "selectPathEditBox(this)";
	}

	public static String fnFileViewKeyDownEditBox() {
		return "keydownPathEditBox(this, event)";
	}

	public static String fnFileViewExpandDir(String path, boolean isVirtual) {
		return "expandDir(this,'" + path + "'," + isVirtual + ")";
	}

	public static String fnFileViewSort(String field) {
		return "sort(this,'" + field + "')";
	}

	public static String fnFileContextMenu(boolean isVirtual) {
		return "return contextMenu(this," + isVirtual + ",event)";
	}

	public static String fnFileContextMenuHide(boolean isVirtual) {
		return "contextMenuHide(" + isVirtual + ")";
	}

	public static String fnFileNewVirtualDir(String path) {
		return "newVirtualDir('" + path + "',this.previousElementSibling.value);";
	}

	public static String fnFileCheckBoxChange() {
		return "recalculateSelectedFileSize();";
	}

	public static String fnNewDirDialog() {
		return "newVirtualDirDialog();";
	}

	public static String fnGetDirIcons() {
		return "getDirIcons(this);";
	}

	public static String fnSetDirIcon() {
		return "setDirIcon(this);";
	}

	public static final String INPUT_UPPERCASE = "this.value=this.value.toUpperCase()";
	public static final String INPUT_LOWERCASE = "this.value=this.value.toLowerCase()";
	public static final String STOP_DEFAULT = "return false;";

	public static String tableSort() {
		return "tableSort(this)";
	}

	public static String tableSort(String id) {
		return "tableSort(this, '" + id + "')";
	}

	public static String tableFilterShow(String id) {
		return "tableFilterShow('" + id + "',this)";
	}

	public static String tableFilter() {
		return "tableFilterInput(this)";
	}

	public static String tableFilter(String id) {
		return "tableFilterInput(this,'" + id + "')";
	}

	public static String formValidate() {
		return "validateForm(this, event);";
	}

	public static String formSubmit(final String id) {
		return "return submitForm('" + id + "');";
	}

	public static String formFetch(final String id, final String queryKey, final String queryValue) {
		return "fetchForm('" + id + "','" + queryKey + "=" + queryValue + "');";
	}

	public static String formFetch(final String id, final String queryKey, final String queryValue,
			final String jsOnComplete) {
		return "fetchForm('" + id + "','" + queryKey + "=" + queryValue + "',() => {" + jsOnComplete + "});";
	}

	public static String commonHideToast() {
		return "hideToast();";
	}

	public static String confirmToast(String url) {
		return "toastConfirm('" + url + "')";
	}

	public static String showModal(final String id) {
		return "showModal('" + id + "');";
	}

	public static String showModal(final String id, boolean clearForm) {
		return "showModal('" + id + "'," + clearForm + ");";
	}

	public static String hideModal(final String id) {
		return "hideModal('" + id + "');";
	}

	public static String generateLTOLabel(final String path) {
		return "generateBarcode('" + path + "')";
	}

	public static String confirmToastA(final String message) {
		return "return confirmToast(this.href, '" + message + "')";
	}

	public static String libraryChangeTapeType(final String id, final String wormId, final String typeId) {
		return "onSelectTapeType('" + id + "','" + wormId + "','" + typeId + "');"; // See add_tape.js
	}

	public static String libraryChangeManufacturer(final String formId, final String typeSelectId) {
		return "refetchType('" + formId + "','" + typeSelectId + "');"; // See add_tape.js
	}
}
