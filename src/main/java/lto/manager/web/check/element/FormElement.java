package lto.manager.web.check.element;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Consumer;

import org.xmlet.htmlapifaster.Div;
import org.xmlet.htmlapifaster.Element;
import org.xmlet.htmlapifaster.GlobalAttributes;
import org.xmlet.htmlapifaster.Label;

import lto.manager.web.check.FormValidator;

public abstract class FormElement {
	public static enum FormElementType {
		INPUT_TEXT, INPUT_PASSWORD, INPUT_CHECKBOX, INPUT_RADIO, SELECT, BUTTON, BUTTON_ICON
	}

	public static enum FormOperation {
		Id, Class, Label, Value, Name, InputSelected, MinLen, MaxLen, Text, OnClick, OnKeyDown, OnKeyUp
	};

	private final FormElementType type;
	private final FormValidator validator;
	protected Map<FormOperation, Consumer<Element<?, ?>>> preOperations = new LinkedHashMap<FormOperation, Consumer<Element<?, ?>>>();
	protected Map<FormOperation, Consumer<Element<?, ?>>> operations = new LinkedHashMap<FormOperation, Consumer<Element<?, ?>>>();

	protected Map<FormOperation, String> values = new LinkedHashMap<FormOperation, String>();

	private String text; // Text attributes should always be applied last

	public FormElement(FormElementType type) {
		this.type = type;
		this.validator = null;
	}

	public FormElement(FormElementType type, FormValidator validator) {
		this.type = type;
		this.validator = validator;
	}

	public FormElementType getFormElementType() {
		return type;
	}

	public FormValidator getFormValidator() {
		return validator;
	}

	public FormElement withId(final String id) {
		operations.put(FormOperation.Id, (Element<?, ?> e) -> {
			((GlobalAttributes<?, ?>) e).attrId(id);
		});
		values.put(FormOperation.Id, id);
		return this;
	}

	public String getId() {
		return values.get(FormOperation.Id);
	}

	public FormElement withLabel(final String label) {
		preOperations.put(FormOperation.Label, (Element<?, ?> parent) -> {
			new Label<>(parent).of(l -> {
				final String name = values.get(FormOperation.Name);
				if (name != null) {
					l.attrFor(name);
				}
			}).text(label).__();
		});
		values.put(FormOperation.Label, label);
		return this;
	}

	public String getLabel() {
		return values.get(FormOperation.Label);
	}

	public FormElement withText(final String text) { // This should be last
		this.text = text;
		return this;
	}

	public String getText() {
		return text;
	}

	public FormElement withOnClickJS(String onClick) {
		operations.put(FormOperation.OnClick, (Element<?, ?> e) -> {
			((GlobalAttributes<?, ?>) e).attrOnclick(onClick);
		});
		return this;
	}

	public FormElement withOnKeyDownJS(String onKeyDown) {
		operations.put(FormOperation.OnClick, (Element<?, ?> e) -> {
			((GlobalAttributes<?, ?>) e).attrOnkeydown(onKeyDown);
		});
		return this;
	}

	public FormElement withOnKeyUpJS(String onKeyUp) {
		operations.put(FormOperation.OnKeyUp, (Element<?, ?> e) -> {
			((GlobalAttributes<?, ?>) e).attrOnkeyup(onKeyUp);
		});
		return this;
	}

	public Set<Entry<FormOperation, Consumer<Element<?, ?>>>> getOperations() {
		return operations.entrySet();
	}

	public Set<Entry<FormOperation, Consumer<Element<?, ?>>>> getPreOperations() {
		return preOperations.entrySet();
	}

	public abstract void render(Div<?> div);

}
