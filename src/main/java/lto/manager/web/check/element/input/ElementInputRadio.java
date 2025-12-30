package lto.manager.web.check.element.input;

import java.util.ArrayList;
import java.util.List;

import org.xmlet.htmlapifaster.Div;
import org.xmlet.htmlapifaster.EnumTypeInputType;

import lto.manager.web.check.FormValidator;

public class ElementInputRadio extends ElementInput {
	public static class ElementRadioOption {
		final String value;
		final String text;

		public ElementRadioOption(final String value, final String text) {
			this.value = value;
			this.text = text;
		}
	}

	private final List<ElementRadioOption> options = new ArrayList<ElementRadioOption>();

	public ElementInputRadio() {
		super(FormElementType.INPUT_RADIO);
	}

	public ElementInputRadio(FormValidator validator) {
		super(FormElementType.INPUT_RADIO, validator);
	}

	public static ElementInputRadio of() {
		return new ElementInputRadio();
	}

	public static ElementInputRadio of(FormValidator validator) {
		return new ElementInputRadio(validator);
	}

	public ElementInputRadio withOptions(List<ElementRadioOption> options) {
		this.options.addAll(options);
		return this;
	}

	public List<ElementRadioOption> getOptions() {
		return options;
	}

	@Override
	public void render(Div<?> div) {
		//for (final var opt : eRadio.getOptions()) {}

		// @formatter:off
		div
			.input()
				.attrType(EnumTypeInputType.RADIO)
				.of(i -> {
					for (final var op: getOperations()) {
						op.getValue().accept(i);
					}
				})
			.__(); // input
		// @formatter:on
	}

}
