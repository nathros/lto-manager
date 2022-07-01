package lto.manager.common;

public class Main {
	
	 public static void main(String[] args) {
		 if (args.length > 0) {
			 if (args[0] == "web") {
				 lto.manager.web.Main.main(args);
				 return;
			 } else if (args[0] == "gui") {
				 lto.manager.gui.Main.main(args);
				 return;
			 } 
		 }
		 System.out.println("Missing param options are web and gui");
	 }
}
