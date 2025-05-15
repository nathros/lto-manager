package lto.manager.web.handlers.http.ajax;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import org.xmlet.htmlapifaster.Div;

import com.sun.net.httpserver.HttpExchange;

import lto.manager.common.database.tables.records.RecordRole.Permission;
import lto.manager.web.handlers.http.BaseHTTPHandler;
import lto.manager.web.handlers.http.partial.inlinemessage.InlineMessage;
import lto.manager.web.handlers.http.templates.TemplateAJAX.TemplateFetcherModel;
import lto.manager.web.handlers.http.templates.models.BodyModel;
import lto.manager.web.resource.Asset;
import lto.manager.web.resource.CSS;

public class AJAXGetInlineMessage extends BaseHTTPHandler {
	public static final String PATH = Asset.PATH_AJAX_BASE + "inlinemessage/";

	static Void content(Div<?> view, BodyModel model) {
		final String title = model.getQuery("title");
		final String message = model.getQuery("message");
		final String extraPadding = model.getQuery("p");
		final InlineMessageType type = InlineMessageType.valueOf(model.getQuery("type"));
		if (type == InlineMessageType.good) {
			if (extraPadding != null) {
				view.of(div -> InlineMessage.contentGenericOK(CSS.GROUP, div, title));
			} else {
				view.of(div -> InlineMessage.contentGenericOK(div, title));
			}
		} else if (type == InlineMessageType.warning) {
			if (extraPadding != null) {
				view.of(div -> InlineMessage.contentGenericWarning(div, title));
			} else {
				view.of(div -> InlineMessage.contentGenericWarning(CSS.GROUP, div, title));
			}
		} else {
			if (extraPadding != null) {
				view.of(div -> InlineMessage.contentGenericError(div, title, message));
			} else {
				view.of(div -> InlineMessage.contentGenericError(CSS.GROUP, div, title, message));
			}
		}
		return null;
	}

	@Override
	public void requestHandle(HttpExchange he, BodyModel bm) throws IOException, InterruptedException, ExecutionException {
		requestHandleCompleteFetcher(he, new TemplateFetcherModel(AJAXGetInlineMessage::content, bm));
	}

	@Override
	public Permission getHandlePermission() {
		// TODO Auto-generated method stub
		return null;
	}

	private enum InlineMessageType {
		good, error, warning, info;
    }

}
