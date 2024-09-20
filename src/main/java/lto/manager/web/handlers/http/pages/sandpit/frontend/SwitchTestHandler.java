package lto.manager.web.handlers.http.pages.sandpit.frontend;

import org.xmlet.htmlapifaster.Div;

import com.sun.net.httpserver.HttpExchange;

import lto.manager.common.database.tables.records.RecordRole.Permission;
import lto.manager.web.handlers.http.BaseHTTPHandler;
import lto.manager.web.handlers.http.pages.sandpit.SandpitHandler;
import lto.manager.web.handlers.http.partial.components.Switch;
import lto.manager.web.handlers.http.partial.components.Switch.SwitchOptions;
import lto.manager.web.handlers.http.templates.TemplatePage.BreadCrumbs;
import lto.manager.web.handlers.http.templates.TemplatePage.SelectedPage;
import lto.manager.web.handlers.http.templates.TemplatePage.TemplatePageModel;
import lto.manager.web.handlers.http.templates.models.BodyModel;
import lto.manager.web.handlers.http.templates.models.HeadModel;
import lto.manager.web.resource.Asset;

public class SwitchTestHandler extends BaseHTTPHandler {
	public static final String PATH = Asset.PATH_SANDPIT_BASE + "switch/";
	public static final String NAME = "Switch";

	static Void content(Div<?> view, BodyModel model) {
		final var opt1 = SwitchOptions.of("Unchecked");
		final var opt2 = SwitchOptions.of("Checked").setChecked(true);
		final var opt3 = SwitchOptions.of("Disabled Unchecked").setDisabled(true);
		final var opt4 = SwitchOptions.of("Disabled Checked").setChecked(true).setDisabled(true);

		view.
			div()
				.of(d -> Switch.content(d, opt1)).br().__()
				.of(d -> Switch.content(d, opt2)).br().__()
				.of(d -> Switch.content(d, opt3)).br().__()
				.of(d -> Switch.content(d, opt4)).br().__()
			.__(); // div
		return null;
	}

	@Override
	public void requestHandle(HttpExchange he, BodyModel bm) throws Exception {
		HeadModel thm = HeadModel.of(NAME);
		thm.addCSS(Asset.CSS_FORMS);
		BreadCrumbs crumbs = new BreadCrumbs().add(SandpitHandler.NAME, SandpitHandler.PATH).add(NAME, PATH);
		TemplatePageModel tpm = TemplatePageModel.of(SwitchTestHandler::content, null, thm, SelectedPage.Sandpit, bm, crumbs);
		requestHandleCompletePage(he, tpm);
	}

	@Override
	public Permission getHandlePermission() {
		// TODO Auto-generated method stub
		return null;
	}

}
