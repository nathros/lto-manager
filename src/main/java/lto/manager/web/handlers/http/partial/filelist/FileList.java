package lto.manager.web.handlers.http.partial.filelist;

import java.io.File;

import org.xmlet.htmlapifaster.Div;
import org.xmlet.htmlapifaster.EnumTypeButtonType;
import org.xmlet.htmlapifaster.EnumTypeInputType;

import lto.manager.common.fileview.PathTreeBase;
import lto.manager.web.resource.CSS;
import lto.manager.web.resource.JS;

public class FileList {

	public static Void content(Div<?> view, FileListModel model) {
		final var finalView = view;
		Div<?> wrapper = null;
		if (model.getTree().getDepth() == 0) {
			if (model.getOptions().showRoot()) {
				wrapper = view
						.attrId(CSS.FV_ID + model.getIDPostFix())
						.attrClass(CSS.FV_ROOT);

				wrapper // Context menu
					.div() // All divs need tab index to be focusable, so can be hidden on blur
						.attrId(CSS.FV_ID_CONTEXT_CONTAINER + model.getIDPostFix())
						.attrClass(CSS.FV_CONTEXT_CONTAINER)
						.attrTabIndex(-1)
						.attrOnmouseup(JS.fnFileContextMenuHide(model.getOptions().isVirtual()))
						.div().attrClass(CSS.FV_CONTEXT_MENU)
							.attrTabIndex(-1)
								.div()
									.attrClass(CSS.FV_CONTEXT_MENU_ITEM)
									.attrTabIndex(-1)
									.text("New Folder")
								.__()
								.div()
									.attrClass(CSS.FV_CONTEXT_MENU_INPUT)
									.input()
										.attrType(EnumTypeInputType.TEXT)
										.attrPlaceholder("Name")
									.__()
									.button()
										.attrClass(CSS.BUTTON_SMALL + CSS.BUTTON_IMAGE + CSS.BACKGROUND_GREEN + CSS.ICON_CHECK)
										.attrOnclick(JS.fnFileNewVirtualDir(model.getTree().getAbsolutePath()))
									.__()
								.__()
								.div()
									.attrClass(CSS.FV_CONTEXT_MENU_ITEM)
									.attrTabIndex(-1)
									.text("Rename Folder")
								.__()
								.div()
									.attrClass(CSS.FV_CONTEXT_MENU_INPUT)
									.input()
										.attrType(EnumTypeInputType.TEXT)
										.attrPlaceholder("Name")
									.__()
									.button()
										.attrClass(CSS.BUTTON_SMALL + CSS.BUTTON_IMAGE + CSS.BACKGROUND_GREEN + CSS.ICON_CHECK)
									.__()
									.button()
										.attrClass(CSS.BUTTON_SMALL + CSS.BUTTON_IMAGE + CSS.BACKGROUND_CAUTION + CSS.ICON_CROSS)
									.__()
								.__()
								.div()
									.attrClass(CSS.FV_CONTEXT_MENU_ITEM)
									.attrTabIndex(-1)
									.text("Delete Folder")
								.__()
								.div()
									.attrClass(CSS.FV_CONTEXT_MENU_ITEM)
									.attrTabIndex(-1)
									.text("Change Icon")
								.__()
						.__()
					.__();

				// Breadcrumbs
				String bread = model.getOptions().breadcrumbs();
				if (!bread.contains(model.getTree().getAbsolutePath())) {
					bread = model.getTree().getAbsolutePath();
				}
				final String breadcrumbsPath = bread;
				wrapper
					.input()
						.attrType(EnumTypeInputType.HIDDEN)
						.attrId(FileListModel.BREADCRUMBS_LAST + model.getIDPostFix())
						.attrName(FileListModel.BREADCRUMBS_LAST + model.getIDPostFix())
						.attrValue(breadcrumbsPath)
					.__()
					.of(n -> {
						String[] split = breadcrumbsPath.split(File.separator);
						if (split.length == 0) split = new String[]{ "" };
						String currentPath = "";
						var container = n.div().attrClass(CSS.FV_BREADCRUMB_CONTAINER);
						int currentIndex = model.getTree().getAbsolutePath().split(File.separator).length;
						currentIndex--;
						if (currentIndex == 0) currentIndex++;
						else if (currentIndex <= 0) currentIndex = 0;
						String path = "";
						for (int i = 0; i < split.length; i++) {
							path += split[i] + File.separator;
							if (i == currentIndex) currentPath = path;
							if (split[i].equals("")) split[i] = File.separator;
							container.button()
								.attrClass(CSS.BUTTON + (i == currentIndex ? CSS.BACKGROUND_ACTIVE : ""))
								.attrType(EnumTypeButtonType.BUTTON)
								.attrTitle("")
								.attrOnclick(JS.fnFileViewListChangeDir(path, model.getOptions().isVirtual()))
								.text(split[i])
							.__();
						}
						container.button() // TODO on click this button is not hidden despite opacity 0 CSS, caused by JavaScript selectPathEditBox
							.attrClass(CSS.BUTTON + CSS.BUTTON_IMAGE + CSS.ICON_EDIT + CSS.FV_BREADCRUMB_EDIT_BTN)
							.attrType(EnumTypeButtonType.BUTTON)
							.attrTitle("Manually enter directory")
							.attrOnclick(JS.fnFileViewSelectPathEditBox())
							.attrOnfocus(JS.fnFileViewSelectPathEditBox())
							.attrStyle("float:right")
						.__();

						container.div()
							.attrClass(CSS.FV_BREADCRUMB_EDIT_CONTAINER)
							.input()
								.attrType(EnumTypeInputType.TEXT)
								.attrValue(currentPath)
								.attrOnkeyup(JS.fnFileViewKeyDownEditBox())
								.attrOnkeydown("return event.key != 'Enter';")
							.__()
							.button()
								.attrClass(CSS.BUTTON + CSS.BUTTON_IMAGE + CSS.ICON_CHECK)
								.attrType(EnumTypeButtonType.BUTTON)
								.attrTitle("Confirm directoty change")
								.attrOnclick(JS.fnFileViewListChangeDirManual(model.getOptions().isVirtual()))
								.attrStyle("float:right")
							.__()
						.__();

						container.__();
					});
				if (!model.getTree().exists()) {
					wrapper.p().text("Directory does not exist").__();
					return null;
				}
			}
			finalView.of(div -> FileListItem.content(div, model));
		}

		if (model.getTree().isDirectory()) {
			view.ul().of(ul -> {
				var children = model.getTree().getChildren();
				if (children.size() == 0 && model.getTree().getDepth() == 0) {
					ul.li().span().text("Empty").__().__(); // Open empty dir
				} else {
					for (PathTreeBase child: children) {
						if (model.showItem(child)) {
							var li = ul.li();
							model.setTree(child);
							finalView.of(div -> FileListItem.content(div, model));
							li.__();
						}
					}
				}
			}).__();

		}
		if (wrapper != null) {
			if (!model.getOptions().isVirtual()) {
				wrapper
					.div()
						.attrId(CSS.FV_ID_SELECT_TOTAL)
						.div()
							.text("Total Size: ")
							.b()
								.text("0")
							.__()
						.__()
					.__();
			}
		}
		return null;
	 }
}
