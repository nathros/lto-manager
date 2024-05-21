package lto.manager.web.handlers.http.pages.sandpit.frontend;

import org.xmlet.htmlapifaster.Div;
import org.xmlet.htmlapifaster.EnumTypeInputType;

import com.sun.net.httpserver.HttpExchange;

import lto.manager.web.handlers.http.BaseHTTPHandler;
import lto.manager.web.handlers.http.pages.sandpit.SandpitHandler;
import lto.manager.web.handlers.http.templates.TemplatePage.BreadCrumbs;
import lto.manager.web.handlers.http.templates.TemplatePage.SelectedPage;
import lto.manager.web.handlers.http.templates.TemplatePage.TemplatePageModel;
import lto.manager.web.handlers.http.templates.models.BodyModel;
import lto.manager.web.handlers.http.templates.models.HeadModel;
import lto.manager.web.resource.Asset;
import lto.manager.web.resource.CSS;

public class ToastTestHandler extends BaseHTTPHandler {
	public static final String PATH = Asset.PATH_SANDPIT_BASE + "toast/";
	public static final String NAME = "Toast";

	static Void content(Div<?> view, BodyModel model) {
		final String type = "Toast[document.getElementById('opt').value]";
		final String text = "document.getElementById('text').value";
		final String time = "document.getElementById('time').value";
		final String cancel = "document.getElementById('can').checked";
		final String buttonJS = String.format("showToast(%s, %s, %s, null, %s)", type, text, time, cancel);
		view.
			div()
				.attrClass(CSS.FORMS_CONTAINER)
				.b().text("Toast Message:").__()
				.input()
					.attrType(EnumTypeInputType.TEXT)
					.attrId("text")
					.attrValue("Message text")
				.__()

				.b().text("Show time MS:").__()
				.input()
					.attrType(EnumTypeInputType.NUMBER)
					.attrId("time")
					.attrValue("0")
				.__()

				.b().text("Type:").__()
				.select()
					.attrId("opt")
					.option().text("Good").__()
					.option().text("Warning").__()
					.option().text("Error").__()
					.option().text("Info").__()
				.__()

				.b().text("Enable cancel:").__()
				.input()
					.attrType(EnumTypeInputType.CHECKBOX)
					.attrId("can")
				.__()
			.__() // div
			.button()
				.attrClass(CSS.BUTTON)
				.attrOnclick(buttonJS)
				.text("Show Toast")
			.__();
		return null;
	}

	@Override
	public void requestHandle(HttpExchange he) throws Exception {
		HeadModel thm = HeadModel.of(NAME);
		thm.addCSS(Asset.CSS_FORMS);
		BreadCrumbs crumbs = new BreadCrumbs().add(SandpitHandler.NAME, SandpitHandler.PATH).add(NAME, PATH);
		TemplatePageModel tpm = TemplatePageModel.of(ToastTestHandler::content, null, thm, SelectedPage.Sandpit, BodyModel.of(he, null), crumbs);
		requestHandleCompletePage(he, tpm);
	}

}
