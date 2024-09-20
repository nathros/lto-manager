package lto.manager.web.handlers.http.pages.sandpit;

import java.util.Arrays;

import org.xmlet.htmlapifaster.Div;
import org.xmlet.htmlapifaster.EnumMethodType;
import org.xmlet.htmlapifaster.EnumTypeButtonType;

import com.sun.net.httpserver.HttpExchange;

import lto.manager.common.database.tables.records.RecordRole.Permission;
import lto.manager.web.handlers.http.BaseHTTPHandler;
import lto.manager.web.handlers.http.partial.components.CheckBox;
import lto.manager.web.handlers.http.partial.components.CheckBox.CheckBoxOptions;
import lto.manager.web.handlers.http.templates.TemplatePage.BreadCrumbs;
import lto.manager.web.handlers.http.templates.TemplatePage.SelectedPage;
import lto.manager.web.handlers.http.templates.TemplatePage.TemplatePageModel;
import lto.manager.web.handlers.http.templates.models.BodyModel;
import lto.manager.web.handlers.http.templates.models.HeadModel;
import lto.manager.web.resource.Asset;
import lto.manager.web.resource.CSS;

public class ClearCacheHandler extends BaseHTTPHandler {
	public static final String PATH = Asset.PATH_SANDPIT_BASE + "clear-cache/";
	public static final String NAME = "Clear Cache";

	public static final String QCACHE = "cache";
	public static final String QCOOKIE = "cookie";
	public static final String QSTORAGE = "storage";

	static Void content(Div<?> view, BodyModel model) {
		final String cacheStr = model.getQuery(QCACHE);
		final String cookieStr = model.getQuery(QCOOKIE);
		final String storageStr = model.getQuery(QSTORAGE);

		final var cacheOpt = CheckBoxOptions.of().setID(QCACHE).setChecked(cacheStr != null);
		final var cookieOpt = CheckBoxOptions.of().setID(QCOOKIE).setChecked(cookieStr != null);
		final var storageOpt = CheckBoxOptions.of().setID(QSTORAGE).setChecked(storageStr != null);

		String tmpHeaderValue = "";
		if (cacheStr != null) {
			tmpHeaderValue += "\"cache\", ";
		}
		if (cookieStr != null) {
			tmpHeaderValue += "\"cookies\", ";
		}
		if (storageStr != null) {
			tmpHeaderValue += "\"storage\", ";
		}
		final String headerValue = tmpHeaderValue.length() == 0 ? ""
				: tmpHeaderValue.substring(0, tmpHeaderValue.length() - 2);

		if (headerValue.length() > 1) {
			model.getHttpExchange().getResponseHeaders().putIfAbsent("Clear-Site-Data", Arrays.asList(headerValue));
		}

		view
			.form()
				.attrMethod(EnumMethodType.GET)
				.attrAction("")
				.div()
					.attrClass(CSS.FORMS_CONTAINER)
					.b().text("Cache: ").__()
					.of(d -> CheckBox.content(d, cacheOpt))
					.b().text("Cookies: ").__()
					.of(d -> CheckBox.content(d, cookieOpt))
					.b().text("Storage: ").__()
					.of(d -> CheckBox.content(d, storageOpt))
				.__() // div
				.button()
					.attrClass(CSS.BUTTON)
					.attrType(EnumTypeButtonType.SUBMIT)
					.text("Submit")
				.__()
			.__() // form
			.div()
				.p()
					.attrStyle("color:red")
					.text("Note: clearing cookies will log you out")
				.__()
				.p()
					.attrStyle("color:red")
					.text("Note: this uses Clear-Site-Data in response header so it only works over localhost and secure contexts (HTTPS)")
				.__()
			.__()

			.div()
				.of(d -> {
					if (headerValue.length() > 1) {
						d.b()
							.text("Clear-Site-Data : " + headerValue)
							.__()
						.br().__()
						.text("Header added to HTTP response");
					}
				})
			.__(); // div
		return null;
	}

	@Override
	public void requestHandle(HttpExchange he, BodyModel bm) throws Exception {
		HeadModel thm = HeadModel.of(NAME);
		thm.addCSS(Asset.CSS_FORMS);
		BreadCrumbs crumbs = new BreadCrumbs().add(SandpitHandler.NAME, SandpitHandler.PATH).add(NAME, PATH);
		TemplatePageModel tpm = TemplatePageModel.of(ClearCacheHandler::content, null, thm, SelectedPage.Sandpit, bm, crumbs);
		requestHandleCompletePage(he, tpm);
	}

	@Override
	public Permission getHandlePermission() {
		// TODO Auto-generated method stub
		return null;
	}

}
