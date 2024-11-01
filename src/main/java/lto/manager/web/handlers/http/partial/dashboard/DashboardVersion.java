package lto.manager.web.handlers.http.partial.dashboard;

import java.io.IOException;
import java.net.InetAddress;
import java.util.function.Supplier;

import org.xmlet.htmlapifaster.Div;

import lto.manager.Version;
import lto.manager.web.handlers.http.api.APISystemInfo;
import lto.manager.web.resource.CSS;

public class DashboardVersion {
	private static final Supplier<String> hostNameSupplier = () -> {
		try {
			return InetAddress.getLocalHost().getHostName();
		} catch (IOException e) {
			return "Not found";
		}
	};

	public static Void content(Div<?> view, final DashboardVersionOptions options) {
		final String hostname = hostNameSupplier.get();
		final long uptime = (System.currentTimeMillis() - APISystemInfo.startTime);
		final long uptimeSeconds = uptime / 1000;
		final String sec = String.format("%02d", uptimeSeconds % 60);
		final String min = String.format("%02d", (uptimeSeconds / 60) % 60);
		final String hours = String.format("%02d", (uptimeSeconds / 60 / 60) % 60);
		final String days = String.format("%01d", (uptimeSeconds / 60 / 60 / 60) % 24);
		final String uptimeStr = String.valueOf(days) + " Days, " + hours + ":" + min + ":" + sec;
		view
			.div()
				.attrClass(CSS.DASHBOARD_ITEM)
				.h3().text("System Information").__()
				.h4().text("Overview").__()
				.div()
					.attrClass("system-info")
					.b().text("Version: ").__()
					.span().text(Version.VERSION).__()
					.b().text("Build Date: ").__()
					.span().text(Version.BUILD_DATE).__()
					.b().text("Branch: ").__()
					.span().text(Version.BRANCH).__()
					.b().text("Hostname: ").__()
					.span().text(hostname).__()
					.b().text("Uptime: ").__()
					.span().attrId("uptime").text(uptimeStr).__()
				.__()
				.of(div -> {
					if (!options.getShowLibraries()) return;
					div.h4().text("Libraries").__()
					.div()
						.attrClass("system-info")
						.of(divInner -> {
							for (int i = 0; i < Version.DEPENDENCIES.size(); i += 3) {
								divInner.b().text(Version.DEPENDENCIES.get(i)).__();
								divInner
									.span()
										.text(Version.DEPENDENCIES.get(i + 1) + ": ")
										.a()
											.attrStyle("font-size:small")
											.attrTarget("_blank")
											.attrHref(Version.DEPENDENCIES.get(i + 2))
											.text(Version.DEPENDENCIES.get(i + 2))
										.__() // a
									.__(); // span
							}
						}) // of divInner
					.__(); // div
				}) // of div
			.__(); // div
		return null;
	}

	public static class DashboardVersionOptions {
		private boolean showLibraries;

		public static DashboardVersionOptions of(final boolean showLibraries) {
			return new DashboardVersionOptions().setShowLibraries(showLibraries);
		}

		public DashboardVersionOptions setShowLibraries(boolean showLibraries) {
			this.showLibraries = showLibraries;
			return this;
		}

		public boolean getShowLibraries() {
			return showLibraries;
		}
	}
}
