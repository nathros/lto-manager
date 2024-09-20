package lto.manager.web.handlers.http.pages.sandpit.frontend;

import org.xmlet.htmlapifaster.Div;

import com.sun.net.httpserver.HttpExchange;

import lto.manager.common.database.tables.records.RecordRole.Permission;
import lto.manager.web.handlers.http.BaseHTTPHandler;
import lto.manager.web.handlers.http.pages.sandpit.SandpitHandler;
import lto.manager.web.handlers.http.partial.components.CheckBox;
import lto.manager.web.handlers.http.partial.components.CheckBox.CheckBoxOptions;
import lto.manager.web.handlers.http.templates.TemplatePage.BreadCrumbs;
import lto.manager.web.handlers.http.templates.TemplatePage.SelectedPage;
import lto.manager.web.handlers.http.templates.TemplatePage.TemplatePageModel;
import lto.manager.web.handlers.http.templates.models.BodyModel;
import lto.manager.web.handlers.http.templates.models.HeadModel;
import lto.manager.web.resource.Asset;

public class CheckBoxTestHandler extends BaseHTTPHandler {
	public static final String PATH = Asset.PATH_SANDPIT_BASE + "checkbox/";
	public static final String NAME = "Checkbox";

	static Void content(Div<?> view, BodyModel model) {
		final var opt1 = CheckBoxOptions.of("Unchecked");
		final var opt2 = CheckBoxOptions.of("Checked").setChecked(true);
		final var opt3 = CheckBoxOptions.of("Disabled Unchecked").setDisabled(true);
		final var opt4 = CheckBoxOptions.of("Disabled Checked").setChecked(true).setDisabled(true);
		final var opt5 = CheckBoxOptions.of().setChecked(true);

		view.
			div()
				.of(d -> CheckBox.content(d, opt1)).br().__()
				.of(d -> CheckBox.content(d, opt2)).br().__()
				.of(d -> CheckBox.content(d, opt3)).br().__()
				.of(d -> CheckBox.content(d, opt4)).br().__()
				.of(d -> CheckBox.content(d, opt5)).br().__()
			.__(); // div
		return null;
	}

	@Override
	public void requestHandle(HttpExchange he, BodyModel bm) throws Exception {
		HeadModel thm = HeadModel.of(NAME);
		thm.addCSS(Asset.CSS_FORMS);
		BreadCrumbs crumbs = new BreadCrumbs().add(SandpitHandler.NAME, SandpitHandler.PATH).add(NAME, PATH);
		TemplatePageModel tpm = TemplatePageModel.of(CheckBoxTestHandler::content, null, thm, SelectedPage.Sandpit, bm, crumbs);
		requestHandleCompletePage(he, tpm);
	}

	@Override
	public Permission getHandlePermission() {
		// TODO Auto-generated method stub
		return null;
	}

}
