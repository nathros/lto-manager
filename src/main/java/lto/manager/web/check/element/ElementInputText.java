package lto.manager.web.check.element;

import org.xmlet.htmlapifaster.Div;
import org.xmlet.htmlapifaster.EnumTypeInputType;

import lto.manager.web.check.FormValidator;

public class ElementInputText extends ElementInput {
	public ElementInputText() {
		super(FormElementType.INPUT_TEXT);
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
		// @formatter:off
		div
			.input()
				.attrType(EnumTypeInputType.TEXT)
				.of(i -> {
					for (final var op: getOperations()) {
						op.getValue().accept(i);
					}
				})
			.__(); // input
		// @formatter:on
	}

}
