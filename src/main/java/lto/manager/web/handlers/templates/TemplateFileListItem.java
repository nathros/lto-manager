package lto.manager.web.handlers.templates;

import java.util.List;

import org.xmlet.htmlapifaster.EnumTypeInputType;

import htmlflow.DynamicHtml;
import htmlflow.HtmlView;
import lto.manager.common.Util;
import lto.manager.common.fileselector.PathTree;
import lto.manager.web.handlers.AssetHandler;
import lto.manager.web.handlers.files.FilesAddHandler;
import lto.manager.web.resource.Asset;
import lto.manager.web.resource.CSS;

public class TemplateFileListItem {

	public static HtmlView<PathTree> view = DynamicHtml.view(TemplateFileListItem::template);

	private static final String DATA_SIZE = "data-size";
	private static final String DATA_TIME = "data-time";
	private static final String DATA_NAME = "data-name";
	private static final List<String> fileTypeCache = AssetHandler.getCachedFileListInDir(Asset.IMG_TYPES);

	 static void template(DynamicHtml<PathTree> view, PathTree fileTree) {
		final String LINK = FilesAddHandler.PATH + "?" + FilesAddHandler.DIR + "=";
		final String ABS_PATH = fileTree.getFile().getAbsolutePath();
		try {
			if (fileTree.getFile().isDirectory()) {
				view.defineRoot()
					.span()
						.addAttr(DATA_SIZE, "0")
						.addAttr(DATA_TIME, "0")
						.addAttr(DATA_NAME, "")
						.input()
							.attrType(EnumTypeInputType.CHECKBOX)
							.attrOnclick("selectDir(this)")
							.attrName(FilesAddHandler.FILE_SELECTED)
							.attrValue(ABS_PATH)
						.__()
						.a().attrClass(CSS.BUTTON_SMALL + CSS.BACKGROUND_GRAY).attrOnclick("hideFileTree(this)").text("+").__()
						.img().attrClass(CSS.FV_FILE_ICON).attrSrc(getFileTypeIcon(fileTree)).__()
						.a()
							.attrHref(LINK + Util.encodeUrl(ABS_PATH)).text(fileTree.getFile().getName())
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
			} else {
				view.defineRoot()
					.span()
						.addAttr(DATA_SIZE, String.valueOf(fileTree.getFileSizeBytes()))
						.addAttr(DATA_TIME, String.valueOf(fileTree.getModifiedDateTimeLong()))
						.addAttr(DATA_NAME, fileTree.getFile().getName())
						.input()
							.attrType(EnumTypeInputType.CHECKBOX)
							.attrOnclick("selectFile(this)")
							.attrName(FilesAddHandler.FILE_SELECTED)
							.attrValue(ABS_PATH)
						.__()
						.img().attrClass(CSS.FV_FILE_ICON).attrSrc(getFileTypeIcon(fileTree)).__()
						.a()
							.attrHref(LINK + Util.encodeUrl(ABS_PATH)).text(fileTree.getFile().getName())
						.__()
						.b().attrClass(CSS.FV_FILE_DETAILS_CONTAINER)
							.em().attrClass(CSS.FV_FILE_DETAILS).text(fileTree.getFileSizeHR()).__()
							.em().attrClass(CSS.FV_FILE_DETAILS).text(fileTree.getModifiedDateTimeStr()).__()
						.__()
					.__();
			}
		 } catch (Exception e) {
			 view = DynamicHtml.view(TemplateFileListItem::template);
			 throw e;
		}
	 }

	 static private String getFileTypeIcon(PathTree file) { // FIXME https://www.xfce-look.org/p/1498619
		 if (file.getFile().isDirectory()) {
			 return Asset.IMG_TYPES + "folder.svg";
		 } else {
			 final String filename = file.getFile().getName();
			 int index = filename.lastIndexOf(".");
			 if (index >= 0) {
				 final String ext = filename.substring(index + 1) + ".svg";
				 if (fileTypeCache.contains(ext)) {
					 final String asset = Asset.IMG_TYPES + ext;
					 return asset;
				 }
			 }
			 return Asset.IMG_TYPES + "unknown.svg";
		 }
	 }
}
