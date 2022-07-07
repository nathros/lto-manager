package lto.manager.web.handlers.templates;

import org.xmlet.htmlapifaster.EnumRelType;

import htmlflow.DynamicHtml;
import lto.manager.web.Asset;
import lto.manager.web.handlers.BaseHandler;

public class TemplateHead {
	public static class TemplateHeadModel {
		final String title;

		private TemplateHeadModel(String title) {
			this.title = title;
		}

		public static TemplateHeadModel of(String title) {
			return new TemplateHeadModel(title);
		}
	}

	public static DynamicHtml<TemplateHeadModel> view = DynamicHtml.view(TemplateHead::template);

	static void template(DynamicHtml<TemplateHeadModel> view, TemplateHeadModel model) {
		view
			.defineRoot()
				.head()
					.meta().addAttr(BaseHandler.CHARSET_KEY, BaseHandler.CHARSET_VALUE).__()
					.title().dynamic(title -> title.text(model.title)).__()
					.link().addAttr(BaseHandler.ICON_KEY, BaseHandler.ICON_VALUE).attrHref(Asset.IMG_FAVICO_SVG).addAttr(BaseHandler.TYPE_KEY, BaseHandler.TYPE_SVG).addAttr(BaseHandler.ICON_SIZE_KEY, BaseHandler.ICON_SIZE_VALUE).__()
					.meta().attrName(BaseHandler.VIEWPORT_KEY).attrContent(BaseHandler.VIEWPORT_VALUE).__()
					.link().attrRel(EnumRelType.STYLESHEET).attrHref(Asset.CSS_MAIN).__()
					.link().attrRel(EnumRelType.STYLESHEET).attrHref(Asset.CSS_MOBILE).addAttr(BaseHandler.MEDIA_KEY, BaseHandler.CSS_MOBILE_MEDIA).__()
				.__(); // head
	}

}
