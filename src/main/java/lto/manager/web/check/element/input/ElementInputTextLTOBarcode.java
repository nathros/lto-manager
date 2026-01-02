package lto.manager.web.check.element.input;

import org.xmlet.htmlapifaster.Div;
import org.xmlet.htmlapifaster.EnumTypeInputType;

import lto.manager.web.check.CheckStatusType;
import lto.manager.web.check.FormValidator;
import lto.manager.web.resource.CSS;

public class ElementInputTextLTOBarcode extends ElementInputText {
	public ElementInputTextLTOBarcode() {
	}

	public ElementInputTextLTOBarcode(FormValidator validator) {
		super(validator);
	}

	public static ElementInputTextLTOBarcode of() {
		return new ElementInputTextLTOBarcode();
	}

	public static ElementInputTextLTOBarcode of(FormValidator validator) {
		return new ElementInputTextLTOBarcode(validator);
	}

	@Override
	public void render(Div<?> div) {
		// Same as ElementInputText with extra input

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
				.input()
					.attrId("des") // See: add_tape.js
					.attrStyle("width:1.5rem;margin-left:var(--padding);text-align:center") // TODO ad to special.css
					.attrDisabled(true)
				.__()
			.__(); // div
		// @formatter:on
	}

}
