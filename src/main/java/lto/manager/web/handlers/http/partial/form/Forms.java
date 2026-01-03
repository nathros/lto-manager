package lto.manager.web.handlers.http.partial.form;

import java.util.List;

import org.xmlet.htmlapifaster.Div;

import lto.manager.common.Optional;
import lto.manager.web.check.FormDefinition;
import lto.manager.web.check.element.FormElement;
import lto.manager.web.handlers.http.partial.inlinemessage.InlineMessage;
import lto.manager.web.handlers.http.templates.models.BodyModel;
import lto.manager.web.resource.Attribute;
import lto.manager.web.resource.CSS;
import lto.manager.web.resource.JS;

public class Forms {
	private static final String VALIDATE_SUBMIT = "__submit";
	private static final String VALIDATE_SINGLE = "__single";
	private static final String VALIDATE_SINGLE_KEY = "__k";

	public static void content(Div<?> view, FormDefinition fd) {
		final BodyModel model = fd.getBodyModel();
		final boolean validateSingleField = model.getQueryModel().getChecked(VALIDATE_SINGLE, false);
		if (validateSingleField) {
			contentSingle(view, fd, model);
			return;
		}

		final boolean isSubmit = model.getQueryModel().getChecked(VALIDATE_SUBMIT, false);
		final boolean hasValidationErrors = fd.hasValidationErrors();

		final Optional<String> submitMessage = Optional.empty();
		if (isSubmit && !hasValidationErrors) {
			try {
				fd.getSubmitFunction().apply(fd, model);
			} catch (Exception e) {
				submitMessage.set(e.getMessage());
			}
		}

		final List<FormElement> elements = fd.getElements();
		final List<FormElement> buttons = fd.getButtons();

		// @formatter:off
		view.form()
			.attrId(fd.getId())
			.addAttr(Attribute.FORM_DATA, fd.getAJAXPath()) // Path to send form to validate
			.addAttr(Attribute.FORM_REPLACE, fd.getOnReplace()) // JavaScript to run on replace
			.attrOnchange(JS.formValidate())
			.attrOnkeyup(JS.formValidate())

			.div()
				.attrClass(CSS.FORMS_CONTAINER)
				.of(d -> {
					for (final FormElement fe : elements) {
						for (final var pre : fe.getPreOperations()) {
							pre.getValue().accept(view);
						}
						fe.render(d);
					}
				})
			.__() // div FORMS_CONTAINER

			.of(f -> {
				if (isSubmit) {
					if (submitMessage.isPresent()) {
						f.div().of(d -> InlineMessage.contentGenericError(d, submitMessage.get()));
					} else {
						f.div().of(d -> InlineMessage.contentGenericOK(d, "Success"));
					}
				}
				if (buttons.size() > 0) { // Buttons at bottom of form
					f.div().attrClass(CSS.FORMS_MODAL_CONTAINER)
						.of(d -> {
							for (final FormElement btn : buttons) {
								btn.render(d);
							}
						})
					.__(); // div FORMS_MODAL_CONTAINER
				}
			})
		.__(); // form
		// @formatter:on
	}

	private static void contentSingle(Div<?> view, FormDefinition fd, BodyModel model) {
		final String key = model.getQueryModel().getString(VALIDATE_SINGLE_KEY);
		if (key == null) {
			throw new IllegalArgumentException(VALIDATE_SINGLE_KEY + " is missing");
		}
		final List<FormElement> elements = fd.getElements();
		for (final FormElement fe : elements) {
			final String id = fe.getId();
			if (key.equals(id)) {
				view.of(d -> fe.render(d));
				return;
			}
		}
		throw new IllegalArgumentException("Unable to find" + key);
	}
}
