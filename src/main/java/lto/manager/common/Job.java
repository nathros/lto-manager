package lto.manager.common;

import java.io.IOException;

public class Job extends ExternalProcess {

	private static final String[] cmd = {"bash", "copy.sh" };

	public boolean start() throws IOException {
		return start(cmd);
	}

	public RsyncUpdateInfo getLatestInfo() {
		String parse = "NONE NONE NONE NONE";

		if ((process != null)) {
			int index = stdout.size();
			if (index > 0)
			parse = stdout.get(stdout.size() - 1);
		}

		RsyncUpdateInfo info = null;
		try {
			info = new RsyncUpdateInfo(parse);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return info;
	}

	@Override
	public String getLatestError() {
		int index = stderr.size();
		if (index > 0) return stderr.get(index - 1);
		else return "NONE";
	}


	public class RsyncUpdateInfo {
		private String totalCopied = null;
		private String percentCompleted = null;
		private String transferSpeed = null;
		private String estimatedTimeRemaining = null;

		public RsyncUpdateInfo(String stdout) throws Exception {
			if ((stdout == null) || (stdout.length() < 8)) throw new Exception("RsyncUpdateInfo invalid input too small: " + stdout);

			String[] tmp = new String[4];
			stdout = stdout.trim();
			String[] split = stdout.split(" ");

			int count = 0;
			for (int i = 0; i < split.length; i++) {
				if (!split[i].equals("")) {
					tmp[count] = split[i];
					count++;
					if (count == 4) break;
				}
			}

			if (count != 4) throw new Exception("RsyncUpdateInfo invalid input not all params found: " + stdout);
			totalCopied = tmp[0];
			percentCompleted = tmp[1];
			transferSpeed = tmp[2];
			estimatedTimeRemaining = tmp[3];
		}

		public String getTotalCopied() { return totalCopied; }
		public String getPercentCompleted() { return percentCompleted; }
		public String getTransferSpeed() { return transferSpeed; }
		public String getEstimatedTimeRemaining() { return estimatedTimeRemaining; }
	}


	@Override
	public void onProcessExit() {}
}
