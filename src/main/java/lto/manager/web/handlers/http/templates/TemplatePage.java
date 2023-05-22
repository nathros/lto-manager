package lto.manager.web.handlers.http.templates;

import java.util.function.BiFunction;

import org.xmlet.htmlapifaster.Div;
import org.xmlet.htmlapifaster.EnumTypeButtonType;
import org.xmlet.htmlapifaster.EnumTypeInputType;

import htmlflow.HtmlFlow;
import htmlflow.HtmlPage;
import htmlflow.HtmlView;
import lto.manager.common.Main;
import lto.manager.web.handlers.http.AdminHandler;
import lto.manager.web.handlers.http.BaseHTTPHandler;
import lto.manager.web.handlers.http.drives.DrivesHandler;
import lto.manager.web.handlers.http.files.FilesHandler;
import lto.manager.web.handlers.http.jobs.JobsHandler;
import lto.manager.web.handlers.http.sandpit.SandpitHandler;
import lto.manager.web.handlers.http.tapes.TapesHandler;
import lto.manager.web.handlers.http.templates.models.BodyModel;
import lto.manager.web.handlers.http.templates.models.HeadModel;
import lto.manager.web.resource.CSS;
import lto.manager.web.resource.JS;

public class TemplatePage {
	public static enum SelectedPage {
		Admin,
		Tapes,
		Drives,
		Files,
		Sandpit,
		Jobs,
		Missing
	}

	public static class TemplatePageModel {
		final HeadModel head;
		final SelectedPage page;
		final BodyModel body;
		final BiFunction<Div<?>, BodyModel, Void> contentFunction;

		private TemplatePageModel(HeadModel head, SelectedPage page, BodyModel body, BiFunction<Div<?>, BodyModel, Void> contentFunction) {
			this.head = head;
			this.page = page;
			this.body = body;
			this.contentFunction = contentFunction;
		}

		public static TemplatePageModel of(BiFunction<Div<?>, BodyModel, Void> func, HeadModel head, SelectedPage page, BodyModel body) {
			return new TemplatePageModel(head, page, body, func);
		}

		public BodyModel getBodyModel() { return body; }
		public HeadModel getHeadModel() { return head; }
		public BiFunction<Div<?>, BodyModel, Void> getContent() { return contentFunction; }
		public static BiFunction<Div<?>, BodyModel, Void> parametrisedMethod(BiFunction<Div<?>, BodyModel, Void> function) { return function; }
	}

	public static HtmlView view = HtmlFlow.view(TemplatePage::template);

	static void template(HtmlPage view) {
		final String selected = " selected";

		view
			.html().attrLang(BaseHTTPHandler.LANG_VALUE)
			.<TemplatePageModel>dynamic((root, bodyModel) -> TemplateHead.template(root, bodyModel.getHeadModel()))
				.body()
					.div().attrId(CSS.TOAST_ID)
						.button().attrId(CSS.TOAST_ID_CROSS)
							.attrType(EnumTypeButtonType.BUTTON).attrOnclick(JS.commonHideToast()).text("x")
						.__()
						.p().attrId(CSS.TOAST_ID_MESSAGE).text("Empty").__()
						.div()
							.button()
								.attrId(CSS.TOAST_ID_OK)
								.attrClass(CSS.BUTTON + CSS.BACKGROUND_ACTIVE)
								.attrOnclick(JS.commonHideToast()).text("OK")
							.__()
							.button()
								.attrId(CSS.TOAST_ID_CANCEL)
								.attrClass(CSS.BUTTON + CSS.BACKGROUND_ACTIVE)
								.attrOnclick(JS.commonHideToast()).text("Cancel")
							.__()
						.__()
					.__() // Toast
					.header().attrClass("header-root")
					.div().attrClass("header-root-container")
						.div().attrId("nav-toggle")
							.input().attrType(EnumTypeInputType.CHECKBOX).__()
							.div() // Menu Icon
								.span().__()
								.span().__()
								.span().__()
							.__() // div
							.ul()
								.attrId("nav-menu")
								.<TemplatePageModel>dynamic((ul, model) -> {
									ul.li()
										.a().of(a -> a.attrHref(AdminHandler.PATH)
											.attrClass(CSS.ICON_ADMIN + (model.page == SelectedPage.Admin ? selected : ""))
											.text("Admin"))
										.__()
									.__()
									.li()
										.a().of(a -> a.attrHref(TapesHandler.PATH)
											.attrClass(CSS.ICON_TAPE + (model.page == SelectedPage.Tapes ? selected : ""))
											.text("Tapes"))
										.__()
									.__()
									.li()
										.a().of(a -> a.attrHref(DrivesHandler.PATH)
											.attrClass(CSS.ICON_DRIVE + (model.page == SelectedPage.Drives ? selected : ""))
											.text("Drives"))
										.__()
									.__()
									.li()
										.a().of(a -> a.attrHref(FilesHandler.PATH)
											.attrClass(CSS.ICON_FOLDER + (model.page == SelectedPage.Files ? selected : ""))
											.text("Files"))
										.__()
									.__()
									.li()
										.a().of(a -> a.attrHref(JobsHandler.PATH)
											.attrClass(CSS.ICON_JOBS + (model.page == SelectedPage.Jobs ? selected : ""))
											.text("Jobs"))
										.__()
									.__()
									.li().of(li -> {
										if (Main.DEBUG_MODE) {
											li
												.a()
													.attrHref(SandpitHandler.PATH).attrClass(CSS.ICON_SANDPIT + (model.page == SelectedPage.Sandpit ? selected : ""))
													.text("Sandpit")
												.__();
										}
									}).__();
							}).__() // ul
						.__() // div nav-toggle
					.__() // div header-root-container
				.__() // header-root
				.div().attrClass("main-content")
					.div().attrClass("nav-area").__()
					.div().attrClass("main-content-wrapper")
						.<TemplatePageModel>dynamic((div, bodyModel) -> bodyModel.getContent().apply(div, bodyModel.getBodyModel()))
					.__() // div
				.__() //div
			.__() // body
		.__(); // html
	}
}
