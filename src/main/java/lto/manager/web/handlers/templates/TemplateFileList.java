package lto.manager.web.handlers.templates;

import htmlflow.DynamicHtml;
import htmlflow.HtmlView;
import lto.manager.common.Util;
import lto.manager.common.fileselector.PathTree;
import lto.manager.web.handlers.files.FilesAddHandler;
import lto.manager.web.resource.CSS;

public class TemplateFileList {

	public static HtmlView<PathTree> view = DynamicHtml.view(TemplateFileList::template);

	 static void template(DynamicHtml<PathTree> view, PathTree fileTree) {
		 final var finalView = view;
		 final String LINK = FilesAddHandler.PATH + "?" + FilesAddHandler.DIR + "=";

		 try {
			if (fileTree.getFile().isDirectory()) {
				view.defineRoot().ul().of(ul -> {
					if (fileTree.getDepth() == 0) ul.attrClass(CSS.FV_ROOT);

					for (PathTree child: fileTree.getChildren()) {
						var li = ul.li();
						if (child.getFile().isDirectory()) {
							li.span()
								.a().attrOnclick("hideFileTree(this)").text("+").__()
								.a()
									.attrHref(LINK + Util.encodeUrl(child.getFile().getAbsolutePath())).text(child.getFile().getName())
								.__()
								.span()
									.attrClass(CSS.FV_ICON_SIZE).attrOnclick("sort(this,'data-size')")
									.em().text("Sort by file size").__()
								.__()
								.span()
									.attrClass(CSS.FV_ICON_MODIFIED).attrOnclick("sort(this,'data-time')")
									.em().text("Sort by modified").__()
								.__()
								.span()
									.attrClass(CSS.FV_ICON_NAME).attrOnclick("sort(this,'data-name')")
									.em().text("Sort by name").__()
								.__()
							.__();
						} else { // File
							li.span()
								.addAttr("data-size", String.valueOf(child.getFileSizeBytes()))
								.addAttr("data-time", String.valueOf(child.getModifiedDateTimeLong()))
								.addAttr("data-name", String.valueOf(child.getFile().getName()))
								.a()
									.attrHref(LINK + Util.encodeUrl(child.getFile().getAbsolutePath())).text(child.getFile().getName())
								.__()
								.b().attrClass(CSS.FV_FILE_DETAILS_CONTAINER)
									.em().attrClass(CSS.FV_FILE_DETAILS).text(child.getFileSizeHR()).__()
									.em().attrClass(CSS.FV_FILE_DETAILS).text(child.getModifiedDateTimeStr()).__()
								.__()
							.__();
						}

						if (child.getFile().isDirectory()) {
							finalView.addPartial(TemplateFileList.view, child);
						}
						li.__();
					}
				}).__();
			} else {
				view.defineRoot().li().span().text(fileTree.getFile().getName()).__().__();
			}
		 } catch (Exception e) {
			 view = DynamicHtml.view(TemplateFileList::template);
			 throw e;
		}

	 }
}
