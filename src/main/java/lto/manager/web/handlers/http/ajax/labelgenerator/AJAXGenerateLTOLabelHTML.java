package lto.manager.web.handlers.http.ajax.labelgenerator;

import java.util.List;

import org.xmlet.htmlapifaster.Div;

import com.sun.net.httpserver.HttpExchange;

import lto.manager.web.handlers.http.BaseHTTPHandler;
import lto.manager.web.handlers.http.templates.TemplateAJAX.TemplateFetcherModel;
import lto.manager.web.handlers.http.templates.models.BodyModel;

public class AJAXGenerateLTOLabelHTML extends BaseHTTPHandler {
	public static final String PATH = "/ajax/generate/lto/label/html";

	public static Void content(Div<?> view, BodyModel model) {
		view
			.of(o -> {
				List<String> labelsSVGs = GenerateLTOLabelSVG.generate(LTOLabelOptions.of(model));
				for (final String svg: labelsSVGs) {
					o.text(svg);
				}
			});
		return null;
	}

	@Override
	public void requestHandle(HttpExchange he) throws Exception {
		BodyModel bm = BodyModel.of(he, null);
		requestHandleCompleteFetcher(he, new TemplateFetcherModel(AJAXGenerateLTOLabelHTML::content, bm));
	}
}
