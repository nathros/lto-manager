package lto.manager.web.handlers.http.ajax.labelgenerator;

import java.util.List;

import org.xmlet.htmlapifaster.Div;

import com.sun.net.httpserver.HttpExchange;

import lto.manager.common.database.tables.records.RecordRole.Permission;
import lto.manager.web.handlers.http.BaseHTTPHandler;
import lto.manager.web.handlers.http.templates.TemplateAJAX.TemplateFetcherModel;
import lto.manager.web.handlers.http.templates.models.BodyModel;
import lto.manager.web.resource.Asset;

public class AJAXGenerateLTOLabelHTML extends BaseHTTPHandler {
	public static final String PATH = Asset.PATH_AJAX_BASE + "generate/lto/label/html/";

	public static Void content(Div<?> view, BodyModel model) {
		List<String> labelsSVGs = GenerateLTOLabelSVG.generate(LTOLabelOptions.of(model));
		view
			.of(o -> {
				for (final String svg: labelsSVGs) {
					o.raw(svg);
				}
			});
		return null;
	}

	@Override
	public void requestHandle(HttpExchange he, BodyModel bm) throws Exception {
		requestHandleCompleteFetcher(he, new TemplateFetcherModel(AJAXGenerateLTOLabelHTML::content, bm));
	}

	@Override
	public Permission getHandlePermission() {
		return null;
	}
}
