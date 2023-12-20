const barcodeForm = document.getElementById("barcode-form");
const barcodePreview = document.getElementById("barcode-preview");
const inputs = barcodeForm.getElementsByTagName("input");
const selects = barcodeForm.getElementsByTagName("select");

function getBarcodeFormParams(preview) {
	const formData = new FormData(barcodeForm);
	const params = new URLSearchParams(formData);
	if (preview) {
		const preCount = document.getElementById("preview-count").value;
		const preScale = document.getElementById("preview-scale").value;
		params.append("preview-count", preCount);
		params.append("preview-scale", preScale);
	}
	return params;
}

function onBarcodeInputChange(event) {
	console.log(event)
	const params = getBarcodeFormParams(true);
	fetch(`/ajax/generate/lto/label/html?` + params.toString(),
	{
		method: "GET",
		signal: AbortSignal.timeout(3000)
	}).then((response) => {
		return {"text": response.text(), "status" : response.status};
	}).then((response) => {
		if (response.status == 500) {
			document.activeElement.classList.add("error");
		} else {
			for (let index = 0; index < inputs.length; index++) {
				inputs[index].classList.remove("error");
			}
		}
		return response.text;
	}).then((text) => {
		barcodePreview.innerHTML = text;
	}).catch((error) => {
		showToast(Toast.Error, `Failed to update preview: ${error}`, -1, undefined, false);
	});
}

function generateBarcode(href) {
	const params = getBarcodeFormParams(false);
	Object.assign(document.createElement('a'), {
		target: '_blank',
		rel: 'noopener noreferrer',
		href: `${href}?${params.toString()}`,
		download: "lto-label"
	}).click();
}

if ((barcodePreview != null) && (barcodeForm != null)) {
	for (let index = 0; index < inputs.length; index++) {
		const input = inputs[index];
		input.onchange = (event) => { onBarcodeInputChange(event); }; // TODO incorrectly triggered on leave
		input.onkeyup = (event) => { onBarcodeInputChange(event); }
	}
	for (let index = 0; index < selects.length; index++) {
		selects[index].onchange = (event) => { onBarcodeInputChange(event); };
	}
	onBarcodeInputChange();
}