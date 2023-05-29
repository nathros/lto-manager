package lto.manager.web.handlers.http.pages.admin;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import org.xmlet.htmlapifaster.Div;

import com.sun.net.httpserver.HttpExchange;

import lto.manager.web.handlers.http.BaseHTTPHandler;
import lto.manager.web.handlers.http.partial.pie.PieMemoryUsage;
import lto.manager.web.handlers.http.partial.pie.PieCPUUsage;
import lto.manager.web.handlers.http.templates.TemplatePage.SelectedPage;
import lto.manager.web.handlers.http.templates.TemplatePage.TemplatePageModel;
import lto.manager.web.handlers.http.templates.models.BodyModel;
import lto.manager.web.handlers.http.templates.models.HeadModel;
import lto.manager.web.resource.Asset;
import lto.manager.web.resource.CSS;

public class AdminHandler extends BaseHTTPHandler {
	public static AdminHandler self = new AdminHandler();
	public static final String PATH = "/admin";

	static Void content(Div<?> view, BodyModel model) {
		view
		.div().of(div -> {
			div
				.a().attrClass(CSS.BUTTON).attrHref(UpdateOptionsHandler.PATH).text("Change Settings").__()
				.div().attrClass("card").addAttr("header-text", "System information")
					.div().attrClass(CSS.PIE_CONTAINER)
						.of(pie -> PieCPUUsage.content(pie))
						.of(pie -> PieMemoryUsage.content(pie))
					.__()
				.__()
			.__(); // div
		}).__(); //  div
		return null;
	}

	@Override
	public void requestHandle(HttpExchange he) throws IOException, InterruptedException, ExecutionException {
		HeadModel thm = HeadModel.of("Admin");
		thm.AddCSS(Asset.CSS_PIE);
		TemplatePageModel tpm = TemplatePageModel.of(AdminHandler::content, thm, SelectedPage.Admin, BodyModel.of(he, null));
		requestHandleCompletePage(he, tpm);
	}

}
