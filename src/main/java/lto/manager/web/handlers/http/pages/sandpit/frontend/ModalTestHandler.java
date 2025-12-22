package lto.manager.web.handlers.http.pages.sandpit.frontend;

import org.xmlet.htmlapifaster.Div;

import com.sun.net.httpserver.HttpExchange;

import lto.manager.common.database.tables.records.RecordRole.Permission;
import lto.manager.web.handlers.http.BaseHTTPHandler;
import lto.manager.web.handlers.http.pages.sandpit.SandpitHandler;
import lto.manager.web.handlers.http.partial.modal.Modal;
import lto.manager.web.handlers.http.partial.modal.ModalOptions;
import lto.manager.web.handlers.http.templates.TemplatePage.BreadCrumbs;
import lto.manager.web.handlers.http.templates.TemplatePage.SelectedPage;
import lto.manager.web.handlers.http.templates.TemplatePage.TemplatePageModel;
import lto.manager.web.handlers.http.templates.models.BodyModel;
import lto.manager.web.handlers.http.templates.models.HeadModel;
import lto.manager.web.resource.Asset;
import lto.manager.web.resource.CSS;
import lto.manager.web.resource.JS;

public class ModalTestHandler extends BaseHTTPHandler {
	public static final String PATH = Asset.PATH_SANDPIT_BASE + "modal/";
	public static final String NAME = "Modal";

	private static final String MODAL_ID = "modal-1";

	static Void content(Div<?> view, BodyModel model) {

		// @formatter:off
		view.
			div()
				.button().attrClass(CSS.BUTTON).attrOnclick(JS.showModal(MODAL_ID)).text("Show").__()

				// Start of modal dialog
				.of(parent -> Modal.content(parent, ModalOptions.of(MODAL_ID, "Test title", true), innerDiv -> {
					innerDiv
						.p().text("Press hide button, cross button or Escape key to close").__()
						.button().attrClass(CSS.BUTTON).attrOnclick(JS.hideModal(MODAL_ID)).text("Hide").__();
				}))
				// End of modal dialog

			.__(); // div
		// @formatter:on
		return null;
	}

	@Override
	public void requestHandle(HttpExchange he, BodyModel bm) throws Exception {
		HeadModel thm = HeadModel.of(NAME);
		BreadCrumbs crumbs = new BreadCrumbs().add(SandpitHandler.NAME, SandpitHandler.PATH).add(NAME, PATH);
		TemplatePageModel tpm = TemplatePageModel.of(ModalTestHandler::content, null, thm, SelectedPage.Sandpit, bm, crumbs);
		requestHandleCompletePage(he, tpm);
	}

	@Override
	public Permission getHandlePermission() {
		// TODO Auto-generated method stub
		return null;
	}

}
