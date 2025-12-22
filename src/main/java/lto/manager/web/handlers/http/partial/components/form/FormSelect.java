package lto.manager.web.handlers.http.partial.components.form;

import org.xmlet.htmlapifaster.Form;

import lto.manager.web.check.element.ElementSelect;
import lto.manager.web.check.element.ElementSelect.ElementSelectOption;
import lto.manager.web.check.element.FormElement;
import lto.manager.web.handlers.http.templates.models.BodyModel;
import lto.manager.web.resource.HTML;

public class FormSelect {
	public static Void content(Form<?> view, BodyModel model, FormElement element) {
		ElementSelect el = (ElementSelect) element;

		// @formatter:off
		if (el.getLabel() != null) {
			view.b().text(el.getLabel()).__();
		}
		view
			.select()
				.of(s -> HTML.selectId(s, el.getId()))
				.of(s -> HTML.selectName(s, el.getName()))
				.of(s -> {
					if (el.getDisabledDefault() != null) {
						s.option()
							.text("Select")
							.of(o -> HTML.option(o, true, true))
						.__();
						if (el.getCurrentelected() == null) {

						} else {

						}
					}
					for (final ElementSelectOption option: el.getOptions()) {
						s.option()
							.attrValue(option.getValue())
							.text(option.getText())
						.__();
					}
				})
			.__(); // select
		return null;
		// @formatter:on
	}

}
