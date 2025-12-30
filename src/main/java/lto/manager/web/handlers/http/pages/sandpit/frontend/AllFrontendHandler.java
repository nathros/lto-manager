package lto.manager.web.handlers.http.pages.sandpit.frontend;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import org.xmlet.htmlapifaster.Div;

import com.sun.net.httpserver.HttpExchange;

import lto.manager.common.database.tables.records.RecordRole.Permission;
import lto.manager.web.handlers.http.BaseHTTPHandler;
import lto.manager.web.handlers.http.templates.TemplatePage.BreadCrumbs;
import lto.manager.web.handlers.http.templates.TemplatePage.SelectedPage;
import lto.manager.web.handlers.http.templates.TemplatePage.TemplatePageModel;
import lto.manager.web.handlers.http.templates.models.BodyModel;
import lto.manager.web.handlers.http.templates.models.HeadModel;
import lto.manager.web.resource.Asset;
import lto.manager.web.resource.CSS;

public class AllFrontendHandler extends BaseHTTPHandler {
	public static final String PATH = Asset.PATH_SANDPIT_BASE + "all/";
	public static final String NAME = "All Frontend";

	static Void content(Div<?> view, BodyModel model) {
		view
			.div().attrClass(CSS.GROUP).addAttr(CSS.GROUP_ATTRIBUTE, ToastTestHandler.NAME)
				.of(d -> ToastTestHandler.content(d, model))
			.__()
			.div().attrClass(CSS.GROUP).addAttr(CSS.GROUP_ATTRIBUTE, CheckBoxTestHandler.NAME)
				.of(d -> CheckBoxTestHandler.content(d, model))
			.__()
			.div().attrClass(CSS.GROUP).addAttr(CSS.GROUP_ATTRIBUTE, SwitchTestHandler.NAME)
				.of(d -> SwitchTestHandler.content(d, model))
			.__()
			.div().attrClass(CSS.GROUP).addAttr(CSS.GROUP_ATTRIBUTE, RadioGroupTestHandler.NAME)
				.of(d -> RadioGroupTestHandler.content(d, model))
			.__()
			.div().attrClass(CSS.GROUP).addAttr(CSS.GROUP_ATTRIBUTE, InlineMessageTestHandler.NAME)
				.of(d -> InlineMessageTestHandler.content(d, model)) // FIXME Has unclosed
			.__()
			.div().attrClass(CSS.GROUP).addAttr(CSS.GROUP_ATTRIBUTE, ModalTestHandler.NAME)
				.of(d -> ModalTestHandler.content(d, model))
			.__()
			.div().attrClass(CSS.GROUP).addAttr(CSS.GROUP_ATTRIBUTE, InputTextTestHandler.NAME)
				.of(d -> InputTextTestHandler.content(d, model))
			.__()
			;
		return null;
	}

	@Override
	public void requestHandle(HttpExchange he, BodyModel bm) throws IOException, InterruptedException, ExecutionException {
		HeadModel thm = HeadModel.of(NAME);
		BreadCrumbs crumbs = new BreadCrumbs().add(AllFrontendHandler.NAME, AllFrontendHandler.PATH);
		TemplatePageModel tpm = TemplatePageModel.of(AllFrontendHandler::content, null, thm, SelectedPage.Sandpit, bm, crumbs);
		requestHandleCompletePage(he, tpm);
	}

	@Override
	public Permission getHandlePermission() {
		// TODO Auto-generated method stub
		return null;
	}
}
