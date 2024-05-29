package lto.manager.web.handlers.http.ajax;

import java.io.IOException;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import org.xmlet.htmlapifaster.Div;

import com.sun.net.httpserver.HttpExchange;

import lto.manager.common.ExternalProcess;
import lto.manager.common.ltfs.ListTapeDevices;
import lto.manager.web.handlers.http.BaseHTTPHandler;
import lto.manager.web.handlers.http.pages.AssetHandler;
import lto.manager.web.handlers.http.partial.inlinemessage.InlineMessage;
import lto.manager.web.handlers.http.templates.TemplateAJAX.TemplateFetcherModel;
import lto.manager.web.handlers.http.templates.models.BodyModel;
import lto.manager.web.resource.Asset;
import lto.manager.web.resource.CSS;

public class AJAXGetAttachedDrivesFetcher extends BaseHTTPHandler {
	public static final String PATH = Asset.PATH_AJAX_BASE + "driveslist/";
	private static int requestNumber = 0;

	static Void content(Div<?> view, BodyModel model) {
		final String uuid = String.valueOf(requestNumber);
		requestNumber++;
		final ListTapeDevices devices = new ListTapeDevices();
		try {
			Semaphore completedSemaphore = devices.startBlocking(uuid);
			boolean success = completedSemaphore.tryAcquire(5, TimeUnit.SECONDS);
			if (success == false || devices.getExitCode() != ExternalProcess.EXIT_CODE_OK) {
				view.of(v -> InlineMessage.contentExternalProcess(v, uuid));
			} else {
				view.of(v -> {
					for (var dev: devices.getDevices()) {
						final String logo = Asset.IMG_COMPANY_LOGOS + dev.getVendorID().toLowerCase() + ".svg";
						v.div().attrClass(CSS.DRIVE_CONTAINER)
							.b().text("Product: ").__()
							.span().text(dev.getProductName()).__()
							.b().text("Manufacturer: ").__()
							.of(o -> {
								if (AssetHandler.assetExists(logo)) {
									o.img().attrStyle("height:1rem").attrSrc(logo).__();
								} else {
									o.span().text(dev.getVendorID()).__();
								}
							})
							.b().text("Serial: ").__()
							.span().text(dev.getSerialNumber()).__()
							.b().text("Device Path: ").__()
							.span().text(dev.getDeviceName()).__()
						.__();
					}
				});
			}

		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}

	@Override
	public void requestHandle(HttpExchange he) throws Exception {
		BodyModel bm = BodyModel.of(he, null);
		requestHandleCompleteFetcher(he, new TemplateFetcherModel(AJAXGetAttachedDrivesFetcher::content, bm));
	}
}
