package lto.manager.web.handlers.http.partial.inlinemessage;

import org.xmlet.htmlapifaster.Div;

import lto.manager.common.ExternalProcess;
import lto.manager.web.resource.CSS;

public class InlineErrorMessage {
	public static Void content(Div<?> view, final String uuid) {
		view.div().attrClass(CSS.INLINE_MESSAGE + CSS.ERROR)
			.span().__()
			.b().text("Error getting data").__()
			.a().attrClass(CSS.INLINE_MESSAGE_DETAILS_LINK).attrTabIndex(1).text("Show Details").__()
			.div().attrClass(CSS.INLINE_MESSAGE_DETAILS_CONTENT + CSS.FONT_MONOSPACE).attrTabIndex(2)
				.of(div -> {
					if (uuid != null) {
						final var process = ExternalProcess.processes.get(uuid);
						if (process != null) {
							div.b().text("cmd:").__()
							.p().text(process.getArgsAsString()).__()
							.of(d -> {
								if (process.completed()) {
									d.b().text("exit code:").__()
									.p().text(process.getExitCode()).__();
								} else {
									d.b().text("process has not exited").__().br().__();
								}
							})
							.b().text("stdout:").__()
							.of(d -> {
								for (var out: process.getStdout()) {
									d.p().text(out).__();
								}
							});
							div.b().text("stderr:").__()
							.of(d -> {
								for (var out: process.getStderr()) {
									d.p().text(out).__();
								}
							});
						} else {
							div.p().text("Unable to get details no processes with UUID: " + uuid).__();
						}
					} else {
						div.p().text("Unable to get details UUID is missing").__();
					}
				})
			.__()
		.__();
		return null;
	}
}
