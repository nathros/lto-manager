package lto.manager.web.handlers.http.fetchers.hostfiles;

import java.io.File;

import org.xmlet.htmlapifaster.Div;
import org.xmlet.htmlapifaster.EnumTypeInputType;

import htmlflow.DynamicHtml;
import htmlflow.HtmlView;
import lto.manager.common.fileselector.PathTree;
import lto.manager.web.resource.CSS;
import lto.manager.web.resource.JS;

public class HostFileList {

	public static HtmlView<HostFileListModel> view = DynamicHtml.view(HostFileList::template);

	static void template(DynamicHtml<HostFileListModel> view, HostFileListModel model) {
		final var finalView = view;
		try {
			Div<HtmlView<HostFileListModel>> wrapper = null;
			if (model.getTree().getDepth() == 0) {
				if (model.getShowRoot()) {
					wrapper = view.div().attrId(CSS.FV_ID).attrClass(CSS.FV_ROOT);
					String bread = model.getBreadcrumbs();
					if (!bread.contains(model.getTree().getFile().getAbsolutePath())) {
						bread = model.getTree().getFile().getAbsolutePath();
					}
					final String breadcrumbsPath = bread;
					wrapper
						.input()
							.attrStyle("width:500px")
							.attrType(EnumTypeInputType.HIDDEN)
							.attrId(HostFileListModel.BREADCRUMBS_LAST)
							.attrValue(breadcrumbsPath)
						.__()
						.button()
							.attrClass(CSS.BUTTON + CSS.BACKGROUND_CAUTION)
							.attrOnclick(JS.fnHostFileListChangeDir(model.getTree().getFile().getParent()))
							.text("&#x2191")
						.__()
						.of(n -> {
							String[] split = breadcrumbsPath.split(File.separator);

							int currentIndex = model.getTree().getFile().getAbsolutePath().split(File.separator).length;
							currentIndex--;
							if (currentIndex == 0) currentIndex++;
							String path = "";
							for (int i = 0; i < split.length; i++) {
								path += split[i] + File.separator;
								if (split[i].equals("")) split[i] = File.separator;

								n.button()
									.attrClass(CSS.BUTTON + (i == currentIndex ? CSS.BACKGROUND_ACTIVE : ""))
									.attrOnclick(JS.fnHostFileListChangeDir(path))
									.text(split[i])
								.__();
							}
						});
				}
				finalView.addPartial(HostFileListItem.view, model);
			}

			if (model.getTree().getFile().isDirectory()) {
				view.defineRoot().ul().of(ul -> {
					var children = model.getTree().getChildren();
					if (children.size() == 0 && model.getTree().getDepth() == 0) {
						ul.li().span().text("Empty").__().__(); // Open empty dir
					} else {
						for (PathTree child: children) {
							if (model.showItem(child)) {
								var li = ul.li();
								model.setTree(child);
								finalView.addPartial(HostFileListItem.view, model);
								if (child.getFile().isDirectory()) {
									finalView.addPartial(HostFileList.view, model);
								}
								li.__();
							}
						}
					}
				}).__();

			} else {
				view.defineRoot().li().span().text(model.getTree().getFile().getName()).__().__();
			}
			if (wrapper != null) wrapper.__();
		 } catch (Exception e) {
			 view = DynamicHtml.view(HostFileList::template);
			 throw e;
		}
	 }
}
