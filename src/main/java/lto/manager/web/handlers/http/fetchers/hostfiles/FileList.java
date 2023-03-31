package lto.manager.web.handlers.http.fetchers.hostfiles;

import java.io.File;

import org.xmlet.htmlapifaster.Div;
import org.xmlet.htmlapifaster.EnumTypeInputType;

import htmlflow.DynamicHtml;
import htmlflow.HtmlView;
import lto.manager.common.fileview.PathTreeBase;
import lto.manager.web.resource.CSS;
import lto.manager.web.resource.JS;

public class FileList {

	public static HtmlView<FileListModel> view = DynamicHtml.view(FileList::template);

	static void template(DynamicHtml<FileListModel> view, FileListModel model) {
		final var finalView = view;
		try {
			Div<HtmlView<FileListModel>> wrapper = null;
			if (model.getTree().getDepth() == 0) {
				if (model.getOptions().showRoot()) {
					wrapper = view.div().attrId(CSS.FV_ID).attrClass(CSS.FV_ROOT);
					String bread = model.getOptions().breadcrumbs();
					if (!bread.contains(model.getTree().getAbsolutePath())) {
						bread = model.getTree().getAbsolutePath();
					}
					final String breadcrumbsPath = bread;
					wrapper
						.input()
							.attrStyle("width:500px")
							.attrType(EnumTypeInputType.HIDDEN)
							.attrId(FileListModel.BREADCRUMBS_LAST)
							.attrValue(breadcrumbsPath)
						.__()
						.of(n -> {
							String[] split = breadcrumbsPath.split(File.separator);
							String currentPath = "";
							var container = n.div().attrClass(CSS.FV_BREADCRUMB_CONTAINER);
							int currentIndex = model.getTree().getAbsolutePath().split(File.separator).length;
							currentIndex--;
							if (currentIndex == 0) currentIndex++;
							String path = "";
							for (int i = 0; i < split.length; i++) {
								path += split[i] + File.separator;
								if (i == currentIndex) currentPath = path;
								if (split[i].equals("")) split[i] = File.separator;
								container.button()
									.attrClass(CSS.BUTTON + (i == currentIndex ? CSS.BACKGROUND_ACTIVE : ""))
									.attrOnclick(JS.fnFileViewListChangeDir(path, model.getOptions().isVirtual()))
									.text(split[i])
								.__();
							}
							container.button()
								.attrClass(CSS.BUTTON + CSS.BUTTON_IMAGE + CSS.ICON_EDIT + CSS.FV_BREADCRUMB_EDIT_BTN)
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
								.__()
								.button()
									.attrClass(CSS.BUTTON + CSS.BUTTON_IMAGE + CSS.ICON_CHECK)
									.attrOnclick(JS.fnFileViewListChangeDirManual(model.getOptions().isVirtual()))
									.attrStyle("float:right")
								.__()
							.__();

							container.__();
						});
				}
				finalView.addPartial(FileListItem.view, model);
			}

			if (model.getTree().isDirectory()) {
				view.defineRoot().ul().of(ul -> {
					var children = model.getTree().getChildren();
					if (children.size() == 0 && model.getTree().getDepth() == 0) {
						ul.li().span().text("Empty").__().__(); // Open empty dir
					} else {
						for (PathTreeBase child: children) {
							if (model.showItem(child)) {
								var li = ul.li();
								model.setTree(child);
								finalView.addPartial(FileListItem.view, model);
								if (child.isDirectory()) {
									finalView.addPartial(FileList.view, model);
								}
								li.__();
							}
						}
					}
				}).__();

			} else {
				view.defineRoot().li().span().text(model.getTree().getName()).__().__();
			}
			if (wrapper != null) wrapper.__();
		 } catch (Exception e) {
			 view = DynamicHtml.view(FileList::template);
			 throw e;
		}
	 }
}
