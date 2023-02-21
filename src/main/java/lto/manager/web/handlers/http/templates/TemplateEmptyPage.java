package lto.manager.web.handlers.http.templates;

import htmlflow.DynamicHtml;
import lto.manager.web.handlers.http.BaseHTTPHandler;
import lto.manager.web.handlers.http.templates.models.HeadModel;

public class TemplateEmptyPage {
	public static class TemplateEmptyPageModel {
		final DynamicHtml<?> dynamicHtml;
		final HeadModel head;

		private TemplateEmptyPageModel(DynamicHtml<?> dynamicHtml, HeadModel head) {
			this.dynamicHtml = dynamicHtml;
			this.head = head;
		}

		public static TemplateEmptyPageModel of(DynamicHtml<?> dynamicHtml, HeadModel head) {
			return new TemplateEmptyPageModel(dynamicHtml, head);
		}
	}

	public static DynamicHtml<TemplateEmptyPageModel> view = DynamicHtml.view(TemplateEmptyPage::template);

	static void template(DynamicHtml<TemplateEmptyPageModel> view, TemplateEmptyPageModel model) {
		try {
			view
				.html().attrLang(BaseHTTPHandler.LANG_VALUE)
					.dynamic(head -> view.addPartial(TemplateHead.view, HeadModel.of(model.head.getTitle())))
					.body()
						.dynamic(div -> view.addPartial(model.dynamicHtml, null))
					.__() // body
				.__(); // html
		} catch (Exception e) {
			TemplateEmptyPage.view = DynamicHtml.view(TemplateEmptyPage::template);
			throw e;
		}
	}
}
