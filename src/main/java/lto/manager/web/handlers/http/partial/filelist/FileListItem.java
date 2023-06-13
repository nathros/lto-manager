package lto.manager.web.handlers.http.partial.filelist;

import java.util.HashSet;

import org.xmlet.htmlapifaster.Div;
import org.xmlet.htmlapifaster.EnumTypeInputType;

import lto.manager.common.fileview.PathTreeBase;
import lto.manager.web.handlers.http.pages.AssetHandler;
import lto.manager.web.resource.Asset;
import lto.manager.web.resource.CSS;
import lto.manager.web.resource.HTML;
import lto.manager.web.resource.JS;

public class FileListItem {
	private static final String DATA_SIZE = "data-size";
	private static final String DATA_TIME = "data-time";
	private static final String DATA_NAME = "data-name";
	private static final HashSet<String> fileTypeCache = AssetHandler.getCachedFileListInDir(Asset.IMG_TYPES);

	 static void content(Div<?> view, FileListModel fileTree) {
		//final String LINK = FilesAddHandler.PATH + "?" + FilesAddHandler.DIR + "=";
		final String ABS_PATH = fileTree.getTree().getAbsolutePath() + (fileTree.getOptions().isVirtual() ? "/" : "");
		if (fileTree.getTree().isDirectory()) {
			view
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
								.attrOnchange(JS.fnFileCheckBoxChange())
								.attrName(FileListModel.FILE_SELECTED)
								.attrValue(ABS_PATH)
								.of(in -> HTML.check(in, fileTree.isSelected()))
							.__()
							.a()
								.attrClass(CSS.BUTTON_SMALL + CSS.BACKGROUND_GRAY)
								.attrOnclick(JS.fnFileViewExpandDir(ABS_PATH, fileTree.getOptions().isVirtual()))
								.text("+")
							.__();
						}
					})
					.img().attrClass(CSS.FV_FILE_ICON).attrSrc(getFileTypeIcon(fileTree.getTree())).attrAlt("").__()
					.a()
						.attrOnclick(JS.fnFileViewListChangeDir(ABS_PATH, fileTree.getOptions().isVirtual()))
						.text(fileTree.getTree().getName())
					.__()
					.of(con -> {
						boolean show = fileTree.getOptions().isVirtual();
						if (show) {
							show = fileTree.getTree().getDepth() == 0;
						} else {
							show = true;
						}
						if (show) {
							con.span()
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
							.__();
						}
					})
				.__();
		} else {
			view
				.span()
					.addAttr(DATA_SIZE, String.valueOf(fileTree.getTree().getFileSizeBytes()))
					.addAttr(DATA_TIME, String.valueOf(fileTree.getTree().getModifiedDateTimeLong()))
					.addAttr(DATA_NAME, fileTree.getTree().getName())
					.of(s -> {
						if (!fileTree.getOptions().isVirtual()) {
							s.input()
								.attrType(EnumTypeInputType.CHECKBOX)
								.attrOnclick("selectFile(this)")
								.attrOnchange(JS.fnFileCheckBoxChange())
								.attrName(FileListModel.FILE_SELECTED)
								.attrValue(ABS_PATH)
								.of(in -> {if (fileTree.isSelected()) in.of(i -> HTML.check(i, true));})
							.__();
						}
					})
					.img().attrClass(CSS.FV_FILE_ICON).attrSrc(getFileTypeIcon(fileTree.getTree())).attrAlt("").__()
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
	 }

	 static private String getFileTypeIcon(PathTreeBase file) { // TODO https://www.xfce-look.org/p/1498619
		 final String customIcon = file.getCustomIcon();
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
