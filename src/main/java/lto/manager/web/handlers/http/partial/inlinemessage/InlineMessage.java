package lto.manager.web.handlers.http.partial.inlinemessage;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.xmlet.htmlapifaster.Div;

import lto.manager.common.ExternalProcess;
import lto.manager.web.resource.CSS;

public class InlineMessage {
	public static Void contentGenericError(Div<?> view, final Exception ex) {
		view.attrClass(CSS.INLINE_MESSAGE + CSS.ERROR)
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
			.__();
		return null;
	}

	public static Void contentGenericError(Div<?> view, final String title, final Exception ex) {
		view.attrClass(CSS.INLINE_MESSAGE + CSS.ERROR)
			.span().__()
			.b().raw(title).__()
			.a().attrClass(CSS.INLINE_MESSAGE_DETAILS_LINK).attrTabIndex(1).text("Show Details").__()
			.div().attrClass(CSS.INLINE_MESSAGE_DETAILS_CONTENT + CSS.FONT_MONOSPACE + CSS.FONT_SMALL).attrTabIndex(2)
				.of(div -> {
					StringWriter sw = new StringWriter();
					PrintWriter pw = new PrintWriter(sw);
					ex.printStackTrace(pw);
					final String stackTrace = "<div>" + sw.toString().replace(System.lineSeparator(), "</div><div>");
					div.raw(stackTrace);
				})
			.__();
		return null;
	}

	public static Void contentGenericError(Div<?> view, final String title, final String message) {
		view.attrClass(CSS.INLINE_MESSAGE + CSS.ERROR)
			.span().__()
			.b().text(title).__()
			.a().attrClass(CSS.INLINE_MESSAGE_DETAILS_LINK).attrTabIndex(1).text("Show Details").__()
			.div()
				.attrClass(CSS.INLINE_MESSAGE_DETAILS_CONTENT + CSS.FONT_MONOSPACE + CSS.FONT_SMALL).attrTabIndex(2)
				.text(message)
			.__()
		.__();
		return null;
	}

	// Self closing
	public static Void contentGenericError(Div<?> view, final String title) {
		view.attrClass(CSS.INLINE_MESSAGE + CSS.ERROR)
			.span().__()
			.b().text(title).__()
		.__();
		return null;
	}

	public static Void contentGenericOK(Div<?> view, final String title) {
		view.attrClass(CSS.INLINE_MESSAGE + CSS.OK)
			.span().__()
			.b().text(title).__()
		.__();
		return null;
	}

	public static Void contentGenericOK(final String classStr, Div<?> view, final String title) {
		view.attrClass(CSS.INLINE_MESSAGE + CSS.OK + classStr)
			.span().__()
			.b().text(title).__()
		.__();
		return null;
	}

	public static Void contentGenericInfo(Div<?> view, final String title) {
		view.attrClass(CSS.INLINE_MESSAGE + CSS.INFO)
			.span().__()
			.b().text(title).__()
		.__();
		return null;
	}

	public static Void contentGenericInfo(final String classStr, Div<?> view, final String title) {
		view.attrClass(CSS.INLINE_MESSAGE + CSS.INFO + classStr)
			.span().__()
			.b().text(title).__()
		.__();
		return null;
	}

	public static Void contentExternalProcess(Div<?> view, final String uuid) {
		view.attrClass(CSS.INLINE_MESSAGE + CSS.ERROR)
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
			.__();
		return null;
	}
}
