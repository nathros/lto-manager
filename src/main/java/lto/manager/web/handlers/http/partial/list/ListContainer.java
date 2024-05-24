package lto.manager.web.handlers.http.partial.list;

import java.util.List;
import java.util.function.Consumer;

import org.xmlet.htmlapifaster.Div;
import org.xmlet.htmlapifaster.Element;

import lto.manager.web.resource.CSS;

public class ListContainer {
	public static void content(Element<?, ?> view, List<Consumer<Div<?>>> itemContents) {
		new Div<>(view)
			.attrClass(CSS.LIST_CONTAINER)
				.of(container -> {
					for (var item : itemContents) {
						container.div()
							.of(c -> item.accept(c))
						.__();
					}
				})
		.__(); // Div
	}

}
