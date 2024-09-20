package lto.manager.web.handlers.http.pages.help;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import com.sun.net.httpserver.HttpExchange;

import htmlflow.HtmlFlow;
import htmlflow.HtmlPage;
import htmlflow.HtmlView;
import lto.manager.common.database.tables.records.RecordRole.Permission;
import lto.manager.web.handlers.http.BaseHTTPHandler;
import lto.manager.web.handlers.http.templates.TemplatePage.SelectedPage;
import lto.manager.web.handlers.http.templates.TemplatePage.TemplatePageModel;
import lto.manager.web.handlers.http.templates.models.BodyModel;
import lto.manager.web.handlers.http.templates.models.HeadModel;
import lto.manager.web.resource.Asset;

public class HelpHandler extends BaseHTTPHandler {
	private static HtmlView<TemplatePageModel> v = HtmlFlow.view(HelpHandler::content);
	public static HtmlView<TemplatePageModel> view = v.threadSafe().setIndented(false);
	public static final String PATH = "/help/";


	public static void content(HtmlPage view) {
		view
		.html()
			.attrLang(BaseHTTPHandler.LANG_VALUE)
		.<TemplatePageModel>dynamic((root, model) -> {
			root
			.head()
				.meta().addAttr(BaseHTTPHandler.CHARSET_KEY, BaseHTTPHandler.CHARSET_VALUE).__()
				.title().of(title -> title.text(model.getHeadModel().getTitle())).__()
				.link().addAttr(BaseHTTPHandler.ICON_KEY, BaseHTTPHandler.ICON_VALUE).attrHref(Asset.IMG_FAVICO_SVG).addAttr(BaseHTTPHandler.TYPE_KEY, BaseHTTPHandler.TYPE_SVG).__()
				.meta().attrName(BaseHTTPHandler.VIEWPORT_KEY).attrContent(BaseHTTPHandler.VIEWPORT_VALUE).__()
			.__() // head
			.body()
				.p().text("help page - todo").__()
			.__();
			})
		.__(); // html
	}

	@Override
	public void requestHandle(HttpExchange he, BodyModel bm) throws IOException, InterruptedException, ExecutionException {
		HeadModel thm = HeadModel.of("LTO Manager Help");
		TemplatePageModel tpm = TemplatePageModel.of(null, null, thm, SelectedPage.Missing, bm, null);
		requestHandleCompleteView(he, view, tpm);
	}

	@Override
	public Permission getHandlePermission() {
		// TODO Auto-generated method stub
		return null;
	}

}
