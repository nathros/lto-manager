package lto.manager.web.handlers.templates;

import htmlflow.DynamicHtml;
import htmlflow.HtmlView;
import lto.manager.common.Util;
import lto.manager.common.fileselector.PathTree;
import lto.manager.web.handlers.files.FilesAddHandler;

public class TemplateFileList {

	public static HtmlView<PathTree> view = DynamicHtml.view(TemplateFileList::template);

	 static void template(DynamicHtml<PathTree> view, PathTree fileTree) {
		 final var finalView = view;
		 final String LINK = FilesAddHandler.PATH + "?" + FilesAddHandler.DIR + "=";
		 try {
			if (fileTree.getFile().isDirectory()) {
				view.defineRoot().ul().of(ul -> {
					if (fileTree.getDepth() == 0) ul.attrClass("wtree");

					for (PathTree child: fileTree.getChildren()) {
						var li = ul.li();
						li.span()
							.a().attrOnclick("alert('dd')").text("+").__()
							.a()
								.attrHref(LINK + Util.encodeUrl(child.getFile().getAbsolutePath())).text(child.getFile().getName())
							.__()
						.__();
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
