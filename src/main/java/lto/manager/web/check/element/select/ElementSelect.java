package lto.manager.web.check.element.select;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import org.xmlet.htmlapifaster.Div;
import org.xmlet.htmlapifaster.Element;
import org.xmlet.htmlapifaster.Option;
import org.xmlet.htmlapifaster.Select;

import lto.manager.web.check.element.FormElement;

public class ElementSelect extends FormElement {
	public static class ElementSelectOption {
		final String value;
		final String text;
		final boolean disabled;
		final boolean selected;
		Consumer<Option<?>> customAction;

		public ElementSelectOption(final String value, final String text, final boolean disabled, final boolean selected) {
			this.value = value;
			this.text = text;
			this.disabled = disabled;
			this.selected = selected;
		}

		public ElementSelectOption(final String value, final String text, final String currentSelected) {
			this.value = value;
			this.text = text;
			this.disabled = false;
			this.selected = value.equals(currentSelected);
		}

		public ElementSelectOption(final String value, final String text, final String currentSelected, Consumer<Option<?>> action) {
			this.value = value;
			this.text = text;
			this.customAction = action;
			this.disabled = false;
			this.selected = value.equals(currentSelected);
		}

		public String getValue() {
			return value;
		}

		public String getText() {
			return text;
		}

		public Consumer<Option<?>> getCustomAction() {
			if (customAction == null) {
				return (Option<?> option) -> {
					// Empty - do nothing
				};
			}
			return customAction;
		}
	}

	private final List<ElementSelectOption> options = new ArrayList<ElementSelectOption>();

	public ElementSelect() {
		super(FormElementType.SELECT);
	}

	public static ElementSelect of() {
		return new ElementSelect();
	}

	public ElementSelect withOptions(List<ElementSelectOption> options) {
		this.options.addAll(options);
		return this;
	}

	public List<ElementSelectOption> getOptions() {
		return options;
	}

	public ElementSelect withValue(final String value) {
		operations.put(FormOperation.Value, (Element<?, ?> e) -> {
			((Option<?>) e).attrValue(value);
		});
		values.put(FormOperation.Class, value);
		return this;
	}

	public String getValue() {
		return values.get(FormOperation.Value);
	}

	public FormElement withName(final String name) {
		operations.put(FormOperation.Name, (Element<?, ?> e) -> {
			((Select<?>) e).attrName(name);
		});
		values.put(FormOperation.Name, name);
		return this;
	}

	public String getName() {
		return values.get(FormOperation.Name);
	}

	public ElementSelect withCurrentSelected(final String currentSelected) {
		values.put(FormOperation.InputSelected, currentSelected);
		return this;
	}

	public String getCurrentelected() {
		return values.get(FormOperation.InputSelected);
	}

	public ElementSelect withDisabledDefault(final String currentValue) {
		if (currentValue == null || "null".equals(currentValue)) {
			options.add(new ElementSelectOption(null, "Select", true, true));
		}
		return this;
	}

	public ElementSelect withDisabledDefault() {
		options.add(new ElementSelectOption(null, "Select", true, false));
		return this;
	}

	public String getDisabledDefault() {
		return null;
	}

	@Override
	public void render(Div<?> div) {
		// @formatter:off
		div
			.select()
				.of(s -> {
					for (final var op: getOperations()) {
						op.getValue().accept(s);
					}
					for (final ElementSelectOption opt : getOptions()) {
						s.option()
							.of(o -> opt.getCustomAction().accept(o))
							.attrValue(opt.getValue())
							.attrSelected(opt.selected)
							.attrDisabled(opt.disabled)
							.text(opt.getText())
						.__();
					}
				})
			.__();
		// @formatter:on
	}

}
