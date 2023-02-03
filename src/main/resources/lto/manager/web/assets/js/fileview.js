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
