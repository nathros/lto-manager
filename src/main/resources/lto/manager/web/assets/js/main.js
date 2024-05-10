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
async function shutdownConfirm() {
	let response = await showToast(Toast.Warning, 'Are you sure? This will shutdown service' , -1, undefined, true);
	if (response != ToastResponse.Cancel) {
		window.location.href = "/shutdown"
	}
}
