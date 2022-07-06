package lto.manager.web.handlers.sandpit;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;

import com.sun.net.httpserver.HttpExchange;

import htmlflow.StaticHtml;
import lto.manager.web.handlers.BaseHandler;

public class SandpitHandler extends BaseHandler {

	@Override
	public void requestHadle(HttpExchange he) throws IOException {
		String response =
		StaticHtml
			.view()
				.html().attrLang(BaseHandler.LANG_VALUE)
					.head()
						.meta().addAttr(BaseHandler.CHARSET_KEY, BaseHandler.CHARSET_VALUE).__()
						.title().text("Sandpit").__()
					.__() //head
					.body()
						.div().attrStyle("text-align:center")
							.p().a().attrHref("/sandpit/database").text("Database test").__().__()
						.__() //  div
					.__() //body
				.__() //html
			.render();

		he.sendResponseHeaders(HttpURLConnection.HTTP_OK, response.length());
		OutputStream os = he.getResponseBody();
		os.write(response.getBytes());
		os.close();
	}
}
