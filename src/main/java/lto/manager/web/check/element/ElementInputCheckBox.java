package lto.manager.web.check.element;

import org.xmlet.htmlapifaster.Div;
import org.xmlet.htmlapifaster.EnumTypeInputType;

import lto.manager.web.check.FormValidator;

public class ElementInputCheckBox extends ElementInput {
	private final boolean checked;

	public ElementInputCheckBox(final boolean checked) {
		super(FormElementType.INPUT_CHECKBOX);
		this.checked = checked;
	}

	public ElementInputCheckBox(FormValidator validator, final boolean checked) {
		super(FormElementType.INPUT_CHECKBOX, validator);
		this.checked = checked;
	}

	public static ElementInputCheckBox of(final boolean checked) {
		return new ElementInputCheckBox(checked);
	}

	@Override
	public void render(Div<?> div) {
		// @formatter:off
		div
			.input()
				.attrType(EnumTypeInputType.CHECKBOX)
				.of(i -> {
					for (final var op: getOperations()) {
						op.getValue().accept(i);
					}
				})
				.attrChecked(checked)
			.__(); // input
		// @formatter:on
	}

}
