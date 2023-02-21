package lto.manager.web.resource;

public class JS {
	public static String fnFileViewListChangeDir(String path) { return "hostChangeDir('" + path + "')"; }
	public static String fnFileViewExpandDir(String path) { return "expandDir(this,'" + path + "')"; }
	public static String fnFileViewSort(String field) { return "sort(this,'" + field + "')"; }

}
