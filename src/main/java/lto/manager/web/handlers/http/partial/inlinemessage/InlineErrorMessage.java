package lto.manager.web.handlers.http.partial.inlinemessage;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.xmlet.htmlapifaster.Div;

import lto.manager.common.ExternalProcess;
import lto.manager.web.resource.CSS;

public class InlineErrorMessage {
	public static Void contentGeneric(Div<?> view, final Exception ex) {
		view.div().attrClass(CSS.INLINE_MESSAGE + CSS.ERROR)
			.span().__()
			.b().raw("Error: " + ex.getMessage()).__()
			.a().attrClass(CSS.INLINE_MESSAGE_DETAILS_LINK).attrTabIndex(1).text("Show Details").__()
			.div().attrClass(CSS.INLINE_MESSAGE_DETAILS_CONTENT + CSS.FONT_MONOSPACE + CSS.FONT_SMALL).attrTabIndex(2)
				.of(div -> {
					StringWriter sw = new StringWriter();
					PrintWriter pw = new PrintWriter(sw);
					ex.printStackTrace(pw);
					final String stackTrace = "<div>" + sw.toString().replace(System.lineSeparator(), "</div><div>");
					div.raw(stackTrace);
				})
			.__()
		.__();
		return null;
	}

	public static Void contentExternalProcess(Div<?> view, final String uuid) {
		view.div().attrClass(CSS.INLINE_MESSAGE + CSS.ERROR)
			.span().__()
			.b().text("Error getting data").__()
			.a().attrClass(CSS.INLINE_MESSAGE_DETAILS_LINK).attrTabIndex(1).text("Show Details").__()
			.div().attrClass(CSS.INLINE_MESSAGE_DETAILS_CONTENT + CSS.FONT_MONOSPACE).attrTabIndex(2)
				.of(div -> {
					if (uuid != null) {
						final var process = ExternalProcess.getFinishedProcess(uuid);
						if (process != null) {
							final var processShow = process;
							div.b().text("cmd:").__()
							.p().text(processShow.getArgsAsString()).__()
							.of(d -> {
								if (processShow.completed()) {
									d.b().text("exit code:").__()
									.p().text(processShow.getExitCode()).__();
								} else {
									d.b().text("process has not exited").__().br().__();
								}
							})
							.b().text("stdout:").__()
							.of(d -> {
								for (var out: processShow.getStdout()) {
									d.p().text(out).__();
								}
							});
							div.b().text("stderr:").__()
							.of(d -> {
								for (var out: processShow.getStderr()) {
									d.p().text(out).__();
								}
							});
						} else {
							div.p().text("Unable to get details of processes with UUID: " + uuid).__();
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
