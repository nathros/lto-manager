package lto.manager.web.handlers.http.fetchers.hostfiles;

import java.io.IOException;
import java.sql.SQLException;

import lto.manager.common.database.Database;
import lto.manager.common.database.tables.TableFile;
import lto.manager.common.fileview.PathTreeBase;
import lto.manager.common.fileview.PathTreePhysical;
import lto.manager.common.fileview.PathTreeVirtual;

public class FileListModel {
		public static final String FILE_SELECTED = "f@";
		public static final String FILE_PATH = "f@path";
		public static final String BREADCRUMBS_LAST = "f@bread";
		public static final String MAX_DEPTH = "f@depth";
		public static final String SHOW_ROOT = "f@root";
		public static final String IS_VIRTUAL = "f@isvirtual";
		public static final int INFINIE_DEPTH = -1;

		private PathTreeBase tree;
		private FileListOptions options;

		public FileListModel(String baseDir, FileListOptions options) {
			this.options = options;
			if (options.isVirtual()) {
				try {
					var files = TableFile.getFilesInDir(Database.connection, baseDir);
					this.tree = new PathTreeVirtual(baseDir, 0, options.maxDepth() + 1 - 1, files);
				} catch (SQLException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else {
				this.tree = new PathTreePhysical(baseDir, 0, options.maxDepth());
			}
		}

		public String getIDPostFix() { return getOptions().isVirtual() ? "-v" : "-p"; }
		public PathTreeBase getTree() { return tree; }
		public FileListOptions getOptions() { return options; }
		public void setTree(PathTreeBase tree) { this.tree = tree; }
		public boolean isSelected() {
			String path = tree.getAbsolutePath();
			boolean result = options.selected().contains(path);
			return result;
		}
		public boolean showItem() {
			var depth = tree.getDepth();
			return depth <= options.maxDepth();
		}
		public boolean showItem(PathTreeBase item) {
			var depth = item.getDepth();
			return depth <= options.maxDepth();
		}
	}