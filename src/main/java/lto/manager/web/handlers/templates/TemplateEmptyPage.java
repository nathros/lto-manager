package lto.manager.web.handlers.templates;

import htmlflow.DynamicHtml;
import lto.manager.web.handlers.BaseHandler;
import lto.manager.web.handlers.templates.TemplateHead.TemplateHeadModel;

public class TemplateEmptyPage {
	public static class TemplateEmptyPageModel {
		final DynamicHtml<?> dynamicHtml;
		final TemplateHeadModel head;

		private TemplateEmptyPageModel(DynamicHtml<?> dynamicHtml, TemplateHeadModel head) {
			this.dynamicHtml = dynamicHtml;
			this.head = head;
		}

		public static TemplateEmptyPageModel of(DynamicHtml<?> dynamicHtml, TemplateHeadModel head) {
			return new TemplateEmptyPageModel(dynamicHtml, head);
		}
	}

	public static DynamicHtml<TemplateEmptyPageModel> view = DynamicHtml.view(TemplateEmptyPage::template);

	static void template(DynamicHtml<TemplateEmptyPageModel> view, TemplateEmptyPageModel model) {
		view.
			html().attrLang(BaseHandler.LANG_VALUE)
			.dynamic(head -> view.addPartial(TemplateHead.view, TemplateHead.TemplateHeadModel.of(model.head.title)))
	            .body()
	            	.div()
            			.dynamic(div -> view.addPartial(model.dynamicHtml, null))
            		.__() // div
	            .__() // body
        .__(); // html
	}
}
