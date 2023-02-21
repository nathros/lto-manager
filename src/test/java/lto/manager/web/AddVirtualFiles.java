package lto.manager.web;

public class AddVirtualFiles {

	public static void main(String[] args) throws InterruptedException {
		Runnable runnable = () -> { lto.manager.web.MainWeb.main(args); };
		Thread thread = new Thread(runnable);
		thread.start();
		Thread.sleep(1000);
		//Database.addFilesToTape(0, null, null)
	}

}
