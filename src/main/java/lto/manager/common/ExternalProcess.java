package lto.manager.common;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class ExternalProcess {
	protected List<String> stdout = new ArrayList<String>();
	protected List<String> stderr = new ArrayList<String>();

	protected Process process;
	protected ExecutorService service;
	protected AtomicBoolean inProgress = new AtomicBoolean();
	protected Integer exitCode;

	public boolean start(String... commands) throws IOException {
		if (inProgress.get()) return false;
		inProgress.set(true);

		exitCode = null;
		ProcessBuilder builder = new ProcessBuilder(commands);
		process = builder.start();
		service = Executors.newFixedThreadPool(2);

		new Thread(() -> {
			try {
				process.waitFor();
				exitCode = process.exitValue();
				stop();
				onProcessExit();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}).start();

		service.submit(() -> { // stdout
			BufferedReader lineReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
	        while (inProgress.get() && process.isAlive()) {
	        	String tmp = null;
				try {
					tmp = lineReader.readLine();
				} catch (IOException e) {
					e.printStackTrace();
					break;
				}
				if (tmp != null) {
					System.out.println("stdout:" + tmp);
					stdout.add(tmp);
					if (stdout.size() > 16) stdout.remove(0);
				}
	        }
	    });

		service.submit(() -> { // stderr
			BufferedReader lineReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
	        while (inProgress.get() && process.isAlive()) {
	        	String tmp = null;
				try {
					tmp = lineReader.readLine();
				} catch (IOException e) {
					e.printStackTrace();
					break;
				}
				if (tmp != null) {
					System.out.println("stderr:" + tmp);
					stderr.add(tmp);
					if (stdout.size() > 32) stdout.remove(0);
				}
	        }
	    });

		return true;
	}

	public boolean stop() {
		inProgress.set(false);
		if (service != null) service.shutdownNow();
		if (service != null) process.destroyForcibly();
		return true;
	}

	public boolean operationInProgress() {
		if (process == null) return false;
		return process.isAlive();
	}

	public boolean completed() {
		return exitCode != null;
	}

	public String getLatestError() {
		int index = stderr.size();
		if (index > 0) return stderr.get(index - 1);
		else return "NONE";
	}

	public Integer getExitCode() { return exitCode; }

	public abstract void onProcessExit();

}
