package lto.manager.web.check.element.input;

import java.util.ArrayList;
import java.util.List;

import org.xmlet.htmlapifaster.Div;
import org.xmlet.htmlapifaster.EnumTypeInputType;

import lto.manager.web.resource.CSS;

public class ElementInputRadio extends ElementInput {
	private final String name;

	public static class ElementRadioOption {
		final String value;
		final String label;
		final boolean selected;

		public ElementRadioOption(final String value, final String label, final boolean selected) {
			this.value = value;
			this.label = label;
			this.selected = selected;
		}
	}

	private final List<ElementRadioOption> options = new ArrayList<ElementRadioOption>();

	public ElementInputRadio(final String name) {
		super(FormElementType.INPUT_RADIO);
		this.name = name;
	}

	public static ElementInputRadio of(final String name) {
		return new ElementInputRadio(name);
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
		// @formatter:off
		div
			.div()
				.attrClass(CSS.RADIO_GROUP_CONTAINER)
				.of(d -> {
					int index = 0;
					for (final ElementRadioOption radio : options) {
						d.input()
							.attrType(EnumTypeInputType.RADIO)
							.attrId(name + index)
							.attrName(name)
							.attrValue(radio.value)
							.attrChecked(radio.selected)
						.__() // input
						.label()
							.attrFor(name + index)
							.text(radio.label)
						.__(); // label
						index++;
					}
				})
			.__(); // div RADIO_GROUP_CONTAINER
		// @formatter:on
	}

}
