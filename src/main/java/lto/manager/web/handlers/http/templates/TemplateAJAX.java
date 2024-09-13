package lto.manager.web.handlers.http.templates;

import java.util.function.BiFunction;

import org.xmlet.htmlapifaster.Div;

import htmlflow.HtmlFlow;
import htmlflow.HtmlPage;
import htmlflow.HtmlView;
import lto.manager.web.handlers.http.templates.models.BodyModel;

public class TemplateAJAX {
	private static HtmlView<TemplateFetcherModel> v = HtmlFlow.view(TemplateAJAX::template);
	public static HtmlView<TemplateFetcherModel> view = v.threadSafe().setIndented(false);

	public static class TemplateFetcherModel {
		final BiFunction<Div<?>, BodyModel, Void> contentFunction;
		final BodyModel model;
		final boolean removeParentDiv;

		public TemplateFetcherModel(BiFunction<Div<?>, BodyModel, Void> contentFunction, BodyModel model) {
			this.contentFunction = contentFunction;
			this.model = model;
			this.removeParentDiv = false;
		}

		public TemplateFetcherModel(BiFunction<Div<?>, BodyModel, Void> contentFunction, BodyModel model,
				boolean removeParentDiv) {
			this.contentFunction = contentFunction;
			this.model = model;
			this.removeParentDiv = removeParentDiv;
		}

		public void render(Div<?> div) {
			contentFunction.apply(div, model);
		}

		public boolean getRemoveParentDiv() {
			return removeParentDiv;
		}
	}

	public static void template(HtmlPage view) {
		view.div().<TemplateFetcherModel>dynamic((v, model) -> model.render(v)).__();
	}
}
