function setCookie(name, value, days) {
	let expires = "";
	if (days) {
		let date = new Date();
		date.setTime(date.getTime() + (days * 24 * 60 * 60 * 1000));
		expires = "; expires=" + date.toUTCString();
	}
	document.cookie = name + "=" + (value || "") + expires + "; path=/";
}
function getCookie(name) {
	let nameEQ = name + "=";
	let ca = document.cookie.split(';');
	for(let i = 0; i < ca.length; i++) {
		let c = ca[i];
		while (c.charAt(0)==' ') c = c.substring(1,c.length);
		if (c.indexOf(nameEQ) == 0) return c.substring(nameEQ.length,c.length);
	}
	return null;
}
function eraseCookie(name) { document.cookie = name + '=; Max-Age=-1; path=/'; }
const COOKIE_ON = "on";
const COOKIE_OFF = "off";

const APIStatus = {
	Ok: "ok",
	Error: "error"
}

const Toast = {
	Good: "good",
	Error: "error",
	Warning: "warning",
	Info: "info"
}

const ToastResponse = {
	Ok: "ok",
	Cancel: "cancel"
}

async function showToast(level, message, time, url, showCancel) {
	let toast = document.getElementById("toast");
	let toastMessage = document.getElementById("toast-message");
	if ((toast != null) && (toastMessage != null)) {
		toastMessage.innerHTML = message;
		toast.classList = "";
		toast.offsetWidth; // Reflow
		toast.className = "show " + level;
		showToast.toastLevel = level;
		document.getElementById("toast-cancel").style.display = showCancel ? "inline-flex" : "none";
		clearTimeout(showToast.toastTimeout);
		if (!time) time = 5000;
		if (time > 0) {
			document.getElementById("toast-cross").style.display = "block";
			showToast.toastTimeout = setTimeout(function(){ toast.className = "hide " + level }, time);
		} else {
			document.getElementById("toast-cross").style.display = "none";
			let response = await toastButtonPromise(url);
			return response;
		}
	} else {
		throw "Toast is not present";
	}
}

async function showToastCallback(level, message, callBack, showCancel) {
	let toast = document.getElementById("toast");
	let toastMessage = document.getElementById("toast-message");
	if ((toast != null) && (toastMessage != null)) {
		toastMessage.innerHTML = message;
		toast.classList = "";
		toast.offsetWidth; // Reflow
		toast.className = "show " + level;
		showToast.toastLevel = level;
		document.getElementById("toast-cancel").style.display = showCancel ? "inline-flex" : "none";
		clearTimeout(showToast.toastTimeout);
		document.getElementById("toast-cross").style.display = "none";
		let response = await toastButtonPromiseCallBack(callBack);
		return response;
	} else {
		throw "Toast is not present";
	}
}

function hideToast() {
	var toast = document.getElementById("toast");
	if (toast != null) {
		clearTimeout(showToast.toastTimeout);
		toast.className = "hide " + showToast.toastLevel;
	} else {
		throw "Toast is not present";
	}
}

function toastConfirm(url) {
	showToast(Toast.Warning, "Are you sure?", -1, url);
}

function toastButtonPromise(url) {
	let link = url;
	return new Promise((resolve/*, reject*/) => {
		let ok = document.getElementById("toast-ok");
		let cancel = document.getElementById("toast-cancel");
		let okFn = () => {
			resolve(ToastResponse.Ok);
			if (link) {
				window.location = link;
			}
			cancel.click(); // Remove other listener
			hideToast();
		};
		let canelFn = () => {
			link = undefined;
			resolve(ToastResponse.Cancel);
			ok.click(); // Remove other listener
			hideToast();
		};
		ok.addEventListener('click', okFn, {once: true});
		cancel.addEventListener('click', canelFn, {once: true});
	});
}

function toastButtonPromiseCallBack(callBack) {
	return new Promise((resolve/*, reject*/) => {
		let ok = document.getElementById("toast-ok");
		let cancel = document.getElementById("toast-cancel");
		let okFn = () => {
			resolve(ToastResponse.Ok);
			hideToast();
			callBack();
		};
		let cancelFn = () => {
			resolve(ToastResponse.Cancel);
			hideToast();
		};
		ok.addEventListener('click', okFn, {once: true});
		cancel.addEventListener('click', cancelFn, {once: true});
	});
}

function confirmToast(href, message) {
	showToastCallback(Toast.Warning, message, () => { window.location.href = href; }, true);
	return false;
}

function getNameFromPath(path) {
	// input: /abc/def/ghi/ output: ghi
	path = path.slice(0, -1);
	const index = path.lastIndexOf("/");
	const name = path.slice(index + 1);
	return (name === "" ? "/" : name);
}

function getUUID() { // Not cryptographic
	return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function(c) {
		let r = Math.random()*16|0, v = c == 'x' ? r : (r&0x3|0x8);
		return v.toString(16);
	});
}
function shutdownConfirm() {
	return confirmToast("/shutdown/", 'Are you sure?<br>This will shutdown service');
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

function tableSort(sender, tableID) {
	let selfIndex = -1;
	let sortFunction;
	const table = tableID === undefined ?
		sender.parentElement.parentElement.parentElement.parentElement :
		document.getElementById(tableID).children[0];
	const arrows = table.getElementsByTagName("span");
	for (let i = 0; i < arrows.length; i++) {
		if (arrows[i] != sender) {
			arrows[i].classList.remove("down", "up");
		} else {
			selfIndex = i;
		}
	}
	if (sender.classList.length == 1 || sender.classList.contains("up")) {
		sender.classList.add("down");
		sender.classList.remove("up");
		sortFunction = ascSort;
	} else {
		sender.classList.add("up");
		sender.classList.remove("down");
		sortFunction = ascDec;
	}
	const tableAsArray = Array.from(table.children);
	tableAsArray.shift(); // Remove header
	tableAsArray
		.sort((a, b) => {
			return sortFunction(a.children[selfIndex].innerText, b.children[selfIndex].innerText);
		})
		.forEach(row => table.appendChild(row));
}

function tableFilterInput(sender, tableID) { // TODO multiple filters are possible but conflict with eachother
	const searchText = sender.value.toLowerCase();
	const table = tableID === undefined ?
		sender.parentElement.parentElement.parentElement :
		document.getElementById(tableID).children[0];
	let cellIndex;

	const firstTR = table.children[0].children;
	for (let i = 0; i < firstTR.length; i++) { // Find column to filter
		if (sender == firstTR[i].children[1]) {
			cellIndex = i;
			break;
		}
	}

	for (let i = 1; i < table.children.length; i++) {
		const found = table.children[i].children[cellIndex].innerText.toLowerCase().search(searchText);
		table.children[i].style.display = found == -1 ? "none" : "table-row";
	}
}

function tableFilterShow(tableID, sender) {
	const table = document.getElementById(tableID);
	const th = table.getElementsByTagName("th");
	let show = false;
	for (let i = 0; i < th.length; i++) {
		try {
			const ele = th[i].children[1];
			show = ele.style.display == "block";
			ele.style.display = show ? "none" : "block";
		} catch (e) {}
	}
	sender.innerText = show ? "Show" : "Hide";
}
