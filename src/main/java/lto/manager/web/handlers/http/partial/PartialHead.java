package lto.manager.web.handlers.http.partial;

import org.xmlet.htmlapifaster.EnumRelType;
import org.xmlet.htmlapifaster.Html;

import htmlflow.HtmlPage;
import lto.manager.web.handlers.http.BaseHTTPHandler;
import lto.manager.web.handlers.http.templates.models.HeadModel;
import lto.manager.web.resource.Asset;

public class PartialHead {
	public static void template(Html<HtmlPage> root, HeadModel model) {
		root
			.head()
				.meta().addAttr(BaseHTTPHandler.CHARSET_KEY, BaseHTTPHandler.CHARSET_VALUE).__()
				.title().of(title -> title.text(model.getTitle())).__()
				.link().addAttr(BaseHTTPHandler.ICON_KEY, BaseHTTPHandler.ICON_VALUE).attrHref(Asset.IMG_FAVICO_SVG).addAttr(BaseHTTPHandler.TYPE_KEY, BaseHTTPHandler.TYPE_SVG).__()
				.meta().attrName(BaseHTTPHandler.VIEWPORT_KEY).attrContent(BaseHTTPHandler.VIEWPORT_VALUE).__()
				.link().attrRel(EnumRelType.STYLESHEET).attrHref(Asset.CSS_MAIN).__()
				.link().attrRel(EnumRelType.STYLESHEET).attrHref(Asset.CSS_MOBILE).addAttr(BaseHTTPHandler.MEDIA_KEY, BaseHTTPHandler.CSS_MOBILE_MEDIA).__()
				.of(extra -> {
					for (String css: model.getExtraStylesList()) {
						extra.link().attrRel(EnumRelType.STYLESHEET).attrHref(css).__();
					}
					extra.script().attrSrc(Asset.JS_MAIN).__();
					for (String js: model.getExtraScriptsList()) {
						extra.script().attrSrc(js).__();
					}
					for (String js: model.getExtraScriptsDeferList()) {
						extra.script().attrSrc(js).attrDefer(true).__();
					}
				})
			.__(); // head
	}
}
