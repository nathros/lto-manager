const AJAX_ATT = "data-ajax";
const AJAX_SUCCESS = "data-ajax-scb";
const AJAX_ERROR = "data-ajax-ecb";
const InlineMessage = {
	Good: "good",
	Error: "error",
	Warning: "warning",
	Info: "info"
}

function updateElement(element, html, removeParent = false) {
	if (removeParent) {
		html = html.substring(5, html.length - 6); // Remove <div>...</div>
	}
	element.outerHTML = html;
	onLoadAJAX(element); // Check if new element has any triggers
}

function updateElementInner(element, html, removeParent = false) {
	if (removeParent) {
		html = html.substring(5, html.length - 6); // Remove <div>...</div>
	}
	element.innerHTML = html;
	onLoadAJAX(element); // Check if new element has any triggers
}

function ajaxFetch(url, element, callbackSuccess, callbackError, removeParent = false) {
	if (!element) {
		console.error(`ajaxFetch element is empty for url ${url}`);
		return;
	}
	fetch(url,
	{
		method: "get",
		signal: AbortSignal.timeout(3000),
		headers: {
			"Content-Type": "application/x-www-form-urlencoded"
	}
	}).then((response) => {
		return response.text();
	}).then((div) => {
		updateElement(element, div, removeParent);
		if (callbackSuccess) {
			callbackSuccess();
		} else {
			const fn = element.getAttribute(AJAX_SUCCESS);
			if (fn) {
				const callback = window[fn];
				if (callback) {
					callback();
				} else {
					console.error(`Missing AJAX success callback ${fn} on element: ${element.outerHTML}`);
				}
			}
		}
	}).catch((error) => {
		inlineMessage(element, "Failed to complete", InlineMessage.Error, error);
		console.log(error);
		if (callbackError) {
			callbackError()
		} else {
			const fn = element.getAttribute(AJAX_ERROR);
			if (fn) {
				const callback = window[fn];
				if (callback) {
					callback();
				} else {
					console.error(`Missing AJAX error callback ${fn} on element ${element.outerHTML}`);
				}
			}
		}
	})
}

// Go through all elements that have dynamic/async contents
function onLoadAJAX(element) {
	let elements = element.querySelectorAll(`[${AJAX_ATT}]`);
	elements.forEach(element => {
		const url = element.getAttribute(AJAX_ATT);
		console.debug(`onLoadAJAX: url: ${url}, successCB: [${element.getAttribute(AJAX_SUCCESS)}], errorCB: [${element.getAttribute(AJAX_ERROR)}]`);
		ajaxFetch(url, element);
	});
}

function onLoadAJAXStart() {
	onLoadAJAX(document);
}

window.addEventListener('load', onLoadAJAXStart, false);

function inlineMessage(element, title, type, error, padding) {
	fetch(`/ajax/inlinemessage/?title=${title}&message=${error}&type=${type}` + (padding ? "&p=p" : ""),
	{
		method: "get"
	}).then((response) => {
		return response.text();
	}).then((div) => {
		updateElement(element, div);
	}).catch((error) => {
		element.outerHTML = "Failed to get error data";
		console.log(error);
	})
}
