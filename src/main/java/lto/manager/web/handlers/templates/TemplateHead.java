package lto.manager.web.handlers.templates;

import org.xmlet.htmlapifaster.EnumRelType;

import htmlflow.DynamicHtml;
import lto.manager.web.handlers.BaseHandler;
import lto.manager.web.handlers.templates.models.TemplateHeadModel;
import lto.manager.web.resource.Asset;

public class TemplateHead {
	public static DynamicHtml<TemplateHeadModel> view = DynamicHtml.view(TemplateHead::template);

	static void template(DynamicHtml<TemplateHeadModel> view, TemplateHeadModel model) {
		try {
			view
				.defineRoot()
					.head()
						.meta().addAttr(BaseHandler.CHARSET_KEY, BaseHandler.CHARSET_VALUE).__()
						.title().dynamic(title -> title.text(model.getTitle())).__()
						.link().addAttr(BaseHandler.ICON_KEY, BaseHandler.ICON_VALUE).attrHref(Asset.IMG_FAVICO_SVG).addAttr(BaseHandler.TYPE_KEY, BaseHandler.TYPE_SVG).__()
						.meta().attrName(BaseHandler.VIEWPORT_KEY).attrContent(BaseHandler.VIEWPORT_VALUE).__()
						.link().attrRel(EnumRelType.STYLESHEET).attrHref(Asset.CSS_MAIN).__()
						.link().attrRel(EnumRelType.STYLESHEET).attrHref(Asset.CSS_MOBILE).addAttr(BaseHandler.MEDIA_KEY, BaseHandler.CSS_MOBILE_MEDIA).__()
						.of(extra -> {
							for (String css: model.getExtraStylesList()) {

								extra.link().attrRel(EnumRelType.STYLESHEET).attrHref(css).__();
							}
							if (model.getExtraScriptsList().size() > 0) {
								for (String js: model.getExtraScriptsList()) {
									extra.script().attrSrc(js).__();
								}
							}
						})
					.__(); // head
		} catch (Exception e) {
			TemplateHead.view = DynamicHtml.view(TemplateHead::template);
			throw e;
		}
	}
}
