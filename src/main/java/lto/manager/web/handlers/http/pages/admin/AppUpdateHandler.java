package lto.manager.web.handlers.http.pages.admin;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.concurrent.ExecutionException;

import org.xmlet.htmlapifaster.Div;

import com.sun.net.httpserver.HttpExchange;

import lto.manager.web.handlers.http.BaseHTTPHandler;
import lto.manager.web.handlers.http.templates.TemplatePage.BreadCrumbs;
import lto.manager.web.handlers.http.templates.TemplatePage.SelectedPage;
import lto.manager.web.handlers.http.templates.TemplatePage.TemplatePageModel;
import lto.manager.web.handlers.http.templates.models.BodyModel;
import lto.manager.web.handlers.http.templates.models.HeadModel;

public class AppUpdateHandler extends BaseHTTPHandler {
	public static final String PATH = "/admin/update/";
	public static final String NAME = "Update";

	static Void content(Div<?> view, BodyModel model) {
		String link = "https://raw.githubusercontent.com/nathros/lto-manager/main/start-server.sh";
		String output = "/tmp/down.file";
		try (BufferedInputStream in = new BufferedInputStream(new URL(link).openStream());
				FileOutputStream fileOutputStream = new FileOutputStream(output)) {
			byte dataBuffer[] = new byte[1024];
			int bytesRead;
			while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
				fileOutputStream.write(dataBuffer, 0, bytesRead);
			}
		} catch (IOException e) {
			// handle exception
		}
		view.div().text("Updater WIP").__(); // div
		return null;
	}

	@Override
	public void requestHandle(HttpExchange he) throws IOException, InterruptedException, ExecutionException {
		HeadModel thm = HeadModel.of(NAME);
		BreadCrumbs crumbs = new BreadCrumbs().add(AdminHandler.NAME, AdminHandler.PATH).add(NAME, PATH);
		TemplatePageModel tpm = TemplatePageModel.of(AppUpdateHandler::content, null, thm, SelectedPage.Admin,
				BodyModel.of(he, null), crumbs);
		requestHandleCompletePage(he, tpm);
	}

}
