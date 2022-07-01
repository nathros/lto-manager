package lto.manager.web.handlers;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;

import org.xmlet.htmlapifaster.EnumRelType;

import com.sun.net.httpserver.HttpExchange;

import htmlflow.StaticHtml;
import lto.manager.web.Asset;

public class RootHandler extends BaseHandler {

	@Override
	public void handle(HttpExchange he) throws IOException {
		super.handle(he);
		String response =
		StaticHtml
			.view()
				.html().attrLang(BaseHandler.LANG_VALUE)
					.head()
						.meta().addAttr(BaseHandler.CHARSET_KEY, BaseHandler.CHARSET_VALUE).__()
						.title().text("RootHandler").__()
						.link().attrRel(EnumRelType.ICON).attrHref(Asset.IMG_FAVICO_SVG).addAttr("type", "image/svg+xml").__()
						.meta().attrName(BaseHandler.VIEWPORT_KEY).attrContent(BaseHandler.VIEWPORT_VALUE).__()
						.link().attrRel(EnumRelType.STYLESHEET).attrHref(Asset.CSS_MAIN).__()
					.__() //head
					.body()
						.div().attrClass("container")
							.h1().text("My first page with HtmlFlow").__()
							.img().attrSrc("http://bit.ly/2MoHwrU").__()
							.p().text("Typesafe is awesome !").__()
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

