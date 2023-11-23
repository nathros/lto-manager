package lto.manager.web.handlers.http.pages.admin;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import org.xmlet.htmlapifaster.Div;
import org.xmlet.htmlapifaster.EnumBorderType;

import com.sun.net.httpserver.HttpExchange;

import lto.manager.common.state.State;
import lto.manager.web.handlers.http.BaseHTTPHandler;
import lto.manager.web.handlers.http.templates.TemplatePage.SelectedPage;
import lto.manager.web.handlers.http.templates.TemplatePage.TemplatePageModel;
import lto.manager.web.handlers.http.templates.models.BodyModel;
import lto.manager.web.handlers.http.templates.models.HeadModel;
import lto.manager.web.resource.CSS;

public class SessionViewerHandler extends BaseHTTPHandler {
	public static SessionViewerHandler self = new SessionViewerHandler();
	public static final String PATH = "/admin/session";

	static Void content(Div<?> view, BodyModel model) {
		view
		.div().of(div -> {
			div
			.a().attrClass(CSS.BUTTON).attrOnclick("history.back()").text("Back").__()
				.table().attrClass(CSS.TABLE).of(table -> {
					table.attrBorder(EnumBorderType._1).tr()
						.th().text("Session ID").__()
						.th().text("Username").__()
					.__();
					final var sessions = State.getAllSessions();
					for (UUID item : sessions.keySet()) {
						table.tr()
							.td().text(item.toString()).__()
							.td().text(sessions.get(item).getUsername()).__()
						.__();
					}
				}).__()
			.__(); // div
		}).__(); //  div
		return null;
	}

	@Override
	public void requestHandle(HttpExchange he) throws IOException, InterruptedException, ExecutionException {
		HeadModel thm = HeadModel.of("Login Sessions");
		TemplatePageModel tpm = TemplatePageModel.of(SessionViewerHandler::content, thm, SelectedPage.Admin, BodyModel.of(he, null));
		requestHandleCompletePage(he, tpm);
	}

}
