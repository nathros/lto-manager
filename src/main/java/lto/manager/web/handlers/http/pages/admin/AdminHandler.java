package lto.manager.web.handlers.http.pages.admin;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import org.xmlet.htmlapifaster.Div;

import com.sun.net.httpserver.HttpExchange;

import lto.manager.common.Util;
import lto.manager.web.handlers.http.BaseHTTPHandler;
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
			final int maxMemoryMB = (int) (Util.getJVMMaxMemory() / 1024 / 1024);
			final int allocatedMB = (int) (Util.getJVMAllocatedMemory() / 1024 / 1024);
			final int usedMemMB = (int) ((Util.getJVMAllocatedMemory() - Util.getUsedMemory()) / 1024 / 1024);

			int p = (int) (((double)usedMemMB / (double)maxMemoryMB) * 100);
			div
				.a().attrClass(CSS.BUTTON).attrHref(UpdateOptionsHandler.PATH).text("Change Settings").__()
				.div().attrClass("card").addAttr("header-text", "Memory Usage")
					.div().attrClass("pie-container")
						.div().attrClass(CSS.PIE_ITEM)
							.div()
								.div().attrClass(CSS.PIE_CIRCLE).attrStyle("--p:" + p + ";--c:orange;").text(p + "%").__()
								.i().attrClass("pie-info")
									.em().attrClass("pie-tooltip")
										.text("[In use] used from allocated, ")
										.text("[Allocated] total request from OS, ")
										.text("[Maximum] max memory for JVM set by -Xmx")
										.br().__()
										.text(usedMemMB + " / " + maxMemoryMB + " * 100 = " + p + "%")
									.__()
								.__()
							.__()
							.div()
								.table()
									.tr()
										.td().b().text("In use:").__().__()
										.td().text(usedMemMB + " MB").__()
									.__()
									.tr()
										.td().b().text("Allocated:").__().__()
										.td().text(allocatedMB + " MB").__()
									.__()
									.tr()
										.td().b().text("Maximum:").__().__()
										.td().text(maxMemoryMB + " MB").__()
									.__()
								.__()
							.__()
						.__()
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
