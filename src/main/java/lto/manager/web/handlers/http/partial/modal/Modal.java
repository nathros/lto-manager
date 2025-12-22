package lto.manager.web.handlers.http.partial.modal;

import java.util.List;
import java.util.function.Consumer;

import org.xmlet.htmlapifaster.Dialog;
import org.xmlet.htmlapifaster.Element;

import lto.manager.web.check.FormDefinition;
import lto.manager.web.check.element.FormElement;
import lto.manager.web.check.element.FormElement.FormElementType;
import lto.manager.web.resource.CSS;

public class Modal {
	public static void content(Element<?, ?> view, ModalOptions options, Consumer<Dialog<?>> consumer) {
		new Dialog<>(view)
			.attrId(options.id())
			.of(d -> {
				var innerDiv =
					d.div()
						.span().text(options.title()).__();

				if (options.enableCross())
				{
					d
						.button()
							.attrClass(CSS.BUTTON)
							.attrOnclick("document.getElementById('" + options.id() + "').close()")
							.text("X")
						.__();
				}
				innerDiv.__(); // Close innerDiv

				consumer.accept(d);
			})
		.__(); // Dialog
	}

	public static void content(Element<?, ?> view, ModalOptions options, FormDefinition fd) {
		new Dialog<>(view)
			.attrId(options.id())
			.div()
				.attrClass(CSS.FORMS_CONTAINER)
				.of(div -> {
					if (options.enableCross())
					{
						div
							.div()
								.attrStyle("text-align:end")
								.button()
									.attrClass(CSS.BUTTON)
									.attrOnclick("document.getElementById('" + options.id() + "').close()")
									.text("X")
								.__()
							.__();
					}
					final List<FormElement> elements = fd.getElements();
					elements.forEach(e -> {
						if (e.getLabel() != null) {
							div.b().text(e.getLabel()).__();
						}
						switch (e.getFormElementType()) {
						case FormElementType.INPUT_TEXT:
							div
							.input()
								.of(i -> {
									//if (e.get)
								})
							.__();
							break;

						default:
							throw new IllegalArgumentException("Unexpected value: " + e.getFormElementType());
						}
					});

				})
			.__() // FORMS_CONTAINER
		.__(); // Dialog
	}

}
