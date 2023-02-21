package lto.manager.common.fileview;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class PathTreePhysical extends PathTreeBase {

	public PathTreePhysical(String filePath, int depth, int maxDepth) {
		super();
		File fileLocal = new File(filePath);
		this.depth = depth;
		this.file = new PathTreeFile(fileLocal);

		if (fileLocal.isDirectory()) {
			children = new ArrayList<PathTreeBase>();
			List<PathTreePhysical> dir = new ArrayList<PathTreePhysical>();
			List<PathTreePhysical> singleFile = new ArrayList<PathTreePhysical>();
			if ((depth + 1) <= maxDepth) {
				for (File f: fileLocal.listFiles()) { // Separate files and directories
					if (f.isDirectory()) {
						dir.add(new PathTreePhysical(f.getAbsolutePath(), depth + 1, maxDepth));
					} else {
						singleFile.add(new PathTreePhysical(f.getAbsolutePath(), depth + 1, maxDepth));
					}
				}
			}
			children.addAll(dir); // Make sure directories are always first
			children.addAll(singleFile);
		}
	}

	public PathTreePhysical(String filePath) {
		this(filePath, 0, 1);
	}
}
