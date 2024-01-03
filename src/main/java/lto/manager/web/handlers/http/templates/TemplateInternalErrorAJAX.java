package lto.manager.web.handlers.http.templates;

import com.sun.net.httpserver.HttpExchange;

import htmlflow.HtmlFlow;
import htmlflow.HtmlPage;
import htmlflow.HtmlView;
import lto.manager.web.handlers.http.partial.inlinemessage.InlineErrorMessage;

public class TemplateInternalErrorAJAX {
	public static HtmlView<TemplateInternalErrorModelAJAX> view = HtmlFlow.view(TemplateInternalErrorAJAX::template);

	public static class TemplateInternalErrorModelAJAX {
		final Exception ex;
		final HttpExchange he;

		private TemplateInternalErrorModelAJAX(Exception ex, HttpExchange he) {
			this.ex = ex;
			this.he = he;
		}

		public static TemplateInternalErrorModelAJAX of(Exception ex, HttpExchange he) {
			return new TemplateInternalErrorModelAJAX(ex, he);
		}
	}

	static void template(HtmlPage view) {
		view
		.div()
			.<TemplateInternalErrorModelAJAX>dynamic((div, errorModel) -> {
				InlineErrorMessage.contentGeneric(div, errorModel.ex);
			})
		.__(); // div
	}
}
