package lto.manager.web.resource;

public class JS {
	public static String fnHostFileListChangeDir(String path) { return "hostChangeDir('" + path + "')"; }
	public static String fnHostExpandDir(String path) { return "expandDir(this,'" + path + "')"; }
}
