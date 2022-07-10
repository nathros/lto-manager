package lto.manager.common;

public class Main {
	public static final String WEB = "web";
	public static final String GUI = "gui";

	public static final boolean DEBUG_MODE = java.lang.management.ManagementFactory.getRuntimeMXBean().getInputArguments().toString().indexOf("-agentlib:jdwp") > 0;

	public static void main(String[] args) {
		if (args.length > 0) {
			if (args[0].equals(WEB)) {
				lto.manager.web.MainWeb.main(args);
				return;
			} else if (args[0].equals(GUI)) {
				lto.manager.gui.MainGUI.main(args);
				return;
			}
		}
		System.out.println("Error: Missing mode param, options are " + WEB + " and " + GUI);
	}
}
