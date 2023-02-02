package lto.manager.web.handlers.templates;

import org.xmlet.htmlapifaster.Div;

import htmlflow.DynamicHtml;
import htmlflow.HtmlView;
import lto.manager.common.fileselector.PathTree;
import lto.manager.web.handlers.files.FilesAddHandler;
import lto.manager.web.resource.CSS;

public class TemplateFileList {

	public static HtmlView<PathTree> view = DynamicHtml.view(TemplateFileList::template);

	 static void template(DynamicHtml<PathTree> view, PathTree fileTree) {
		final var finalView = view;
		final String LINK = FilesAddHandler.PATH + "?" + FilesAddHandler.DIR + "=";
		try {

			Div<HtmlView<PathTree>> wrapper = null;
			if (fileTree.getDepth() == 0) {
				wrapper = view.div().attrClass(CSS.FV_ROOT);
				finalView.addPartial(TemplateFileListItem.view, fileTree);
			}

			if (fileTree.getFile().isDirectory()) {
				view.defineRoot().ul().of(ul -> {
					for (PathTree child: fileTree.getChildren()) {
						var li = ul.li();
							finalView.addPartial(TemplateFileListItem.view, child);
						if (child.getFile().isDirectory()) {
							finalView.addPartial(TemplateFileList.view, child);
						}
						li.__();
					}
				}).__();
			} else {
				view.defineRoot().li().span().text(fileTree.getFile().getName()).__().__();
			}
			if (wrapper != null) wrapper.__();
		 } catch (Exception e) {
			 view = DynamicHtml.view(TemplateFileList::template);
			 throw e;
		}
	 }
}
