function onSelectType() {
	let worm = document.getElementById("worm");
	let sel = document.getElementById("select-type");
	let option = sel.children[sel.selectedIndex];
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
	document.getElementById("des").value = data;
}

function onSelectImmediate(sender) {
	let dateField = document.getElementById("start");
	dateField.disabled = sender.checked;
}