package lto.manager.web.handlers.http.pages.sandpit;

import org.xmlet.htmlapifaster.Div;
import org.xmlet.htmlapifaster.EnumTypeButtonType;

import com.sun.net.httpserver.HttpExchange;

import lto.manager.web.handlers.http.BaseHTTPHandler;
import lto.manager.web.handlers.http.templates.TemplatePage.SelectedPage;
import lto.manager.web.handlers.http.templates.TemplatePage.TemplatePageModel;
import lto.manager.web.handlers.http.templates.models.BodyModel;
import lto.manager.web.handlers.http.templates.models.HeadModel;

public class DatabaseTestHandler extends BaseHTTPHandler {
	public static final String PATH = "/sandpit/database";

	static Void content(Div<?> view, BodyModel model) {
		final String path = model.getQueryNoNull("path");

		view
			.div()
				.form()
					.b().text("Database path: ").__()
					.input().attrName("path").of(input -> input.attrValue(path)).__()
					.button().attrType(EnumTypeButtonType.SUBMIT).text("Open Database").__().br().__()
				.__()
			.__();
		return null;
	}

	@Override
	public void requestHandle(HttpExchange he) throws Exception {
		HeadModel thm = HeadModel.of("Database Test");
		TemplatePageModel tpm = TemplatePageModel.of(DatabaseTestHandler::content, null, thm, SelectedPage.Sandpit, BodyModel.of(he, null), null);
		requestHandleCompletePage(he, tpm);
	}
}
