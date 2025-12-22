package lto.manager.web.handlers.http.partial.loading;

import org.xmlet.htmlapifaster.Div;

import lto.manager.web.resource.Attribute;
import lto.manager.web.resource.CSS;

public class LazyLoad {

	public static Void spinner(Div<?> view, LazyLoadOptions settings) {
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

	private static Void trigger(Div<?> view, LazyLoadOptions settings) {
		view
			.addAttr(Attribute.AJAX_DATA, settings.pathAJAX())
			.addAttr(Attribute.AJAX_SUCCESS_CALLBACK, settings.successCallJS())
			.addAttr(Attribute.AJAX_ERROR_CALLBACK, settings.errorCallJS());
		return null;
	}
}
