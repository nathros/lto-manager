package lto.manager.web.handlers.http.templates;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.sun.net.httpserver.HttpExchange;

import htmlflow.HtmlFlow;
import htmlflow.HtmlPage;
import htmlflow.HtmlView;
import lto.manager.web.handlers.http.BaseHTTPHandler;
import lto.manager.web.handlers.http.templates.models.HeadModel;

public class TemplateInternalError {
	public static HtmlView view = HtmlFlow.view(TemplateInternalError::template);

	public static class TemplateInternalErrorModel {
		final Exception ex;
		final HttpExchange he;

		private TemplateInternalErrorModel(Exception ex, HttpExchange he) {
			this.ex = ex;
			this.he = he;
		}

		public static TemplateInternalErrorModel of(Exception ex, HttpExchange he) {
			return new TemplateInternalErrorModel(ex, he);
		}
	}

	static void template(HtmlPage view) {
		view
		.html().attrLang(BaseHTTPHandler.LANG_VALUE)
		.dynamic((root, m) -> TemplateHead.template(root, HeadModel.of("HTTP Status 500")))
		.body()
			.div()
				.attrStyle("font-family:monospace")
				.<TemplateInternalErrorModel>dynamic((div, model) -> {
					StringWriter sw = new StringWriter();
					PrintWriter pw = new PrintWriter(sw);
					model.ex.printStackTrace(pw);
					String trace = sw.toString().replace(System.lineSeparator(), "<br>\n");

					StringBuilder sb = new StringBuilder(128);

					String path = model.he.getRequestURI().toString();

					Set<Map.Entry<String, List<String>>> entries = model.he.getRequestHeaders().entrySet();
					for (Map.Entry<String, List<String>> entry : entries)
						sb.append(entry.toString() + "<br>\n");

					String headers = "";//sb.toString();

					sb = new StringBuilder(128);
					try {
						InputStreamReader isr = new InputStreamReader(model.he.getRequestBody(), "utf-8");
						BufferedReader br = new BufferedReader(isr);
						sb.append(br.readLine());
					} catch (Exception e) {
						e.printStackTrace();
					}

					String requestBody = sb.toString();

					String query = model.he.getRequestURI().getQuery();
					String requestQuery = query == null ? "null" : query;
					final String style = "background-color:darkblue; color:white;";

					div.p().attrStyle("padding:1rem; font-weight:bold; font-size:1rem;" + style).text("HTTP Status 500 - Internal Error").__()
					.hr().__()
					.p().b().attrStyle(style).text("Path: ").__().__()
					.p().of(p -> p.text(path)).__()
					.hr().__()

					.p().b().attrStyle(style).text("Request Query: ").__().__()
					.p().of(p -> p.text(requestQuery)).__()
					.hr().__()

					.p().b().attrStyle(style).text("Request Body: ").__().__()
					.p().of(p -> p.text(requestBody)).__()
					.hr().__()

					.p().b().attrStyle(style).text("Request Headers: ").__().__()
					.p().of(p -> p.text(headers)).__()
					.hr().__()

					.p().b().attrStyle(style).text("Stack Trace: ").__().__()
					.p().of(p -> p.text(trace)).__()
					.hr().__();
				})
			.__() // div
		.__() // body
		.__(); // html
	}
}
