package lto.manager.web.handlers;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;

import org.xmlet.htmlapifaster.EnumRelType;
import org.xmlet.htmlapifaster.EnumTypeInputType;

import com.sun.net.httpserver.HttpExchange;

import htmlflow.StaticHtml;
import lto.manager.web.Asset;

public class RootHandler extends BaseHandler {
	public static final String PATH = "/";

	@Override
	public void requestHandle(HttpExchange he) throws IOException {
		String response =
		StaticHtml
			.view()
				.html().attrLang(BaseHandler.LANG_VALUE)
					.head()
						.meta().addAttr(BaseHandler.CHARSET_KEY, BaseHandler.CHARSET_VALUE).__()
						.title().text("RootHandler").__()
						.link().addAttr(BaseHandler.ICON_KEY, BaseHandler.ICON_VALUE).attrHref(Asset.IMG_FAVICO_SVG).addAttr(BaseHandler.TYPE_KEY, BaseHandler.TYPE_SVG).addAttr(BaseHandler.ICON_SIZE_KEY, BaseHandler.ICON_SIZE_VALUE).__()
						.meta().attrName(BaseHandler.VIEWPORT_KEY).attrContent(BaseHandler.VIEWPORT_VALUE).__()
						.link().attrRel(EnumRelType.STYLESHEET).attrHref(Asset.CSS_MAIN).__()
						.link().attrRel(EnumRelType.STYLESHEET).attrHref(Asset.CSS_MOBILE).addAttr(BaseHandler.MEDIA_KEY, BaseHandler.CSS_MOBILE_MEDIA).__()
					.__() //head
					.body()
						.header().attrClass("header-root")
							.div().attrClass("header-root-container")
								.div().attrId("nav-toggle")
									.input().attrType(EnumTypeInputType.CHECKBOX).__()
									.div() // Menu Icon
										.span().__()
										.span().__()
										.span().__()
									.__() // div
									.ul().attrId("nav-menu")
										.li().a().attrClass("icon-alarm").text("ss").__().__()
									.__()
								.__() // div nav-toggle
							.__() // div header-root-container
						.__() // header
						.div().attrClass("main-content")
							.div().attrClass("nav-area").__()
							.div().attrClass("main-content-wrapper")
								.h1().text("My first page with HtmlFlow").__()
								.img().attrSrc("http://bit.ly/2MoHwrU").__()
								.p().text("Typesafe is awesome !").__()
							.__() // div
						.__() //div
					.__() //body
				.__() //html
			.render();

		he.sendResponseHeaders(HttpURLConnection.HTTP_OK, response.length());
		OutputStream os = he.getResponseBody();
		os.write(response.getBytes());
		os.close();
	}

}
