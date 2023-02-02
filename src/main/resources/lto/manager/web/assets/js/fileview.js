function hideFileTree(sender) {
	let list = sender.parentElement.nextElementSibling;
	if (list.style.display == "none") {
		list.style.display = "block";
	} else {
		list.style.display = "none";
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
	let x = parseInt(a);
	let y = parseInt(b);
	if (isNaN(x) || isNaN(y)) {
		return a > b ? -1 : 1;
	}
	return x < y ? -1 : 1;
}

function sort(sender, type) {
	let add, sortLambda;
	if (sender.classList.contains("down")) {
		add = "up";
		sortLambda = ascSort;
	} else {
		add = "down";
		sortLambda = ascDec;
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
			let x = sortLambda(strA, strB);
			return x;
		})
		.forEach(li => ul.appendChild(li));
}
