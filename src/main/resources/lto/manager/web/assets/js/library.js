const barcodeForm = document.getElementById("barcode-form");
const barcodePreview = document.getElementById("barcode-preview");

function onBarcodeInputChange() {
	const formData = new FormData(barcodeForm);
  	const params = new URLSearchParams(formData);
  	console.log(params.toString())
	fetch(`/ajax/generate/lto/label/html?` + params.toString(),
	{
		method: "GET",
		signal: AbortSignal.timeout(3000)
	}).then((response) => {
		return response.text();
	}).then((text) => {
		barcodePreview.innerHTML = text;
	}).catch((error) => {
		showToast(Toast.Error, `Failed to update preview: ${error}`, -1, undefined, false);
	});
}


if ((barcodePreview != null) && (barcodeForm != null)) {
	const inputs = barcodeForm.getElementsByTagName("input");
	console.log(inputs)
	for (let index = 0; index < inputs.length; index++) {
		const input = inputs[index];
		//input.onchange = (event) => {
		input.onkeyup = (event) => {
			console.log(event);
			onBarcodeInputChange();
		};
	}
	onBarcodeInputChange();
	
}