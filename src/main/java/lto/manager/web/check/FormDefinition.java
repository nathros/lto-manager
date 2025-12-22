package lto.manager.web.check;

import java.util.ArrayList;
import java.util.List;

import lto.manager.web.check.element.FormElement;

public class FormDefinition {
	private List<FormElement> elements = new ArrayList<FormElement>();
	private List<FormElement> buttons = new ArrayList<FormElement>();
	private String ajaxPath;

	public static FormDefinition of(List<FormElement> elements, List<FormElement> buttons) {
		return new FormDefinition().withElements(elements).withButtons(buttons);
	}

	public FormDefinition withElement(FormElement element) {
		elements.add(element);
		return this;
	}

	public FormDefinition withElements(List<FormElement> elements) {
		this.elements.addAll(elements);
		return this;
	}

	public final List<FormElement> getElements() {
		return elements;
	}

	public FormDefinition withButton(FormElement button) {
		buttons.add(button);
		return this;
	}

	public FormDefinition withButtons(List<FormElement> buttons) {
		buttons.addAll(buttons);
		return this;
	}

	public final List<FormElement> getButtons() {
		return buttons;
	}

	public FormDefinition withAJAXPath(final String url) {
		this.ajaxPath = url;
		return this;
	}

	public String getAJAXPath() {
		return ajaxPath;
	}

}
