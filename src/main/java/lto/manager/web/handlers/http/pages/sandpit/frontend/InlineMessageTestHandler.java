package lto.manager.web.handlers.http.pages.sandpit.frontend;

import org.xmlet.htmlapifaster.Div;

import com.sun.net.httpserver.HttpExchange;

import lto.manager.common.database.tables.records.RecordRole.Permission;
import lto.manager.web.handlers.http.BaseHTTPHandler;
import lto.manager.web.handlers.http.pages.sandpit.SandpitHandler;
import lto.manager.web.handlers.http.partial.inlinemessage.InlineMessage;
import lto.manager.web.handlers.http.templates.TemplatePage.BreadCrumbs;
import lto.manager.web.handlers.http.templates.TemplatePage.SelectedPage;
import lto.manager.web.handlers.http.templates.TemplatePage.TemplatePageModel;
import lto.manager.web.handlers.http.templates.models.BodyModel;
import lto.manager.web.handlers.http.templates.models.HeadModel;
import lto.manager.web.resource.Asset;

public class InlineMessageTestHandler extends BaseHTTPHandler {
	public static final String PATH = Asset.PATH_SANDPIT_BASE + "inline-message/";
	public static final String NAME = "Inline Message";

	static Void content(Div<?> view, BodyModel model) {
		final Exception exception = new Exception("test exception");

		// TODO other types
		view
			.div().attrStyle("display:flex;flex-direction:column;gap:var(--padding)")
				.div().of(d -> InlineMessage.contentGenericInfo(d, "Inline Info [text only]"))

				.div().of(d -> InlineMessage.contentGenericWarning(d, "Inline Warning [text only]"))

				.div().of(d -> InlineMessage.contentGenericError(d, "Inline Error [text only]"))

				.div().of(d -> InlineMessage.contentGenericError(d, "Inline Error [exception]", exception))

			.__();
		return null;
	}

	@Override
	public void requestHandle(HttpExchange he, BodyModel bm) throws Exception {
		HeadModel thm = HeadModel.of(NAME);
		BreadCrumbs crumbs = new BreadCrumbs().add(SandpitHandler.NAME, SandpitHandler.PATH).add(NAME, PATH);
		TemplatePageModel tpm = TemplatePageModel.of(InlineMessageTestHandler::content, null, thm, SelectedPage.Sandpit, bm, crumbs);
		requestHandleCompletePage(he, tpm);
	}

	@Override
	public Permission getHandlePermission() {
		// TODO Auto-generated method stub
		return null;
	}

}
