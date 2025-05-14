package lto.manager.web.handlers.http.partial.loading;

import org.xmlet.htmlapifaster.Div;

import lto.manager.web.resource.CSS;

public class OnLoad {
	public static final String DATA_AJAX = "data-ajax";
	public static final String SUCCESS_CALLBACK_AJAX = "data-ajax-scb";
	public static final String ERROR_CALLBACK_AJAX = "data-ajax-ecb";

	public static Void spinner(Div<?> view, OnLoadOptions settings) {
		view
			.div()
				.attrClass(CSS.LOADING_CONTAINER)
				.of(d -> trigger(d, settings))
				.div().__() // Circle 1
				.div().__() // Circle 2
				.div().__() // Circle 3
				.div().__() // Circle 4
				.div().__() // Circle 5
			.__();
		return null;
	}

	public static Void spinnerNoAJAX(Div<?> view, final String extraClass) {
		view
			.div()
				.attrClass(CSS.LOADING_CONTAINER + extraClass)
				.div().__() // Circle 1
				.div().__() // Circle 2
				.div().__() // Circle 3
				.div().__() // Circle 4
				.div().__() // Circle 5
			.__();
		return null;
	}

	public static Void trigger(Div<?> view, OnLoadOptions settings) {
		view
			.addAttr(DATA_AJAX, settings.pathAJAX())
			.addAttr(SUCCESS_CALLBACK_AJAX, settings.successCallJS())
			.addAttr(ERROR_CALLBACK_AJAX, settings.errorCallJS());
		return null;
	}
}
