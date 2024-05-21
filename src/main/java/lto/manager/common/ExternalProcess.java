package lto.manager.common;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;

import lto.manager.common.database.Options;
import lto.manager.common.database.tables.records.RecordOptions.OptionsSetting;
import lto.manager.common.log.Log;

public abstract class ExternalProcess {
	public static int EXIT_CODE_OK = 0;

	protected List<String> stdout = new ArrayList<String>();
	protected List<String> stderr = new ArrayList<String>();

	protected Process process;
	protected ExecutorService service;
	protected AtomicBoolean inProgress = new AtomicBoolean();
	protected Integer exitCode;
	protected Semaphore isCompletedSemaphore = null;
	protected String uuid;
	protected String[] cmd;

	private static HashMap<String, ExternalProcess> currentProcesses = new HashMap<String, ExternalProcess>();
	private static HashMap<String, ExternalProcess> retiredProcesses = new HashMap<String, ExternalProcess>();
	private LocalDateTime exitDateTime = null;

	public boolean start(Semaphore completedSemaphore, String uuid, String... commands)
			throws IOException, InterruptedException, IllegalArgumentException {
		if (inProgress.get())
			return false;
		this.uuid = this.getClass().getSimpleName() + "-" + uuid;
		this.isCompletedSemaphore = completedSemaphore;
		cmd = commands;
		if (currentProcesses.get(this.uuid) != null) {
			throw new IllegalArgumentException("uuid already exists: " + uuid);
		}
		currentProcesses.put(this.uuid, this);
		if (isCompletedSemaphore != null)
			isCompletedSemaphore.acquire(1);
		inProgress.set(true);

		stdout.clear();
		stderr.clear();
		exitCode = null;
		ProcessBuilder builder = new ProcessBuilder(commands);
		process = builder.start();
		service = Executors.newFixedThreadPool(2);
		BufferedReader stdOutBuffer = new BufferedReader(new InputStreamReader(process.getInputStream()));
		BufferedReader stdErrBuffer = new BufferedReader(new InputStreamReader(process.getErrorStream()));
		Semaphore semaphore = new Semaphore(2, true);
		semaphore.drainPermits();

		new Thread(() -> {
			try {
				process.waitFor();
				exitCode = process.exitValue();
				inProgress.set(false);
				semaphore.acquire(2);
				stop();
				onProcessExit();
				if (isCompletedSemaphore != null)
					isCompletedSemaphore.release();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}).start();

		service.submit(() -> { // stdout
			StringBuilder str = new StringBuilder();
			while (true) {
				try {
					if (stdOutBuffer.ready()) {
						char tmp = (char) stdOutBuffer.read();
						if (tmp != '\n')
							str.append(tmp);
						else {
							String line = str.toString();
							if (Options.getData(OptionsSetting.LOG_EXTERNAL_PROCESS) == Boolean.TRUE)
								System.out.println("stdout:" + line);
							stdout.add(line);
							str = new StringBuilder();
						}
					} else if (!inProgress.get()) {
						break;
					}
				} catch (IOException e) {
					e.printStackTrace();
					break;
				}
			}
			semaphore.release();
			if (Options.getData(OptionsSetting.LOG_EXTERNAL_PROCESS) == Boolean.TRUE)
				System.out.println("stdout: EXIT");
		});

		service.submit(() -> { // stderr
			StringBuilder str = new StringBuilder();
			while (true) {
				try {
					if (stdErrBuffer.ready()) {
						char tmp = (char) stdErrBuffer.read();
						if (tmp != '\n')
							str.append(tmp);
						else {
							String line = str.toString();
							if (Options.getData(OptionsSetting.LOG_EXTERNAL_PROCESS) == Boolean.TRUE)
								System.out.println("stderr:" + line);
							stdout.add(line);
							str = new StringBuilder();
						}
					} else if (!inProgress.get()) {
						break;
					}
				} catch (IOException e) {
					e.printStackTrace();
					break;
				}
			}
			semaphore.release();
			if (Options.getData(OptionsSetting.LOG_EXTERNAL_PROCESS) == Boolean.TRUE)
				System.out.println("stderr: EXIT");
		});

		return true;
	}

	public static ExternalProcess getFinishedProcess(String uuid) {
		var tmp = retiredProcesses.get(uuid);
		return tmp;
	}

	public static Set<String> getFinishedProcessKeyList() {
		return retiredProcesses.keySet();
	}

	public static ExternalProcess getCurrentProcess(String uuid) {
		var tmp = currentProcesses.get(uuid);
		return tmp;
	}

	public static Set<String> getCurrentProcessKeyList() {
		return currentProcesses.keySet();
	}

	private void moveToRetired() {
		exitDateTime = LocalDateTime.now();
		currentProcesses.remove(uuid);
		retiredProcesses.put(uuid, this);
	}

	public boolean stop() {
		inProgress.set(false);
		if (service != null)
			service.shutdownNow();
		if (service != null)
			process.destroyForcibly();
		moveToRetired();
		return true;
	}

	public boolean operationInProgress() {
		if (process == null)
			return false;
		return process.isAlive();
	}

	public boolean completed() {
		return exitCode != null;
	}

	public List<String> getStdout() {
		return stdout;
	}

	public List<String> getStderr() {
		return stderr;
	}

	public String getArgsAsString() {
		return String.join(" ", cmd);
	}

	public Integer getExitCode() {
		return exitCode;
	}

	public final LocalDateTime getExitDateTime() {
		return exitDateTime;
	}

	public abstract void onProcessExit();

	public static void removeRetired(final int ageOfProcessMinutes) {
		if (ageOfProcessMinutes <= 0)
			throw new IllegalArgumentException("ageOfProcessMinutes is less than or equal to 0");

		LocalDateTime now = LocalDateTime.now();
		LocalDateTime from = now.minusMinutes(ageOfProcessMinutes);

		var iterator = retiredProcesses.entrySet().iterator();
		while (iterator.hasNext()) {
			var retired = iterator.next();
			final ExternalProcess p = retired.getValue();
			final LocalDateTime finished = p.getExitDateTime();
			if (finished == null) {
				Log.severe("Retired processes " + retired.getClass().getSimpleName() + ":" + retired.getKey()
						+ " has null exit date time # removed");
				iterator.remove();
			} else {
				if (from.isAfter(finished)) {
					Log.finer("Removed old retired processes " + retired.getClass().getSimpleName() + ":"
							+ retired.getKey() + " has expired");
					iterator.remove();
				}
			}
		}
	}

}
