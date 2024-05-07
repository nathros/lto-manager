package lto.manager.web.handlers.http.partial.dashboard;

import java.util.function.Consumer;

import org.xmlet.htmlapifaster.Div;

import lto.manager.web.resource.CSS;

public class DashboardContainer {
	public static Void content(Div<?> view, Consumer<Div<?>> consumer) {
		view
			.attrClass(CSS.DASHBOARD_CONTAINER)
			.of(div -> {
				consumer.accept(div);
			});
		return null;
	}
}
