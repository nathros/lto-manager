function onLoadAJAX() {
	const AJAX_ATT = "data-ajax";
	let elements = document.querySelectorAll(`[${AJAX_ATT}]`);
	elements.forEach(element => {
		const uuid = getUUID();
		const url = `${element.getAttribute(AJAX_ATT)}?uuid=${uuid}`;
		fetch(url,
		{
			method: "get",
			signal: AbortSignal.timeout(1),
			headers: {
				"Content-Type": "application/x-www-form-urlencoded"
	  		}
		}).then((response) => {
			return response.text();
		}).then((div) => {
			div = div.substring(6, div.length - 6); // Remove <div>...</div>
			element.outerHTML = div;
		}).catch((error) => {
			onLoadInlineError(element, uuid);
			console.log(error);
		})
	});
}

window.addEventListener('load', onLoadAJAX, false);

function onLoadInlineError(element, uuid) {
	fetch(`/ajax/onloaderror?uuid=${uuid}`,
	{
		method: "get",
		headers: {
			"Content-Type": "application/x-www-form-urlencoded"
		}
	}).then((response) => {
		return response.text();
	}).then((div) => {
		div = div.substring(6, div.length - 6); // Remove <div>...</div>
		element.outerHTML = div;
	}).catch((error) => {
		element.outerHTML = "Failed to get error data";
		console.log(error);
	})
}
