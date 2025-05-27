package lto.manager.common.ltfs;

import java.io.IOException;
import java.util.concurrent.Semaphore;

import lto.manager.common.ExternalProcess;
import lto.manager.common.log.Log;

public class CheckLTFSInstalled extends ExternalProcess {
	private static final String[] cmd = { "ltfs", "--version" };
	private String version = null;
	private String spec = null;

	public boolean start() throws IOException, InterruptedException {
		return start(null, null, cmd);
	}

	public Semaphore startBlocking(String uuid) throws InterruptedException, IOException {
		Semaphore semaphore = new Semaphore(1, true);
		start(semaphore, uuid, cmd);
		return semaphore;
	}

	@Override
	public void onProcessExit() {
		if (stderr.size() != 0) {
			final String errStr = String.join("", stderr);
			Log.severe("Failed to get LTFS version with output to stderr: " + errStr);
			return;
		}

		final String searchVersion = "LTFS version";
		final String searchSpec = "Specification version";
		for (String line : stdout) {
			int firstIndex = line.indexOf(searchVersion);
			if (firstIndex != -1) {
				firstIndex += searchVersion.length();
				final int endIndex = line.indexOf(' ', firstIndex + 1);
				if (endIndex != -1) {
					version = line.substring(firstIndex + 1, endIndex);
				}
				continue;
			}

			firstIndex = line.indexOf(searchSpec);
			if (firstIndex != -1) {
				final int endIndex = line.lastIndexOf(' ');
				spec = line.substring(endIndex + 1);
			}

		}
	}

	public String getVersion() {
		return version;
	}

	public String getSpec() {
		return spec;
	}

}
