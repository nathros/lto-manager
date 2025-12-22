const FORM_ATT_PATH = "data-form";

function validateForm(form) {
	const validateURL = form.getAttribute(FORM_ATT_PATH);
	if (validateURL == null || form == null) {
		return;
	}
	ajaxFetch(buildFormURL(validateURL, form), form.parentElement);
}

