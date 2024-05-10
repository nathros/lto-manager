package lto.manager.web.handlers.http.partial.dashboard;

import org.xmlet.htmlapifaster.Div;

import lto.manager.web.handlers.http.partial.pie.PieCPUUsage;
import lto.manager.web.resource.CSS;

public class DashboardCPU {

	public static Void content(Div<?> view) {
		view
			.div()
				.attrClass(CSS.DASHBOARD_ITEM)
				.h3().text("CPU").__()
				.div()
					.attrClass(CSS.GROUP)
					.addAttr(CSS.GROUP_ATTRIBUTE, "")
					.div().attrClass(CSS.PIE_CONTAINER)
						.of(pie -> PieCPUUsage.content(pie))
					.__() // div
				.__() // div
			.__(); // div
		return null;
	}
}
