package lto.manager.web.handlers.http.fetchers.hostfiles;

import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.NotNull;

public record FileListOptions(
	boolean showRoot,
	String breadcrumbs,
	@NotNull List<String> selected,
	int maxDepth,
	boolean isVirtual
) {

	public static FileListOptions of(
		boolean showRoot,
		String breadcrumbs,
		List<String> selected,
		int maxDepth,
		boolean isVirtual)
	{
		if (selected == null) selected = new ArrayList<String>();
		return new FileListOptions(showRoot, breadcrumbs, selected, maxDepth, isVirtual);
	}
}
