package lto.manager.web.handlers.http.partial.filelist;

import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.NotNull;

public record FileListOptions(
	boolean showRoot,
	String breadcrumbs,
	@NotNull List<String> selected,
	int maxDepth,
	boolean isVirtual,
	boolean dirTasks
) {

	public static FileListOptions of(
		boolean showRoot,
		String breadcrumbs,
		List<String> selected,
		int maxDepth,
		boolean isVirtual,
		boolean dirTasks)
	{
		if (selected == null) selected = new ArrayList<String>();
		return new FileListOptions(showRoot, breadcrumbs, selected, maxDepth, isVirtual, dirTasks);
	}
}
