package lto.manager.web.handlers.templates;

import java.io.PrintWriter;
import java.io.StringWriter;

import htmlflow.DynamicHtml;
import lto.manager.web.handlers.BaseHandler;

public class TemplateInternalError {
	public static class TemplateInternalErrorModel {
		final Exception ex;

		private TemplateInternalErrorModel(Exception ex) {
			this.ex = ex;
		}

		public static TemplateInternalErrorModel of(Exception ex) {
			return new TemplateInternalErrorModel(ex);
		}
	}

	public static DynamicHtml<TemplateInternalErrorModel> view = DynamicHtml.view(TemplateInternalError::template);

	static void template(DynamicHtml<TemplateInternalErrorModel> view, TemplateInternalErrorModel model) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		model.ex.printStackTrace(pw);
		String trace = sw.toString().replace(System.lineSeparator(), "<br>\n");

		view.
			html().attrLang(BaseHandler.LANG_VALUE)
	        	.dynamic(head -> view.addPartial(TemplateHead.view, TemplateHead.TemplateHeadModel.of("Error")))
	            .body()
	            	.div()
	            		.p().attrStyle("font-family:monospace")
	            			.dynamic(p -> p.text(trace))
            			.__() // p
            		.__() // div
	            .__() // body
        .__(); // html
	}
}
