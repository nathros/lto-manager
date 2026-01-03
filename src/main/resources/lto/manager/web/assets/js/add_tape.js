function onSelectTapeType(id, wormId, typeId) {
	const worm = document.getElementById(wormId);
	const sel = document.getElementById(typeId);
	const option = sel.children[sel.selectedIndex];
	if (option.getAttribute("data-worm") === "") {
		worm.disabled = true;
		worm.checked = false;
	} else {
		worm.disabled = false;
	}
	let data;
	if (worm.checked) {
		data = option.getAttribute("data-worm");
	} else {
		data = option.getAttribute("data-des");
	}
	if (data === "") data = "Not Supported";
	document.getElementById(id).value = data;
}
