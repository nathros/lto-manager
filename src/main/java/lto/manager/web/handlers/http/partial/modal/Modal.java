package lto.manager.web.handlers.http.partial.modal;

import java.util.function.Consumer;

import org.xmlet.htmlapifaster.Dialog;
import org.xmlet.htmlapifaster.Element;

import lto.manager.web.resource.CSS;

public class Modal {
	public static void content(Element<?, ?> view, ModalOptions options, Consumer<Dialog<?>> consumer) {
		new Dialog<>(view)
			.attrId(options.id())
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
				consumer.accept(div);
			})
		.__(); // Dialog
	}

}
