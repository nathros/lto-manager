package lto.manager.web.check;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

import lto.manager.web.check.element.FormElement;
import lto.manager.web.handlers.http.templates.models.BodyModel;

public class FormDefinition {
	private final BiFunction<FormDefinition, BodyModel, Void> submitFunction;
	private final BodyModel model;

	private List<FormElement> elements = new ArrayList<FormElement>();
	private List<FormElement> buttons = new ArrayList<FormElement>();
	private String ajaxPath;
	private String id;
	private String onReplace;

	public FormDefinition(final BodyModel model, final BiFunction<FormDefinition, BodyModel, Void> submitFunction) {
		this.model = model;
		this.submitFunction = submitFunction;
	}

	public static FormDefinition of(final BodyModel model,
			final BiFunction<FormDefinition, BodyModel, Void> submitFunction, List<FormElement> elements,
			List<FormElement> buttons) {
		return new FormDefinition(model, submitFunction).withElements(elements).withButtons(buttons);
	}

	public FormDefinition withElement(FormElement element) {
		element.validate();
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

	public FormDefinition withId(final String id) {
		this.id = id;
		return this;
	}

	public String getId() {
		return id;
	}

	public FormDefinition withOnReplace(final String replaceJS) {
		this.onReplace = replaceJS;
		return this;
	}

	public String getOnReplace() {
		return onReplace;
	}

	public BodyModel getBodyModel() {
		return model;
	}

	public BiFunction<FormDefinition, BodyModel, Void> getSubmitFunction() {
		return submitFunction;
	}

	public boolean hasValidationErrors() {
		return elements.stream().filter(e -> e.getFormValidatorStatus().getStatus() != CheckStatusType.OK).findFirst()
				.isPresent();
	}

}
