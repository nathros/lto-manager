package lto.manager.web.handlers.templates;

import org.xmlet.htmlapifaster.EnumTypeInputType;

import htmlflow.DynamicHtml;
import lto.manager.common.Main;
import lto.manager.web.handlers.AdminHandler;
import lto.manager.web.handlers.BaseHandler;
import lto.manager.web.handlers.FilesHandler;
import lto.manager.web.handlers.TapesHandler;
import lto.manager.web.handlers.sandpit.SandpitHandler;
import lto.manager.web.handlers.templates.TemplateHead.TemplateHeadModel;

public class TemplatePage {
	public static enum SelectedPage {
		Admin,
		Tapes,
		Files,
		Sandpit,
		Missing
	}

	public static class TemplatePageModel {
		final DynamicHtml<?> dynamicHtml;
		final TemplateHeadModel head;
		final SelectedPage page;

		private TemplatePageModel(DynamicHtml<?> dynamicHtml, TemplateHeadModel head, SelectedPage page) {
			this.dynamicHtml = dynamicHtml;
			this.head = head;
			this.page = page;
		}

		public static TemplatePageModel of(DynamicHtml<?> dynamicHtml, TemplateHeadModel head, SelectedPage page) {
			return new TemplatePageModel(dynamicHtml, head, page);
		}
	}

	public static DynamicHtml<TemplatePageModel> view = DynamicHtml.view(TemplatePage::template);

	static void template(DynamicHtml<TemplatePageModel> view, TemplatePageModel model) {
		view
			.html().attrLang(BaseHandler.LANG_VALUE)
				.dynamic(head -> view.addPartial(TemplateHead.view, TemplateHead.TemplateHeadModel.of(model.head.title)))
				.body()
					.header().attrClass("header-root")
					.div().attrClass("header-root-container")
						.div().attrId("nav-toggle")
							.input().attrType(EnumTypeInputType.CHECKBOX).__()
							.div() // Menu Icon
								.span().__()
								.span().__()
								.span().__()
							.__() // div
							.ul().attrId("nav-menu")//selected
								.li()
									.a().dynamic(a -> a.attrHref(AdminHandler.PATH)
										.attrClass("icon-admin" + (model.page == SelectedPage.Admin ? " selected" : ""))
										.text("Admin")).__()
								.__()
								.li()
									.a().dynamic(a -> a.attrHref(TapesHandler.PATH)
										.attrClass("icon-tape" + (model.page == SelectedPage.Tapes ? " selected" : ""))
										.text("Tapes")).__()
								.__()
								.li()
									.a().dynamic(a -> a.attrHref(FilesHandler.PATH)
										.attrClass("icon-folder" + (model.page == SelectedPage.Files ? " selected" : ""))
										.text("Files")).__()
								.__()
								.li().dynamic(li -> {
									if (Main.DEBUG_MODE)
										li.a()
										.attrHref(SandpitHandler.PATH).attrClass("icon-sandpit" + (model.page == SelectedPage.Sandpit ? " selected" : ""))
										.text("Sandpit").__();
								})
							.__()
						.__() // div nav-toggle
					.__() // div header-root-container
				.__() // header
				.div().attrClass("main-content")
					.div().attrClass("nav-area").__()
					.div().attrClass("main-content-wrapper")
						.dynamic(div -> view.addPartial(model.dynamicHtml, null))
					.__() // div
				.__() //div
			.__() // body
		.__(); // html
	}
}
