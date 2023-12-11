const barcodeForm = document.getElementById("barcode-form");
const barcodePreview = document.getElementById("barcode-preview");
const inputs = barcodeForm.getElementsByTagName("input");

function onBarcodeInputChange() {
	const formData = new FormData(barcodeForm);
	console.log(formData);
	//formData.delete()
  	const params = new URLSearchParams(formData);
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

if ((barcodePreview != null) && (barcodeForm != null)) {
	console.log(inputs)
	for (let index = 0; index < inputs.length; index++) {
		const input = inputs[index];
		input.onchange = (event) => {
			console.log(`onchange ${event}`);
			onBarcodeInputChange();
		};
		input.onkeyup = (event) => {
			console.log(`onkeyup ${event}`);
			onBarcodeInputChange();
		}
	}
	onBarcodeInputChange();
}