package lto.manager.common.ltfs;

import java.io.IOException;

import lto.manager.common.ExternalProcess;

public class BackupToLTFS extends ExternalProcess {
	private static final String[] cmd = {"bash", "list.sh" };

	public boolean start() throws IOException, InterruptedException {
		return start(null, null, cmd);
	}


	@Override
	public void onProcessExit() {
		if (exitCode == 0) {

		}
	}
}
