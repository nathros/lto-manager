package lto.manager.web.handlers.http.templates;

import org.xmlet.htmlapifaster.EnumRelType;

import htmlflow.DynamicHtml;
import lto.manager.web.handlers.http.BaseHTTPHandler;
import lto.manager.web.handlers.http.templates.models.HeadModel;
import lto.manager.web.resource.Asset;

public class TemplateHead {
	public static DynamicHtml<HeadModel> view = DynamicHtml.view(TemplateHead::template);

	static void template(DynamicHtml<HeadModel> view, HeadModel model) {
		try {
			view
				.defineRoot()
					.head()
						.meta().addAttr(BaseHTTPHandler.CHARSET_KEY, BaseHTTPHandler.CHARSET_VALUE).__()
						.title().dynamic(title -> title.text(model.getTitle())).__()
						.link().addAttr(BaseHTTPHandler.ICON_KEY, BaseHTTPHandler.ICON_VALUE).attrHref(Asset.IMG_FAVICO_SVG).addAttr(BaseHTTPHandler.TYPE_KEY, BaseHTTPHandler.TYPE_SVG).__()
						.meta().attrName(BaseHTTPHandler.VIEWPORT_KEY).attrContent(BaseHTTPHandler.VIEWPORT_VALUE).__()
						.link().attrRel(EnumRelType.STYLESHEET).attrHref(Asset.CSS_MAIN).__()
						.link().attrRel(EnumRelType.STYLESHEET).attrHref(Asset.CSS_MOBILE).addAttr(BaseHTTPHandler.MEDIA_KEY, BaseHTTPHandler.CSS_MOBILE_MEDIA).__()
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
