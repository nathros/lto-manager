package lto.manager.web.handlers.http.partial.loading;

import org.xmlet.htmlapifaster.Div;

import lto.manager.web.resource.CSS;

public class OnLoad {
	public static final String DATA_AJAX = "data-ajax";

	public static Void content(Div<?> view, OnLoadOptions settings) {
		view
			.div().attrClass(CSS.LOADING_CONTAINER).addAttr(DATA_AJAX, settings.pathAJAX())
				.div().__() // Circle 1
				.div().__() // Circle 2
				.div().__() // Circle 3
				.div().__() // Circle 4
				.div().__() // Circle 5
			.__();
		return null;
	}
}
