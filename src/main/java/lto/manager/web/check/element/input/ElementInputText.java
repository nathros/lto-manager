package lto.manager.web.check.element.input;

import org.xmlet.htmlapifaster.Div;
import org.xmlet.htmlapifaster.EnumTypeInputType;
import lto.manager.web.check.CheckStatusType;
import lto.manager.web.check.FormValidator;
import lto.manager.web.resource.CSS;

public class ElementInputText extends ElementInput {
	public ElementInputText() {
		super(FormElementType.INPUT_TEXT, FormValidator.ofDefault());
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
	public void validate() {
		validatorStatus = getFormValidator().validateText(values.get(FormOperation.Value), true);
	}

	@Override
	public void render(Div<?> div) {
		// @formatter:off
		div
			.div()
				.attrClass(CSS.TEXT_INPUT_CONTAINER + (validatorStatus.getStatus() == CheckStatusType.OK ? "" : "error"))
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
					.raw(validatorStatus.getValidatorMessage())
				.__() // div
				.div()
					.attrClass(CSS.TEXT_INPUT_ICON)
				.__() // div
			.__(); // div TEXT_INPUT_CONTAINER
		// @formatter:on
	}

}
