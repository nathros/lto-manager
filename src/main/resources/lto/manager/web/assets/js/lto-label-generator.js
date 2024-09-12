const barcodeForm = document.getElementById("barcode-form");
const barcodePreview = document.getElementById("barcode-preview");
const inputs = barcodeForm.getElementsByTagName("input");
const selects = barcodeForm.getElementsByTagName("select");

let presetModal = document.getElementById('modal-preset'); // Can be updated

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
	fetch(`/ajax/generate/lto/label/html/?${params.toString()}`,
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

function refreshPresetList() {
	ajaxFetch("/ajax/ltolabelpreset/", presetModal.parentElement, true, () => {
		presetModal = document.getElementById('modal-preset'); // Original has been replaced by ajaxFetch()
	});
}

function showPresetModal() {
	presetModal.getElementsByTagName("input")[0].value = "";
	presetModal.showModal();
	presetModal.getElementsByTagName("input")[0].focus();
}

function hidePresetModal() {
	presetModal.close();
	document.getElementById("preset-error").style.display = "none";
}

function presetNameInputChange(event) {
	if (event.key == "Enter") {
		addPreset();
	}
}

function addPreset() {
	const config = getBarcodeFormParams(true);
	const setName = document.getElementById("preset-name").value;
	let status = 0;
	fetch(`/api/ltolabelpreset/?op=add&name=${setName}&config=${encodeURIComponent(config.toString())}`,
	{
		method: "GET",
		signal: AbortSignal.timeout(3000)
	}).then((response) => {
		status = response.status;
		return response.json();
	}).then((json) => {
		if (status != 200) {
			throw json["message"];
		}
		refreshPresetList();
		hidePresetModal();
		showToast(Toast.Good, `Added new preset: ${setName}`, 2000, undefined, false);
	}).catch((error) => {
		const e = document.getElementById("preset-error");
		e.style.display = "";
		inlineMessageUpdateMessage(e, error);
	});
}

function deletePreset(event, name) {
	event.stopPropagation();
	let status = 0;
	fetch(`/api/ltolabelpreset/?op=delete&name=${name}`,
	{
		method: "GET",
		signal: AbortSignal.timeout(3000)
	}).then((response) => {
		status = response.status;
		return response.json();
	}).then((json) => {
		if (status != 200) {
			throw json["message"];
		}
		event.target.parentElement.remove();
		showToast(Toast.Good, `Deleted preset: ${name}`, 2000, undefined, false);
	}).catch((error) => {
		showToast(Toast.Error, `Failed to delete preset: ${error}`, -1, undefined, false);
	});
}

function setPreset(name) {
	let status = 0;
	fetch(`/api/ltolabelpreset/?op=get&name=${name}`,
	{
		method: "GET",
		signal: AbortSignal.timeout(3000)
	}).then((response) => {
		status = response.status;
		return response.json();
	}).then((json) => {
		if (status != 200) {
			throw json["message"];
		}
		const config = new URLSearchParams(json["message"]);
		// Apply key value kairs to inputs and selects
		config.forEach((value, name) => {
			for (let i of inputs) {
				if (i.id) {
					if (i.id == name) {
						i.value = value;
						break;
					}
				} else if (i.name == name) {
					i.value = value;
					break;
				}
			}
		});
		config.forEach((value, name) => {
			for (let i of selects) {
				if (i.id) {
					if (i.id == name) {
						i.value = value;
						break;
					}
				} else if (i.name == name) {
					i.value = value;
					break;
				}
			}
		});
		onBarcodeInputChange();
	}).catch((error) => {
		showToast(Toast.Error, `Failed to set preset: ${error}`, -1, undefined, false);
	});
}

