package lto.manager.web.check.element.input;

import org.xmlet.htmlapifaster.Div;
import org.xmlet.htmlapifaster.EnumTypeInputType;
import lto.manager.web.check.CheckStatusType;
import lto.manager.web.check.FormValidator;
import lto.manager.web.check.FormValidator.ValidatorStatus;
import lto.manager.web.resource.CSS;
import lto.manager.web.resource.JS;

public class ElementInputText extends ElementInput {
	public ElementInputText() {
		super(FormElementType.INPUT_TEXT, FormValidator.ofDefault());
		withOnKeyUpJS(JS.formValidateTextInput());
	}

	public ElementInputText(FormValidator validator) {
		super(FormElementType.INPUT_TEXT, validator);
	}

	public static ElementInputText of() {
		return new ElementInputText();
	}

	public static ElementInputText of(FormValidator validator) {
		return new ElementInputText(validator);
	}

	@Override
	public void render(Div<?> div) {
		final FormValidator validator = getFormValidator();
		final ValidatorStatus status = validator == null ? ValidatorStatus.emptyOK()
				: validator.validateText(values.get(FormOperation.Value), true);

		// @formatter:off
		div
			.div()
				.attrClass(CSS.TEXT_INPUT_CONTAINER + (status.getStatus() == CheckStatusType.OK ? "" : "error"))
				.input()
					.attrType(EnumTypeInputType.TEXT)
					.of(i -> {
						for (final var op: getOperations()) {
							op.getValue().accept(i);
						}
					})
				.__() // input
				.div()
					.attrClass(CSS.TEXT_INPUT_MESSAGE)
					.text(status.getValidatorMessage())
				.__() // div
				.div()
					.attrClass(CSS.TEXT_INPUT_ICON)
				.__() // div
			.__(); // div
		// @formatter:on
	}

}
