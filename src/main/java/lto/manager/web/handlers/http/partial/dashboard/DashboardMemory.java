package lto.manager.web.handlers.http.partial.dashboard;

import org.xmlet.htmlapifaster.Div;

import lto.manager.web.handlers.http.partial.pie.PieJVMMemoryUsage;
import lto.manager.web.resource.CSS;

public class DashboardMemory {

	public static Void content(Div<?> view/*, final DashboardMemoryOptions options*/) {
		view
			.div()
				.attrClass(CSS.DASHBOARD_ITEM)
				.h3().text("Memory").__()
				.div()
					.attrClass(CSS.GROUP)
					.addAttr(CSS.GROUP_ATTRIBUTE, "")
					.div().attrClass(CSS.PIE_CONTAINER)
						.of(pie -> PieJVMMemoryUsage.content(pie))
					.__() // div
				.__() // div
			.__(); // div
		return null;
	}

	/*public static class DashboardMemoryOptions {
		private boolean darkBackground;

		public static DashboardMemoryOptions of(final boolean darkBackground) {
			return new DashboardMemoryOptions().setDarkBackground(darkBackground);
		}

		public DashboardMemoryOptions setDarkBackground(boolean darkBackground) {
			this.darkBackground = darkBackground;
			return this;
		}

		public boolean getDarkBackground() {
			return darkBackground;
		}
	}*/
}
