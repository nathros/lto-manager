const HOST_FILEVIEW_ROOT_ID = "id-wtree";
const HOST_FILEVIEW_QUERY_SELECTED = "f@"
const HOST_FILEVIEW_QUERY_PATH = "f@path";
const HOST_FILEVIEW_QUERY_BREADCRUMBS = "f@bread";
const HOST_FILEVIEW_QUERY_MAX_DEPTH = "f@depth";
const HOST_FILEVIEW_QUERY_IS_ROOT = "f@root";

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

function hostChangeDir(path) {
	let root = document.getElementById(HOST_FILEVIEW_ROOT_ID);
	let checkboxes = root.getElementsByTagName("input");
	let params = new URLSearchParams();
	params.append(HOST_FILEVIEW_QUERY_PATH, path);
	params.append(HOST_FILEVIEW_QUERY_MAX_DEPTH, "1");
	params.append(HOST_FILEVIEW_QUERY_IS_ROOT, "1");

	let bread = document.getElementById(HOST_FILEVIEW_QUERY_BREADCRUMBS);
	let breadValue = "";
	if (bread != null) breadValue = bread.value;
	params.append(HOST_FILEVIEW_QUERY_BREADCRUMBS, breadValue);
	for (let i = 0; i < checkboxes.length; i++) {
		if (checkboxes[i].checked) {
			params.append(HOST_FILEVIEW_QUERY_SELECTED, checkboxes[i].value);
		}
	}

	fetch("/fetcher/hfileslist",
	{
		method: "post",
		headers: {
			"Content-Type": "application/x-www-form-urlencoded"
  		},
  		body: params
	}).then((response) => {
		return response.text();
	}).then((div) => {
		root.innerHTML = div;
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

function expandDir(sender, path) {
	let items = sender.parentElement.parentElement.getElementsByTagName("li");
	if (items.length > 0) { // Children already populated
		hideFileTree(sender);
		return;
	}
	let params = new URLSearchParams();
	params.append(HOST_FILEVIEW_QUERY_PATH, path);
	params.append(HOST_FILEVIEW_QUERY_MAX_DEPTH, "1");
	params.append(HOST_FILEVIEW_QUERY_IS_ROOT, "0");

	fetch("/fetcher/hfileslist",
	{
		method: "post",
		headers: {
			"Content-Type": "application/x-www-form-urlencoded"
  		},
  		body: params
	}).then((response) => {
		return response.text();
	}).then((div) => {
		sender.parentElement.parentElement.innerHTML = div;
	}).catch((error) => {
		console.log(error);
	})
}



