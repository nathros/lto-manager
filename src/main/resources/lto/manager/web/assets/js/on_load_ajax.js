function onLoadAJAX() {
	const AJAX_ATT = "data-ajax";
	let elements = document.querySelectorAll(`[${AJAX_ATT}]`);
	elements.forEach(element => {
		const url = element.getAttribute(AJAX_ATT);
		fetch(url,
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
			console.log(error);
		})
	});
}

window.addEventListener('load', onLoadAJAX, false);