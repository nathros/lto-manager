package lto.manager.web.handlers.http.partial.dashboard;

import java.util.function.Consumer;

import org.xmlet.htmlapifaster.Div;
import org.xmlet.htmlapifaster.Element;

import lto.manager.web.resource.CSS;

public class DashboardContainer {
	public static void content(Element<?, ?> parent, Consumer<Div<?>> consumer) {
		new Div<>(parent)
				.attrClass(CSS.DASHBOARD_CONTAINER)
				.of(div -> {
					consumer.accept(div);
				})
			.__();
	}
}
