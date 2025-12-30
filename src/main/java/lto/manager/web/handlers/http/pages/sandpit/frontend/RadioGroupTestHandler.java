package lto.manager.web.handlers.http.pages.sandpit.frontend;

import java.util.Arrays;

import org.xmlet.htmlapifaster.Div;

import com.sun.net.httpserver.HttpExchange;

import lto.manager.common.database.tables.records.RecordRole.Permission;
import lto.manager.web.check.element.input.ElementInputRadio;
import lto.manager.web.check.element.input.ElementInputRadio.ElementRadioOption;
import lto.manager.web.handlers.http.BaseHTTPHandler;
import lto.manager.web.handlers.http.pages.sandpit.SandpitHandler;
import lto.manager.web.handlers.http.templates.TemplatePage.BreadCrumbs;
import lto.manager.web.handlers.http.templates.TemplatePage.SelectedPage;
import lto.manager.web.handlers.http.templates.TemplatePage.TemplatePageModel;
import lto.manager.web.handlers.http.templates.models.BodyModel;
import lto.manager.web.handlers.http.templates.models.HeadModel;
import lto.manager.web.resource.Asset;

public class RadioGroupTestHandler extends BaseHTTPHandler {
	public static final String PATH = Asset.PATH_SANDPIT_BASE + "radio-group/";
	public static final String NAME = "Radio Group";

	static Void content(Div<?> view, BodyModel model) {
		final var items = Arrays.asList(new ElementRadioOption("First", "First", false),
				new ElementRadioOption("Second", "Second", true), new ElementRadioOption("Third", "Third", false), new ElementRadioOption("Forth", "Forth", false));
		final var radioG = ElementInputRadio.of("rad").withOptions(items);

		// @formatter:off
		view.
			div()
				.of(d -> radioG.render(d))
			.__(); // div
		// @formatter:on
		return null;
	}

	@Override
	public void requestHandle(HttpExchange he, BodyModel bm) throws Exception {
		HeadModel thm = HeadModel.of(NAME);
		BreadCrumbs crumbs = new BreadCrumbs().add(SandpitHandler.NAME, SandpitHandler.PATH).add(NAME, PATH);
		TemplatePageModel tpm = TemplatePageModel.of(RadioGroupTestHandler::content, null, thm, SelectedPage.Sandpit,
				bm, crumbs);
		requestHandleCompletePage(he, tpm);
	}

	@Override
	public Permission getHandlePermission() {
		// TODO Auto-generated method stub
		return null;
	}

}
