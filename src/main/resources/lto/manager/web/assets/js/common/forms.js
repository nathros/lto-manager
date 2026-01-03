const FORM_ATT_PATH = "data-form";
const FORM_ATT_REPLACE = "data-replace";
const VALIDATE_SUBMIT = "__submit=on";
const VALIDATE_SINGLE = "__single=on";
const VALIDATE_SINGLE_KEY = "__k";

function validateForm(form, event) {
	const validateURL = form.getAttribute(FORM_ATT_PATH);
	if (validateURL == null || form == null) {
		return;
	}
	validateInput(event.target, form, validateURL);
}

function submitForm(formId) {
	let form = document.getElementById(formId);
	const formURL = getFormURL(form);
	const formQuery = buildFormQuery(form);
	const fetchURL = `${formURL}?${VALIDATE_SUBMIT}&${formQuery}`;
	ajaxFetch(fetchURL, form, () => {
		if (checkAnyFormSuccess(form)) {
			window.location.reload(); // Success on submit refresh with new data
		}
	}, () => {}, true);
	return false; // Stop submit
}

function fetchForm(formId, additionalQuery, onCompleteFn) {
	let form = document.getElementById(formId);
	const formURL = getFormURL(form);
	const fetchURL = `${formURL}?&${additionalQuery}`;
	ajaxFetch(fetchURL, form, () => {
		executeFormReplaceScript(form);
		if (onCompleteFn) {
			onCompleteFn();
		}
	}, () => {}, true);
	return false; // Stop submit
}

function updateFormStatus(form) {
	const submitButtons = form.querySelectorAll("button[type=submit]");
	if (submitButtons.length > 0) {
		const errors = Array.from(form.getElementsByClassName("error")).filter(e => !e.classList.contains("inline-message"));
		const disabled = errors.length != 0;
		submitButtons.forEach(btn => btn.disabled = disabled);
	}
}

function checkAnyFormSuccess(form) {
	const ok = Array.from(form.getElementsByClassName("inline-message ok"));
	return ok.length > 0;
}

function getFormURL(form) {
	return form ? form.getAttribute(FORM_ATT_PATH) : null;
}

function executeFormReplaceScript(form) {
	const script = form ? form.getAttribute(FORM_ATT_REPLACE) : null;
	if (script) {
		eval(script);
	}
}

async function validateInput(element, form, validateURL /* event */) {
	let replaceElement = true;
	if (element.tagName == "INPUT") {
		if (element.type == "text") {
			replaceElement = false; // Cannot replace text input as will lose focus and caret position
		} else if (element.type == "checkbox" || element.type == "radio") {
			return; // Do not validate checkboxes or radios
		}
	}
	const fetchURL = `${validateURL}?${VALIDATE_SINGLE}&${VALIDATE_SINGLE_KEY}=${element.id}&${element.id}=${encodeURIComponent(element.value)}`;
	const newInputContainer = await ajaxFetchWait(fetchURL, true);
	const existingInputContainer = element.parentElement;
	if (replaceElement) {
		updateElement(existingInputContainer, newInputContainer);
	} else {
		existingInputContainer.classList = newInputContainer.classList
		const existingMessage = existingInputContainer.getElementsByClassName("text-input-message")[0];
		const newMessage = newInputContainer.getElementsByClassName("text-input-message")[0];
		if (existingMessage && newMessage) {
			existingMessage.innerHTML = newMessage.innerHTML;
		}
	}
	updateFormStatus(form);
}

window.addEventListener("load", () => {
	const forms = document.querySelectorAll("form");
	forms.forEach(f => updateFormStatus(f));
}, false);

