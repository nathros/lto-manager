package lto.manager.web.handlers;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;

import org.xmlet.htmlapifaster.EnumTypeButtonType;
import org.xmlet.htmlapifaster.EnumTypeInputType;

import com.sun.net.httpserver.HttpExchange;

import htmlflow.DynamicHtml;
import lto.manager.common.database.Options;
import lto.manager.common.database.tables.TableOptions;
import lto.manager.common.ltfs.ListTapeDevices;
import lto.manager.common.ltfs.ListTapeDevices.TapeDevicesInfo;
import lto.manager.web.handlers.templates.TemplateHead.TemplateHeadModel;
import lto.manager.web.handlers.templates.TemplatePage;
import lto.manager.web.handlers.templates.TemplatePage.SelectedPage;
import lto.manager.web.handlers.templates.TemplatePage.TemplatePageModel;
import lto.manager.web.handlers.templates.models.BodyModel;

public class AdminHandler extends BaseHandler {
	public static final String PATH = "/admin";
	public static DynamicHtml<BodyModel> view = DynamicHtml.view(AdminHandler::body);

	private final static String LIST_DRIVES = "list-drives";

	private final static String CHANGE_OPTIONS = "change-options";
	private final static String ENABLE_LOG_REQUESTS = "enable-requests";
	private final static String ENABLE_LOG_EXTERNAL_PROCESS = "enable-ext-log";

	private static ListTapeDevices devices = new ListTapeDevices();

	static void body(DynamicHtml<BodyModel> view, BodyModel model) {
		final boolean showDrives = model.getQueryNoNull(LIST_DRIVES).equals(BodyModel.QUERY_ON);

		if (showDrives) {
			try {
				if (!devices.operationInProgress()) devices.start();
			} catch (Exception e) {}
		}

		final String writeOptions = model.getQueryNoNull(CHANGE_OPTIONS);
		if (writeOptions.equals(BodyModel.QUERY_ON)) {
			String enableRequests = model.getQueryNoNull(ENABLE_LOG_REQUESTS);
			String enableExtLog = model.getQueryNoNull(ENABLE_LOG_EXTERNAL_PROCESS);

			if (enableRequests.equals(BodyModel.QUERY_ON)) Options.setBool(TableOptions.INDEX_ENABLE_LOG_REQUESTS, true);
			else Options.setBool(TableOptions.INDEX_ENABLE_LOG_REQUESTS, false);

			if (enableExtLog.equals(BodyModel.QUERY_ON)) Options.setBool(TableOptions.INDEX_ENABLE_LOG_EXTERNAL_PROCESS, true);
			else Options.setBool(TableOptions.INDEX_ENABLE_LOG_EXTERNAL_PROCESS, false);
		}

		view
		.div().dynamic(div -> {
			div
				.form()
					.input().attrType(EnumTypeInputType.HIDDEN).attrName(LIST_DRIVES).attrValue(BodyModel.QUERY_ON).__()
					.button().attrType(EnumTypeButtonType.SUBMIT).text("list Drives").__()
				.__();
			div
				.form()
					.button().attrType(EnumTypeButtonType.SUBMIT).text("Refresh").__()
				.__();

			if (devices.completed()) {
				if (devices.getDevices().size() > 0) {
					for (TapeDevicesInfo drive: devices.getDevices()) {
						div.b().text("Device Name: ").__().text(drive.getDeviceName()).br().__();
						div.b().text("Vender ID: ").__().text(drive.getVendorID()).br().__();
						div.b().text("Product ID: ").__().text(drive.getProductID()).br().__();
						div.b().text("Serial Number: ").__().text(drive.getSerialNumber()).br().__();
						div.b().text("Product Name: ").__().text(drive.getProductName()).br().__();
						div.hr().__();
					}
				} else {
					div.b().text("No devices found").__().br().__();
				}
			}

			div.hr().__();
			div
				.form()
					.fieldset()
						.legend().text("Options").__()
						.input().attrType(EnumTypeInputType.HIDDEN).attrName(CHANGE_OPTIONS).attrValue(BodyModel.QUERY_ON).__()

						.label().attrFor(ENABLE_LOG_REQUESTS).text("Enable request logging").__()
						.input().of(input -> {
							input.attrType(EnumTypeInputType.CHECKBOX).attrName(ENABLE_LOG_REQUESTS).attrId(ENABLE_LOG_REQUESTS);
							if (Options.getBool(TableOptions.INDEX_ENABLE_LOG_REQUESTS)) input.attrChecked(true);
						}).__()
						.br().__()

						.label().attrFor(ENABLE_LOG_EXTERNAL_PROCESS).text("Enable external process logging").__()
						.input().of(input -> {
							input.attrType(EnumTypeInputType.CHECKBOX).attrName(ENABLE_LOG_EXTERNAL_PROCESS).attrId(ENABLE_LOG_EXTERNAL_PROCESS);
							if (Options.getBool(TableOptions.INDEX_ENABLE_LOG_EXTERNAL_PROCESS)) input.attrChecked(true);
						}).__()
						.br().__()
						.button().attrType(EnumTypeButtonType.SUBMIT).text("Submit").__()
					.__()
				.__();
		}).__(); //  div

	}

	@Override
	public void requestHandle(HttpExchange he) throws IOException {
		try {
			TemplateHeadModel thm = TemplateHeadModel.of("Admin");
			TemplatePageModel tepm = TemplatePageModel.of(view, thm, SelectedPage.Admin, BodyModel.of(he, null));
			String response = TemplatePage.view.render(tepm);

			he.sendResponseHeaders(HttpURLConnection.HTTP_OK, response.length());
			OutputStream os = he.getResponseBody();
			os.write(response.getBytes());
			os.close();
		} catch (Exception e) {
			view = DynamicHtml.view(AdminHandler::body);
			throw e;
		}
	}
}
