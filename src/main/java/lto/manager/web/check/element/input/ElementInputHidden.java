package lto.manager.web.check.element.input;

import org.xmlet.htmlapifaster.Div;
import org.xmlet.htmlapifaster.EnumTypeInputType;
import lto.manager.web.check.FormValidator;
import lto.manager.web.check.FormValidator.ValidatorStatus;
import lto.manager.web.resource.CSS;

public class ElementInputHidden extends ElementInput {
	public ElementInputHidden() {
		super(FormElementType.INPUT_HIDDEN, FormValidator.ofDefault());
	}

	public ElementInputHidden(FormValidator validator) {
		super(FormElementType.INPUT_HIDDEN, validator);
	}

	public static ElementInputHidden of() {
		return new ElementInputHidden();
	}

	public static ElementInputHidden of(FormValidator validator) {
		return new ElementInputHidden(validator);
	}

	@Override
	public void validate() {
		validatorStatus = ValidatorStatus.emptyOK();
	}

	@Override
	public void render(Div<?> div) {
		// @formatter:off
		div
			.attrClass(CSS.TEXT_INPUT_CONTAINER)
			.input()
				.attrType(EnumTypeInputType.HIDDEN)
				.of(i -> {
					for (final var op: getOperations()) {
						op.getValue().accept(i);
					}
				})
			.__(); // input
		// @formatter:on
	}

}
