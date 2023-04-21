package lto.manager.web.handlers.http;

import java.io.IOException;

import org.xmlet.htmlapifaster.Div;
import org.xmlet.htmlapifaster.EnumTypeButtonType;
import org.xmlet.htmlapifaster.EnumTypeInputType;

import com.sun.net.httpserver.HttpExchange;

import lto.manager.common.Util;
import lto.manager.common.database.Options;
import lto.manager.common.database.tables.TableOptions;
import lto.manager.common.ltfs.ListTapeDevices;
import lto.manager.common.ltfs.ListTapeDevices.TapeDevicesInfo;
import lto.manager.web.handlers.http.templates.TemplatePage.SelectedPage;
import lto.manager.web.handlers.http.templates.TemplatePage.TemplatePageModel;
import lto.manager.web.handlers.http.templates.models.BodyModel;
import lto.manager.web.handlers.http.templates.models.HeadModel;
import lto.manager.web.resource.Asset;

public class AdminHandler extends BaseHTTPHandler {
	public static AdminHandler self = new AdminHandler();
	public static final String PATH = "/admin";
	private final static String LIST_DRIVES = "list-drives";

	private final static String CHANGE_OPTIONS = "change-options";
	private final static String ENABLE_LOG_REQUESTS = "enable-requests";
	private final static String ENABLE_LOG_EXTERNAL_PROCESS = "enable-ext-log";

	private static ListTapeDevices devices = new ListTapeDevices();

	static Void content(Div<?> view, BodyModel model) {
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
		.div().of(div -> {
			final int maxMemoryMB = (int) (Util.getJVMMaxMemory() / 1024 / 1024);
			final int allocatedMB = (int) (Util.getJVMAllocatedMemory() / 1024 / 1024);
			final int usedMemMB = (int) ((Util.getJVMAllocatedMemory() - Util.getUsedMemory()) / 1024 / 1024);

			int p = (int) (((double)usedMemMB / (double)maxMemoryMB) * 100);
			div
				.p().text("AnalyticsHandler").__()
				.p().text("JVM max memory: " + maxMemoryMB + "MB").__()
				.p().text("JVM allocated memory: " + allocatedMB + "MB").__()
				.p().text("JVM used memory: " + usedMemMB + "MB").__()
				.p().text("percent used: " + p + "%").__()
				.div().attrClass("pie animate no-round").attrStyle("--p:" + p + ";--c:orange;").text(p + "%").__()
				.div().attrClass("pie animate").attrStyle("--p:66;--c:red;").text("66%-test").__()
			.__(); // div
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
		return null;
	}

	@Override
	public void requestHandle(HttpExchange he) throws IOException {
		HeadModel thm = HeadModel.of("Admin");
		thm.AddCSS(Asset.CSS_PIE);
		TemplatePageModel tpm = TemplatePageModel.of(AdminHandler::content, thm, SelectedPage.Admin, BodyModel.of(he, null));
		requestHandleCompletePage(he, tpm);
	}

}