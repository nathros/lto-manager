package lto.manager.web.handlers.http.fetchers.hostfiles;

import java.util.ArrayList;
import java.util.List;

import lto.manager.common.fileselector.PathTree;

public class HostFileListModel {
		public static final String FILE_SELECTED = "f@";
		public static final String FILE_PATH = "f@path";
		public static final String BREADCRUMBS_LAST = "f@bread";
		public static final String MAX_DEPTH = "f@depth";
		public static final String SHOW_ROOT = "f@root";
		public static final int INFINIE_DEPTH = -1;

		private PathTree tree;
		private boolean showRoot;
		private String breadcrumbs;
		private List<String> selected;
		private int maxDepth;

		public HostFileListModel(String baseDir, boolean showRoot, String breadcrumbs, List<String> selected, int maxDepth) {
			this.tree = new PathTree(baseDir, 0, maxDepth);
			this.showRoot = showRoot;
			this.breadcrumbs = breadcrumbs;
			if (selected == null) this.selected = new ArrayList<String>();
			else this.selected = selected;
			this.maxDepth = maxDepth;
		}

		public HostFileListModel(String baseDir, boolean showBreadcrumbs, String breadcrumbs, List<String> selected) {
			this(baseDir, showBreadcrumbs, breadcrumbs, selected, 1);
		}

		public PathTree getTree() { return tree; }
		public void setTree(PathTree tree) { this.tree = tree; }
		public boolean getShowRoot() { return showRoot; }
		public String getBreadcrumbs() { return breadcrumbs; }
		public List<String> getSelected() { return selected; }
		public int getMaxDepth() { return maxDepth; }
		public boolean isSelected() {
			String path = tree.toString();
			boolean result = selected.contains(path);
			return result;
		}
		public boolean showItem() {
			var depth = tree.getDepth();
			return depth <= maxDepth;
		}
		public boolean showItem(PathTree item) {
			var depth = item.getDepth();
			return depth <= maxDepth;
		}
	}