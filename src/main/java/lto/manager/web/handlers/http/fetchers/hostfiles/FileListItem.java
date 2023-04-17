package lto.manager.web.handlers.http.fetchers.hostfiles;

import java.util.List;

import org.xmlet.htmlapifaster.EnumTypeInputType;

import htmlflow.DynamicHtml;
import htmlflow.HtmlView;
import lto.manager.common.fileview.PathTreeBase;
import lto.manager.web.handlers.http.AssetHandler;
import lto.manager.web.resource.Asset;
import lto.manager.web.resource.CSS;
import lto.manager.web.resource.JS;

public class FileListItem {

	public static HtmlView<FileListModel> view = DynamicHtml.view(FileListItem::template);

	private static final String DATA_SIZE = "data-size";
	private static final String DATA_TIME = "data-time";
	private static final String DATA_NAME = "data-name";
	private static final List<String> fileTypeCache = AssetHandler.getCachedFileListInDir(Asset.IMG_TYPES);

	 static void template(DynamicHtml<FileListModel> view, FileListModel fileTree) {
		//final String LINK = FilesAddHandler.PATH + "?" + FilesAddHandler.DIR + "=";
		final String ABS_PATH = fileTree.getTree().getAbsolutePath() + (fileTree.getOptions().isVirtual() ? "/" : "");
		try {
			if (fileTree.getTree().isDirectory()) {
				view.defineRoot()
					.span()
						.addAttr(DATA_SIZE, "0")
						.addAttr(DATA_TIME, "0")
						.addAttr(DATA_NAME, ABS_PATH)
						.of(s -> {
							s.addAttr("oncontextmenu", JS.fnFileContextMenu(fileTree.getOptions().isVirtual()));
							if (!fileTree.getOptions().isVirtual()) {
								s.input()
									.attrType(EnumTypeInputType.CHECKBOX)
									.attrOnclick("selectDir(this," + fileTree.getOptions().isVirtual() + ")")
									.attrName(FileListModel.FILE_SELECTED)
									.attrValue(ABS_PATH)
									.of(in -> {if (fileTree.isSelected()) in.attrChecked(true);})
								.__()
								.a()
									.attrClass(CSS.BUTTON_SMALL + CSS.BACKGROUND_GRAY)
									.attrOnclick(JS.fnFileViewExpandDir(ABS_PATH, fileTree.getOptions().isVirtual()))
									.text("+")
								.__();
							}
						})
						.img().attrClass(CSS.FV_FILE_ICON).attrSrc(getFileTypeIcon(fileTree.getTree())).__()
						.a()
							.attrOnclick(JS.fnFileViewListChangeDir(ABS_PATH, fileTree.getOptions().isVirtual()))
							.text(fileTree.getTree().getName())
						.__()
						.span()
							.attrClass(CSS.FV_ICON_SIZE).attrOnclick(JS.fnFileViewSort(DATA_SIZE))
							.em().text("Sort by file size").__()
						.__()
						.span()
							.attrClass(CSS.FV_ICON_MODIFIED).attrOnclick(JS.fnFileViewSort(DATA_TIME))
							.em().text("Sort by modified").__()
						.__()
						.span()
							.attrClass(CSS.FV_ICON_NAME).attrOnclick(JS.fnFileViewSort(DATA_NAME))
							.em().text("Sort by name").__()
						.__()
					.__();
			} else {
				view.defineRoot()
					.span()
						.addAttr(DATA_SIZE, String.valueOf(fileTree.getTree().getFileSizeBytes()))
						.addAttr(DATA_TIME, String.valueOf(fileTree.getTree().getModifiedDateTimeLong()))
						.addAttr(DATA_NAME, fileTree.getTree().getName())
						.of(s -> {
							if (!fileTree.getOptions().isVirtual()) {
								s.input()
									.attrType(EnumTypeInputType.CHECKBOX)
									.attrOnclick("selectFile(this)")
									.attrName(FileListModel.FILE_SELECTED)
									.attrValue(ABS_PATH)
									.of(in -> {if (fileTree.isSelected()) in.attrChecked(true);})
								.__();
							}
						})
						.img().attrClass(CSS.FV_FILE_ICON).attrSrc(getFileTypeIcon(fileTree.getTree())).__()
						//.a()
							//.attrHref(LINK + Util.encodeUrl(ABS_PATH))
							.text(fileTree.getTree().getName())
						//.__()
						.b().attrClass(CSS.FV_FILE_DETAILS_CONTAINER)
							.em().attrClass(CSS.FV_FILE_DETAILS).text(fileTree.getTree().getFileSizeHR()).__()
							.em().attrClass(CSS.FV_FILE_DETAILS).text(fileTree.getTree().getModifiedDateTimeStr()).__()
						.__()
					.__();
			}
		 } catch (Exception e) {
			 view = DynamicHtml.view(FileListItem::template);
			 throw e;
		}
	 }

	 static private String getFileTypeIcon(PathTreeBase file) { // FIXME https://www.xfce-look.org/p/1498619
		 String customIcon = file.getCustomIcon();
		 if (customIcon != null) {
			 return Asset.IMG_TYPES + customIcon + ".svg";
		 }
		 if (file.isDirectory()) {
			 return Asset.IMG_TYPES + "folder.svg";
		 } else {
			 final String filename = file.getName();
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
