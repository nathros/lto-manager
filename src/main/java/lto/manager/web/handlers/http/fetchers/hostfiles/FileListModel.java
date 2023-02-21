package lto.manager.web.handlers.http.fetchers.hostfiles;

import lto.manager.common.fileview.PathTreeBase;
import lto.manager.common.fileview.PathTreePhysical;

public class FileListModel {
		public static final String FILE_SELECTED = "f@";
		public static final String FILE_PATH = "f@path";
		public static final String BREADCRUMBS_LAST = "f@bread";
		public static final String MAX_DEPTH = "f@depth";
		public static final String SHOW_ROOT = "f@root";
		public static final int INFINIE_DEPTH = -1;

		private PathTreeBase tree;
		private FileListOptions options;

		public FileListModel(String baseDir, FileListOptions options) {
			this.options = options;
			if (options.isVirtual()) {
				//TODO virtual
			} else {
				this.tree = new PathTreePhysical(baseDir, 0, options.maxDepth());
			}
		}

		public PathTreeBase getTree() { return tree; }
		public FileListOptions getOptions() { return options; }
		public void setTree(PathTreeBase tree) { this.tree = tree; }
		public boolean isSelected() {
			String path = tree.toString();
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