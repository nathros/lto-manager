package lto.manager.web.check.element;

import org.xmlet.htmlapifaster.Element;
import org.xmlet.htmlapifaster.Input;

import lto.manager.web.check.FormValidator;

public abstract class ElementInput extends FormElement {

	public ElementInput(FormElementType type) {
		super(type);
	}

	public ElementInput(FormElementType type, FormValidator validator) {
		super(FormElementType.INPUT_TEXT, validator);
	}

	public FormElement withName(final String name) {
		operations.put(FormOperation.Name, (Element<?, ?> e) -> {
			((Input<?>) e).attrName(name);
		});
		values.put(FormOperation.Name, name);
		return this;
	}

	public FormElement withValue(final String value) {
		operations.put(FormOperation.Value, (Element<?, ?> e) -> {
			((Input<?>) e).attrValue(value);
		});
		values.put(FormOperation.Value, value);
		return this;
	}

	public ElementInput withMinLength(final Long min) {
		operations.put(FormOperation.MinLen, (Element<?, ?> e) -> {
			((Input<?>) e).attrMinlength(min);
		});
		values.put(FormOperation.MinLen, String.valueOf(min));
		return this;
	}

	public ElementInput withMaxLength(final Long max) {
		/*operations.put(FormOperation.MaxLen, (Element<?, ?> e) -> {
			((Input<?>) e).attrMinlength(max);
		});
		values.put(FormOperation.MaxLen, String.valueOf(max));*/
		getFormValidator().getOptions().valueMaxLength(max);
		return this;
	}
}
