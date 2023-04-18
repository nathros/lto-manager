const Toast = {
	Good: "good",
	Error: "error",
	Warning: "warning"
}

const ToastResponse = {
	Ok: "ok",
	Cancel: "cancel"
}

async function showToast(level, message, time) {
	var toast = document.getElementById("toast");
	var toastMessage = document.getElementById("toast-message");
	if ((toast != null) && (toastMessage != null)) {
		toastMessage.innerHTML = message;
		toast.classList = "";
		toast.offsetWidth; // Reflow
		toast.className = "show " + level;
		showToast.toastLevel = level;
		clearTimeout(showToast.toastTimeout);
		if (time == null) time = 5000;
		if (time > 0) {
			document.getElementById("toast-cross").style.display = "block";
			showToast.toastTimeout = setTimeout(function(){ toast.className = "hide " + level }, time);
		} else {
			document.getElementById("toast-cross").style.display = "none";
			let response = await toastButtonPromise();
			return response;
		}
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

function toastButtonPromise() {
	return new Promise((resolve/*, reject*/) => {
		let ok = document.getElementById("toast-ok");
		let cancel = document.getElementById("toast-cancel");
		let okFn = () => {
			resolve(ToastResponse.Ok);
			cancel.click(); // Remove other listener
			hideToast();
		};
		let canelFn = () => {
			resolve(ToastResponse.Cancel);
			ok.click(); // Remove other listener
			hideToast();
		};
		ok.addEventListener('click', okFn, {once: true});
		cancel.addEventListener('click', canelFn, {once: true});
	});
}
