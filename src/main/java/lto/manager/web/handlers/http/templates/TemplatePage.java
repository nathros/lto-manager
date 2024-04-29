package lto.manager.web.handlers.http.templates;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

import org.apache.commons.lang3.tuple.Pair;
import org.xmlet.htmlapifaster.Div;
import org.xmlet.htmlapifaster.EnumTypeButtonType;
import org.xmlet.htmlapifaster.EnumTypeInputType;

import htmlflow.HtmlFlow;
import htmlflow.HtmlPage;
import htmlflow.HtmlView;
import lto.manager.common.Main;
import lto.manager.web.handlers.http.BaseHTTPHandler;
import lto.manager.web.handlers.http.pages.LogOutHandler;
import lto.manager.web.handlers.http.pages.RootHandler;
import lto.manager.web.handlers.http.pages.admin.AdminHandler;
import lto.manager.web.handlers.http.pages.drives.DrivesHandler;
import lto.manager.web.handlers.http.pages.files.FilesHandler;
import lto.manager.web.handlers.http.pages.jobs.JobsHandler;
import lto.manager.web.handlers.http.pages.library.LibraryHandler;
import lto.manager.web.handlers.http.pages.sandpit.SandpitHandler;
import lto.manager.web.handlers.http.partial.PartialHead;
import lto.manager.web.handlers.http.templates.models.BodyModel;
import lto.manager.web.handlers.http.templates.models.HeadModel;
import lto.manager.web.resource.Asset;
import lto.manager.web.resource.CSS;
import lto.manager.web.resource.HTML;
import lto.manager.web.resource.JS;

public class TemplatePage {
	public static enum SelectedPage {
		Admin,
		Library,
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
		final BiFunction<Div<?>, BodyModel, Void> mainContentFunction;
		final BiFunction<Div<?>, BodyModel, Void> headerButtonsFunction;
		final BreadCrumbs breadcrumbs;

		private TemplatePageModel(HeadModel head, SelectedPage page, BodyModel body, BiFunction<Div<?>, BodyModel, Void> mainContentFunction, BiFunction<Div<?>, BodyModel, Void> headerButtonsFunction, BreadCrumbs breadcrumbs) {
			this.head = head;
			this.page = page;
			this.body = body;
			this.mainContentFunction = mainContentFunction;
			this.headerButtonsFunction = headerButtonsFunction;
			this.breadcrumbs = breadcrumbs;
		}

		public static TemplatePageModel of(BiFunction<Div<?>, BodyModel, Void> func, BiFunction<Div<?>, BodyModel, Void> header, HeadModel head, SelectedPage page, BodyModel body, BreadCrumbs breadcrumbs) {
			return new TemplatePageModel(head, page, body, func, header, breadcrumbs);
		}

		public BodyModel getBodyModel() { return body; }
		public HeadModel getHeadModel() { return head; }
		public BiFunction<Div<?>, BodyModel, Void> getMainContent() { return mainContentFunction; }
		public BiFunction<Div<?>, BodyModel, Void> getHeaderContent() { return headerButtonsFunction; }
		public static BiFunction<Div<?>, BodyModel, Void> parametrisedMethod(BiFunction<Div<?>, BodyModel, Void> function) { return function; }
		public BreadCrumbs getBreadCrumbs() { return breadcrumbs; }
	}

	public static class BreadCrumbs {
		private List<Pair<String, String>> crumbs = new ArrayList<Pair<String, String>>();
		public BreadCrumbs() { add("", RootHandler.PATH); }
		public BreadCrumbs add(final String text, final String href) {
			this.crumbs.add(Pair.of(text, href));
			return this;
		}
		public final List<Pair<String, String>> getItems() { return crumbs; }
	}

	private static HtmlView<TemplatePageModel> v = HtmlFlow.view(TemplatePage::template);
	public static HtmlView<TemplatePageModel> view = v.threadSafe().setIndented(false);

	static void template(HtmlPage view) {
		final String selected = "selected";

		view
			.html().attrLang(BaseHTTPHandler.LANG_VALUE)
			.<TemplatePageModel>dynamic((root, bodyModel) -> PartialHead.template(root, bodyModel.getHeadModel()))
				.body()
					.div().attrId(CSS.TOAST_ID)
						.button().attrId(CSS.TOAST_ID_CROSS)
							.attrType(EnumTypeButtonType.BUTTON).attrOnclick(JS.commonHideToast()).text("x")
						.__()
						.div()
							.span().__()
							.p().attrId(CSS.TOAST_ID_MESSAGE).text("Empty").__()
						.__()
						.hr().__()
						.div()
							.button()
								.attrId(CSS.TOAST_ID_OK)
								.attrClass(CSS.BUTTON)
								.attrOnclick(JS.commonHideToast()).text("OK")
							.__()
							.button()
								.attrId(CSS.TOAST_ID_CANCEL)
								.attrClass(CSS.BUTTON)
								.attrOnclick(JS.commonHideToast()).text("Cancel")
							.__()
						.__()
					.__() // Toast
					.header().attrClass("header-root")
					.div().attrClass("header-root-container")
						.div().attrClass(CSS.HEADER_ITEM + "header-user")
							.ul().attrClass(CSS.MENU_LIST)
								.<TemplatePageModel>dynamic((ul, model) -> {
									final String user = model.getBodyModel().getUserNameViaSession();
									final String userShow = user == null ? "Anonymous User" : user;
									ul.li()
										.attrClass(CSS.HEADER_LABEL_TOP)
										.text(userShow)
									.__();
									if (user != null) {
										ul.li()
											.a()
												.attrClass(CSS.ICON_BOX_ARROW_RIGHT + CSS.HEADER_MENU_ITEM_ICON)
												.attrHref(LogOutHandler.PATH)
												.text("Log Out")
											.__()
										.__();
									}
								})
							.__()
						.__()
						.div()
							.attrClass(CSS.HEADER_ITEM + CSS.ICON_INFO_CIRCLE)
							.ul().attrClass(CSS.MENU_LIST)
								.li()
									.attrClass(CSS.HEADER_LABEL_TOP)
									.text("Help")
								.__()
								.li()
									.a()
										.attrHref("https://github.com/nathros/lto-manager")
										.attrTarget(HTML.TARGET_BLANK)
										.text("Documentation")
									.__()
								.__()
								.li()
									.a()
										.attrHref("https://github.com/nathros/lto-manager/issues")
										.attrTarget(HTML.TARGET_BLANK)
										.text("Report Bug")
									.__()
								.__()
							.__() // ul
						.__() // div
						.div()
							.attrClass(CSS.HEADER_ITEM + CSS.ICON_BELL)
							.ul().attrClass(CSS.MENU_LIST)
								.li()
									.attrClass(CSS.HEADER_LABEL_TOP)
									.text("Notifications")
								.__()
								.li()
									.a()
										.attrClass(CSS.ICON_BOX_ARROW_RIGHT)
										.text("Empty")
									.__()
								.__()
							.__() // ul
						.__() // div
						.div().attrStyle("color:white;opacity:50%;user-select:none;").text("|").__()
						.<TemplatePageModel>dynamic((div, model) -> {
							final var customHeaderIcons = model.getHeaderContent();
							if (customHeaderIcons != null) {
								customHeaderIcons.apply(div, model.getBodyModel());
							}
							final var crumb = model.getBreadCrumbs();
							if (crumb != null) {
								final List<Pair<String, String>> items = crumb.getItems();
								for (int i = items.size() - 1; i >= 0; i--) {
									final Pair<String, String> item = items.get(i);
									div.a().attrHref(item.getRight()).text(item.getLeft()).__();
									if (i != 0) div.span().text("/").__();
								}
							}
							div
								.div().attrClass("head-logo").of(d -> { if (crumb != null) d.attrStyle("margin-right:0"); })
									.img().attrSrc(Asset.IMG_LOGO).attrAlt("Logo").__()
									.text("LTO Manager")
								.__();
						})
						.div().attrId("nav-toggle")
							.input().attrType(EnumTypeInputType.CHECKBOX).__()
							.div() // Menu Icon
								.span().__()
								.span().__()
								.span().__()
							.__() // div
							.ul()
								.attrClass(CSS.MENU_LIST)
								.attrId("nav-menu")
								.<TemplatePageModel>dynamic((ul, model) -> {
									ul.li()
										.a().of(a -> a.attrHref(AdminHandler.PATH)
											.attrClass(CSS.ICON_ADMIN + CSS.HEADER_MENU_ITEM_ICON + (model.page == SelectedPage.Admin ? selected : ""))
											.text("Admin"))
										.__()
									.__()
									.li()
										.a().of(a -> a.attrHref(LibraryHandler.PATH)
											.attrClass(CSS.ICON_TAPE + CSS.HEADER_MENU_ITEM_ICON + (model.page == SelectedPage.Library ? selected : ""))
											.text("Library"))
										.__()
									.__()
									.li()
										.a().of(a -> a.attrHref(DrivesHandler.PATH)
											.attrClass(CSS.ICON_DRIVE + CSS.HEADER_MENU_ITEM_ICON + (model.page == SelectedPage.Drives ? selected : ""))
											.text("Drives"))
										.__()
									.__()
									.li()
										.a().of(a -> a.attrHref(FilesHandler.PATH)
											.attrClass(CSS.ICON_FOLDER + CSS.HEADER_MENU_ITEM_ICON + (model.page == SelectedPage.Files ? selected : ""))
											.text("Files"))
										.__()
									.__()
									.li()
										.a().of(a -> a.attrHref(JobsHandler.PATH)
											.attrClass(CSS.ICON_JOBS + CSS.HEADER_MENU_ITEM_ICON + (model.page == SelectedPage.Jobs ? selected : ""))
											.text("Jobs"))
										.__()
									.__()
									.li().of(li -> {
										if (Main.DEBUG_MODE) {
											li
												.a()
													.attrHref(SandpitHandler.PATH).attrClass(CSS.ICON_SANDPIT + CSS.HEADER_MENU_ITEM_ICON + (model.page == SelectedPage.Sandpit ? selected : ""))
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
					.div().attrClass("main-content-wrapper").attrId("main-content-wrapper")
						.<TemplatePageModel>dynamic((div, bodyModel) -> bodyModel.getMainContent().apply(div, bodyModel.getBodyModel()))
					.__() // div
				.__() //div
			.__() // body
		.__(); // html
	}
}
