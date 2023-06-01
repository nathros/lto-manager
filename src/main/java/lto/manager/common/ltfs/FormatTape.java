package lto.manager.common.ltfs;

import java.io.IOException;
import java.util.concurrent.Semaphore;

import lto.manager.common.ExternalProcess;

public class FormatTape extends ExternalProcess {
	private static final String[] cmd = {"bash", "list.sh" };

	public boolean start() throws IOException, InterruptedException {
		return start(null, null, cmd);
	}

	public Semaphore startBlocking(String barcode) throws InterruptedException, IOException {
		Semaphore semaphore = new Semaphore(1, true);
		start(semaphore, barcode, cmd);
		return semaphore;
	}

	@Override
	public void onProcessExit() {
		if (exitCode == 0) {

		}
	}
}
