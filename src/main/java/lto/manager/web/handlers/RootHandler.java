package lto.manager.web.handlers;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;

import org.xmlet.htmlapifaster.EnumRelType;

import com.sun.net.httpserver.HttpExchange;

import htmlflow.StaticHtml;

public class RootHandler extends BaseHandler {

	@Override
	public void handle(HttpExchange he) throws IOException {
		super.handle(he);
		String response =
		StaticHtml
			.view()
		    	.html().addAttr("lang", "en")
		    		.head()
		    			.meta().addAttr("charset", "UTF-8").__()
		    			.title().text("HtmlFlow").__()
		    			.link().attrRel(EnumRelType.ICON).attrHref("assets/img/favico.svg").addAttr("type", "image/svg+xml").__()
		    			.meta().attrName("viewport").attrContent("width=device-width, initial-scale=1").__()
	    			.__() //head
	    			.body()
			        	.div().attrClass("container")
			        		.h1().text("My first page with HtmlFlow").__()
			        		.img().attrSrc("http://bit.ly/2MoHwrU").__()
			        		.p().text("Typesafe is awesome! :-)").__()
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
