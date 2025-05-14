package lto.manager.common.system;

import java.util.function.Consumer;

public class SystemUpdate {
	private String newVersion;
	private Consumer<String> onProgress;

	public SystemUpdate(String newVersion, Consumer<String> onProgress) {
		this.newVersion = newVersion;
		this.onProgress = onProgress;
	}

	public void start() {
		onProgress.accept("Starting update for " + newVersion);
	}
}
