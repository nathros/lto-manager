package lto.manager.web.handlers.http.drives.ajax;

import java.io.IOException;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import org.xmlet.htmlapifaster.Div;

import com.sun.net.httpserver.HttpExchange;

import lto.manager.common.ExternalProcess;
import lto.manager.common.ltfs.ListTapeDevices;
import lto.manager.web.handlers.http.BaseHTTPHandler;
import lto.manager.web.handlers.http.templates.TemplateFetcher.TemplateFetcherModel;
import lto.manager.web.handlers.http.templates.models.BodyModel;
import lto.manager.web.resource.CSS;

public class GetAttachedDrivesFetcher extends BaseHTTPHandler {
	public static final String PATH = "/ajax/driveslist";

	static Void content(Div<?> view, BodyModel model) {
		final ListTapeDevices devices = new ListTapeDevices();
		try {
			Semaphore binarySemaphore = devices.startBlocking();
			boolean success = binarySemaphore.tryAcquire(5, TimeUnit.SECONDS);
			if (success == false || devices.getExitCode() != ExternalProcess.EXIT_CODE_OK) {
				view.p()
					.text("fail")
				.__();
			} else {
				view.of(v -> {
					for (var dev: devices.getDevices()) {
						v.div().attrClass(CSS.DRIVE_CONTAINER)
							.b().text("Product: ").__()
							.span().text(dev.getProductName()).__()
							.b().text("Manufacturer: ").__()
							.span().text(dev.getVendorID()).__()
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
		requestHandleCompleteFetcher(he, new TemplateFetcherModel(GetAttachedDrivesFetcher::content, bm));
	}
}
