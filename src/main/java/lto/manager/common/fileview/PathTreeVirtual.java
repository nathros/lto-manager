package lto.manager.common.fileview;

import java.util.ArrayList;
import java.util.List;

import lto.manager.common.database.tables.records.RecordFile;

public class PathTreeVirtual extends PathTreeBase {

	public PathTreeVirtual(String filePath, int depth, int maxDepth, List<RecordFile> files) {
		super(depth);
		//if (maxDepth > 1) { int a = 5 / 0; };
		if (files.size() > 0) {
			var first = files.get(0);
			file = new PathTreeFile(first);
			customIcon = first.getCustomIcon();
			children = new ArrayList<PathTreeBase>();
			for (int i = 1; i < files.size(); i++) {
				var list = new ArrayList<RecordFile>();
				list.add(files.get(i));
				children.add(new PathTreeVirtual(null, depth + 1, maxDepth, list));
			}
		} else {
			file = PathTreeFile.EmptyFile(filePath);
		}
	}

}
