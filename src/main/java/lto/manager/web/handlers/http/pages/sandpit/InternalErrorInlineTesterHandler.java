package lto.manager.web.handlers.http.pages.sandpit;

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

public class InternalErrorInlineTesterHandler extends BaseHTTPHandler {
	public static final String PATH = Asset.PATH_SANDPIT_BASE + "internalerrorinline/";
	public static final String NAME = "Internal Error Inline";

	static Void content(Div<?> view, BodyModel model) {
		view.div().attrId("inline-example").text("This text should be overridden").__()
		.script()
			.raw("const inline = document.getElementById('inline-example');"
					+ "document.addEventListener('DOMContentLoaded', function(){"
					+ "fetch('" + InternalErrorInlineAJAXTesterHandler.PATH + "',"
					+ "	{"
					+ "		method: 'GET',"
					+ "		signal: AbortSignal.timeout(3000)"
					+ "	}).then((response) => {"
					+ "		return response.text();"
					+ "	}).then((text) => {"
					+ "		inline.innerHTML = text;"
					+ "	}).catch((error) => {"
					+ "		inline.innerHTML = error;"
					+ "	});"
					+ "});")
		.__();
		return null;
	}

	@Override
	public void requestHandle(HttpExchange he, BodyModel bm) throws IOException, InterruptedException, ExecutionException {
		HeadModel thm = HeadModel.of(NAME);
		BreadCrumbs crumbs = new BreadCrumbs().add(SandpitHandler.NAME, SandpitHandler.PATH).add(NAME, PATH);
		TemplatePageModel tpm = TemplatePageModel.of(InternalErrorInlineTesterHandler::content, null, thm, SelectedPage.Sandpit, bm, crumbs);
		requestHandleCompletePage(he, tpm);
	}

	@Override
	public Permission getHandlePermission() {
		// TODO Auto-generated method stub
		return null;
	}

}
