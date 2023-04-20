package lto.manager.web.handlers.http.templates;

import java.util.function.BiFunction;

import org.xmlet.htmlapifaster.Div;

import htmlflow.HtmlFlow;
import htmlflow.HtmlPage;
import htmlflow.HtmlView;
import lto.manager.web.handlers.http.templates.models.BodyModel;

public class TemplateFetcher {
	public static HtmlView view = HtmlFlow.view(TemplateFetcher::template);

	public static class TemplateFetcherModel {
		final BiFunction<Div<?>, BodyModel, Void> contentFunction;
		final BodyModel model;

		public TemplateFetcherModel(BiFunction<Div<?>, BodyModel, Void> contentFunction, BodyModel model) {
			this.contentFunction = contentFunction;
			this.model = model;
		}

		public void render(Div<?> div) {
			contentFunction.apply(div, model);
		}
	}

	public static void template(HtmlPage view) {
		view.div().<TemplateFetcherModel>dynamic((v, model) -> model.render(v)).__();
	}
}
