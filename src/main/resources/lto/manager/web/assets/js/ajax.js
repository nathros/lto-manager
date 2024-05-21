function updateElement(element, html, removeParent = false) {
	if (removeParent) {
		html = html.substring(5, html.length - 6); // Remove <div>...</div>
	}
	element.outerHTML = html;
}

function updateElementInner(element, html, removeParent = false) {
	if (removeParent) {
		html = html.substring(5, html.length - 6); // Remove <div>...</div>
	}
	element.innerHTML = html;
}

// Go through all elements that have dynamic/async contents
function onLoadAJAX() {
	const AJAX_ATT = "data-ajax";
	let elements = document.querySelectorAll(`[${AJAX_ATT}]`);
	elements.forEach(element => {
		const url = element.getAttribute(AJAX_ATT);
		console.debug(`onLoadAJAX: ${url}`);
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
			updateElement(element, div);
		}).catch((error) => {
			onLoadInlineError(element, "Failed to complete", error);
			console.log(error);
		})
	});
}

window.addEventListener('load', onLoadAJAX, false);

function onLoadInlineError(element, title, error) {
	fetch(`/ajax/onloaderror?title=${title}&message=${error}`,
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
