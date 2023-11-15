const HOST_FILEVIEW_ROOT_ID = "id-wtree";
const HOST_FILEVIEW_CONEXT_CONTAINER_ID = "wtree-context-container";
const HOST_FILEVIEW_SELECT_TOTAL = "id-wtree-total";
const HOST_FILEVIEW_QUERY_SELECTED = "f@"
const HOST_FILEVIEW_QUERY_PATH = "f@path";
const HOST_FILEVIEW_QUERY_BREADCRUMBS = "f@bread";
const HOST_FILEVIEW_QUERY_MAX_DEPTH = "f@depth";
const HOST_FILEVIEW_QUERY_IS_ROOT = "f@root";
const HOST_FILEVIEW_QUERY_IS_VIRTUAL = "f@isvirtual";

function getIDPostFix(virtual) {
	return (virtual === true ? "-v" : "-p")
}

function ascSort(a, b) {
	let x = parseInt(a);
	let y = parseInt(b);
	if (isNaN(x) || isNaN(y)) {
		return a < b ? -1 : 1;
	}
	return x > y ? -1 : 1;
}

function ascDec(a, b) {
	return ascSort(a, b) * -1;
}

function sort(sender, type) {
	let add, sortFunction;
	if (sender.classList.contains("down")) {
		add = "up";
		sortFunction = ascSort;
	} else {
		add = "down";
		sortFunction = ascDec;
	}

	let otherButtons = sender.parentElement.getElementsByTagName("span");
	otherButtons[0].classList.remove("down", "up");
	otherButtons[1].classList.remove("down", "up");
	otherButtons[2].classList.remove("down", "up");
	sender.classList.add(add);

	let parent = sender.parentElement.parentElement;
	let ul = parent.getElementsByTagName("ul")[0];
	Array.from(ul.children)
		.sort((a, b) => {
			let emA = a.getElementsByTagName("span");
			let emB = b.getElementsByTagName("span");
			let strA = emA[0].getAttribute(type);
			let strB = emB[0].getAttribute(type);
			let x = sortFunction(strA, strB);
			return x;
		})
		.forEach(li => ul.appendChild(li));
}

function selectDir(sender) {
	let dirCheckboxes = sender.parentElement.parentElement.getElementsByTagName("input");
	for (let item of dirCheckboxes) {
		item.checked = sender.checked
	}
	selectFile(sender);
}

function selectFile(sender) {
	let list = sender.parentElement.parentElement.parentElement;
	let dirCheckboxes = list.getElementsByTagName("input");
	let checked = 0;
	let unchecked = 0;
	for (let item of dirCheckboxes) {
		item.checked ? checked++ : unchecked++;
	}
	let dir = list.parentElement.getElementsByTagName("input");
	if (checked !=0 && unchecked != 0) {
		dir[0].indeterminate = true;
	} else if (checked != 0) {
		dir[0].indeterminate = false;
		dir[0].checked = true;
	} else if (unchecked != 0) {
		dir[0].indeterminate = false;
		dir[0].checked = false;
	}
}

function selectPathEditBox(sender) {
	let input = sender.nextElementSibling.childNodes[1];
	// Blur event fires before the focus happens so the element hides as you try to focus it
	input.classList.add('active');
	input.focus();
	input.classList.remove('active');
	input.selectionStart = input.selectionEnd = input.value.length;
}

function keydownPathEditBox(sender, event) {
	if (event.key === 'Escape') {
		sender.blur();
		sender.parentElement.parentElement.focus();
    } else if (event.key === 'Enter') {
		sender.nextElementSibling.click();
    }
}

function hostChangeDirManual(sender, virtual) {
	hostChangeDir(sender.previousElementSibling.value, virtual);
}

async function hostChangeDir(path, virtual) {
	if (virtual === false) {
		let size = calculateSelectedFileSizeTotal();
		if (size > 0) {
			let response = await showToast(Toast.Warning, 'Some files are selected, these will be reset' , -1, undefined, true);
			if (response == ToastResponse.Cancel) return;
		}
	}
	let root = document.getElementById(HOST_FILEVIEW_ROOT_ID + getIDPostFix(virtual));
	let checkboxes = root.getElementsByTagName("input");
	let params = new URLSearchParams();
	params.append(HOST_FILEVIEW_QUERY_PATH, path);
	params.append(HOST_FILEVIEW_QUERY_MAX_DEPTH, "1");
	params.append(HOST_FILEVIEW_QUERY_IS_ROOT, true);
	params.append(HOST_FILEVIEW_QUERY_IS_VIRTUAL, virtual);

	let bread = document.getElementById(HOST_FILEVIEW_QUERY_BREADCRUMBS + getIDPostFix(virtual));
	let breadValue = "";
	if (bread != null) breadValue = bread.value;
	params.append(HOST_FILEVIEW_QUERY_BREADCRUMBS, breadValue);
	for (let i = 0; i < checkboxes.length; i++) {
		if (checkboxes[i].checked) {
			params.append(HOST_FILEVIEW_QUERY_SELECTED, checkboxes[i].value);
		}
	}

	fetch("/ajax/fileslist",
	{
		method: "post",
		headers: {
			"Content-Type": "application/x-www-form-urlencoded"
  		},
  		body: params
	}).then((response) => {
		return response.text();
	}).then((div) => {
		root.outerHTML = div;
	}).catch((error) => {
		console.log(error);
	})
}

function hideFileTree(sender) {
	let list = sender.parentElement.nextElementSibling;
	if (list.style.display == "none") {
		list.style.display = "block";
		sender.innerHTML = "+";
	} else {
		list.style.display = "none";
		sender.innerHTML = "-";
	}
}

function expandDir(sender, path, virtual) {
	let items = sender.parentElement.parentElement.getElementsByTagName("li");
	if (items.length > 0) { // Children already populated
		hideFileTree(sender);
		return;
	}
	let params = new URLSearchParams();
	params.append(HOST_FILEVIEW_QUERY_PATH, path);
	params.append(HOST_FILEVIEW_QUERY_MAX_DEPTH, "1");
	params.append(HOST_FILEVIEW_QUERY_IS_ROOT, false);
	params.append(HOST_FILEVIEW_QUERY_IS_VIRTUAL, virtual);

	fetch("/ajax/fileslist",
	{
		method: "post",
		headers: {
			"Content-Type": "application/x-www-form-urlencoded"
  		},
  		body: params
	}).then((response) => {
		return response.text();
	}).then((div) => {
		div = div.substring(6, div.length - 6); // Remove <div>...</div>
		sender.parentElement.parentElement.innerHTML = div;
	}).catch((error) => {
		console.log(error);
	});
}

function bytesToHumanReadable(bytes) {
	let count = 0;
	while (bytes > 1024) {
		bytes /= 1024;
		count++;
	}
	switch (count) {
	case 0: return bytes + " bytes";
	case 1: return parseFloat(bytes).toFixed(2) + " KB";
	case 2: return parseFloat(bytes).toFixed(2) + " MB";
	case 3: return parseFloat(bytes).toFixed(2) + " GB";
	default: return parseFloat(bytes).toFixed(2) + " TB";
	}
}

function calculateSelectedFileSizeTotal() {
	let container = document.getElementById(HOST_FILEVIEW_ROOT_ID + getIDPostFix(false));
	let size = 0;
	let spans = container.getElementsByTagName('span');
	for (let item of spans) {
		let child = item.firstElementChild;
		if (child?.type === 'checkbox') {
			if (child.checked) {
				size += parseInt(item.getAttribute('data-size'));
			}
		}
	}
	return size;
}

function recalculateSelectedFileSize() {
	let message = document.getElementById(HOST_FILEVIEW_SELECT_TOTAL);
	let size = calculateSelectedFileSizeTotal();
	message.getElementsByTagName("b")[0].innerText = bytesToHumanReadable(size);
}

function contextMenu(sender, virtual, event) {
	console.log(sender);
	if (!virtual) return true;
	let container = document.getElementById(HOST_FILEVIEW_CONEXT_CONTAINER_ID + getIDPostFix(virtual));
	let menu = container.childNodes[1];
	container.style.display = "block";
	menu.style.top = event.pageY + 1 + "px";
	menu.style.left = event.pageX + 1 + "px";
	return false;
}

function contextMenuHide(virtual) {
	let container = document.getElementById(HOST_FILEVIEW_CONEXT_CONTAINER_ID + getIDPostFix(virtual));
	let menu = container.childNodes[1];
	if (!menu.contains(document.activeElement)) {
		container.style.display = ""; // Lost focus so hide context menu
	}
}

function newVirtualDir(path, newDir) {
	console.log(path);
	console.log(newDir);
	if (newDir === "") {
		return showToast(Toast.Error, "Directory name cannot be empty", -1, undefined, false);
	}
	fetch(`/api/virtualdir?new=true&path=${path}&name=${newDir}`,
	{
		method: "GET",
		signal: AbortSignal.timeout(3000)
	}).then((response) => {
		return response.json();
	}).then((json) => {
		switch (json.status) {
		case APIStatus.Ok:
			hostChangeDir(path, true); // Refresh page
			break;
		case APIStatus.Error:
		default:
			showToast(Toast.Error, `Failed to create new directory: ${json.status}`, -1, undefined, false);
			break;
		}
	}).catch((error) => {
		showToast(Toast.Error, `Failed to create new directory: ${error}`, -1, undefined, false);
	});
}
