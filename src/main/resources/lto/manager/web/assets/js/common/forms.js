const FORM_ATT_PATH = "data-form";
const VALIDATE_SINGLE = "__single=on";
const VALIDATE_SINGLE_KEY = "__k";

function validateForm(form) {
	const validateURL = form.getAttribute(FORM_ATT_PATH);
	if (validateURL == null || form == null) {
		return;
	}
	// TODO not used?
	//ajaxFetch(buildFormURL(validateURL, form), form.parentElement);
}

function findFormAttribute(baseElement) {
	let findForm = baseElement;
	let validateURL = null;
	while (true) { // Work way up until find a <form>
		findForm = findForm.parentNode;
		if (findForm == null) {
			break; // Reached end without find
		}
		if (findForm.tagName == "FORM") {
			validateURL = findForm.getAttribute(FORM_ATT_PATH);
			break;
		}
	}
	return validateURL;
}

async function validateTextInput(input, /*event*/) {
	const validateURL = findFormAttribute(input);
	if (validateURL == null) {
		return;
	}
	const fetchURL = `${validateURL}?${VALIDATE_SINGLE}&${VALIDATE_SINGLE_KEY}=${input.id}&${input.id}=${input.value}`;
	const newInputContainer = await ajaxFetchWait(fetchURL, true);
	const existingInputContainer = input.parentElement;
	// Cannot just replace container as input will lose focus and caret position
	existingInputContainer.classList = newInputContainer.classList

	const existingMessage = existingInputContainer.getElementsByClassName("text-input-message")[0];
	const newMessage = newInputContainer.getElementsByClassName("text-input-message")[0];
	if (existingMessage && newMessage) {
		existingMessage.innerText = newMessage.innerText;
	}
}
