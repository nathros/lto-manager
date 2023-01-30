function hideFileTree(sender) {
	let list = sender.parentElement.nextElementSibling;
	if (list.style.display == "none") {
		list.style.display = "block";
	} else {
		list.style.display = "none";
	}
}

function sort(sender) {
	if (sender.classList.contains("selected")) {
		sender.classList.remove('selected');
	} else {
		sender.classList.add('selected');
	}
}