package lto.manager.web.handlers.http.fetchers.hostfiles;

import java.util.List;

import org.xmlet.htmlapifaster.EnumTypeInputType;

import htmlflow.DynamicHtml;
import htmlflow.HtmlView;
import lto.manager.common.fileselector.PathTree;
import lto.manager.web.handlers.http.AssetHandler;
import lto.manager.web.resource.Asset;
import lto.manager.web.resource.CSS;
import lto.manager.web.resource.JS;

public class HostFileListItem {

	public static HtmlView<HostFileListModel> view = DynamicHtml.view(HostFileListItem::template);

	private static final String DATA_SIZE = "data-size";
	private static final String DATA_TIME = "data-time";
	private static final String DATA_NAME = "data-name";
	private static final List<String> fileTypeCache = AssetHandler.getCachedFileListInDir(Asset.IMG_TYPES);

	 static void template(DynamicHtml<HostFileListModel> view, HostFileListModel fileTree) {
		//final String LINK = FilesAddHandler.PATH + "?" + FilesAddHandler.DIR + "=";
		final String ABS_PATH = fileTree.getTree().getFile().getAbsolutePath();
		try {
			if (fileTree.getTree().getFile().isDirectory()) {
				view.defineRoot()
					.span()
						.addAttr(DATA_SIZE, "0")
						.addAttr(DATA_TIME, "0")
						.addAttr(DATA_NAME, "")
						.input()
							.attrType(EnumTypeInputType.CHECKBOX)
							.attrOnclick("selectDir(this)")
							.attrName(HostFileListModel.FILE_SELECTED)
							.attrValue(ABS_PATH)
							.of(in -> {if (fileTree.isSelected()) in.attrChecked(true);})
						.__()
						.a()
							.attrClass(CSS.BUTTON_SMALL + CSS.BACKGROUND_GRAY)
							//.attrOnclick("hideFileTree(this)")
							.attrOnclick(JS.fnHostExpandDir(ABS_PATH))
							.text("+")
						.__()
						.img().attrClass(CSS.FV_FILE_ICON).attrSrc(getFileTypeIcon(fileTree.getTree())).__()
						.a()
							.attrOnclick(JS.fnHostFileListChangeDir(ABS_PATH))
							.text(fileTree.getTree().getFile().getName())
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
						.addAttr(DATA_SIZE, String.valueOf(fileTree.getTree().getFileSizeBytes()))
						.addAttr(DATA_TIME, String.valueOf(fileTree.getTree().getModifiedDateTimeLong()))
						.addAttr(DATA_NAME, fileTree.getTree().getFile().getName())
						.input()
							.attrType(EnumTypeInputType.CHECKBOX)
							.attrOnclick("selectFile(this)")
							.attrName(HostFileListModel.FILE_SELECTED)
							.attrValue(ABS_PATH)
							.of(in -> {if (fileTree.isSelected()) in.attrChecked(true);})
						.__()
						.img().attrClass(CSS.FV_FILE_ICON).attrSrc(getFileTypeIcon(fileTree.getTree())).__()
						//.a()
							//.attrHref(LINK + Util.encodeUrl(ABS_PATH))
							.text(fileTree.getTree().getFile().getName())
						//.__()
						.b().attrClass(CSS.FV_FILE_DETAILS_CONTAINER)
							.em().attrClass(CSS.FV_FILE_DETAILS).text(fileTree.getTree().getFileSizeHR()).__()
							.em().attrClass(CSS.FV_FILE_DETAILS).text(fileTree.getTree().getModifiedDateTimeStr()).__()
						.__()
					.__();
			}
		 } catch (Exception e) {
			 view = DynamicHtml.view(HostFileListItem::template);
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
