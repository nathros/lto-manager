package lto.manager.web;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.logging.Level;

import lto.manager.common.Main;
import lto.manager.common.database.Database;
import lto.manager.common.log.Log;
import lto.manager.common.state.State;
import lto.manager.common.system.SystemState;

public class MainWeb {
	public static int portHTTP = 9000;
	public static int portHTTPS = -1;
	public static String keyStoreFile = null;
	public static String storePass = null;
	public static String keyPass = null;
	public static String dbPath = "config/base.db";

	public static final String HTTPPORT = "httpport";
	public static final String HTTPSPORT = "httpsport";
	public static final String KEYSTOREPATH = "keystorepath";
	public static final String KEYSTORECONFIGPATH = "keystoreconfigpath";
	public static final String DBPATH = "dbpath";

	public static final String STOREPASS = "storepass";
	public static final String KEYPASS = "keypass";

	public static final BlockingQueue<ExitReason> exitWait = new LinkedBlockingDeque<>();

	public static void main(String[] args) throws InterruptedException {
		if (processArgs(args)) {
			Database.openDatabase(dbPath);
			State.startBackgroundCleanup();
			SystemState.checkSystem();

			SimpleHttpServer httpServer = new SimpleHttpServer();
			SimpleHttpsServer httpsServer = null;
			httpServer.start(portHTTP, portHTTPS > 0);

			if (portHTTPS > 0) {
				httpsServer = new SimpleHttpsServer();
				httpsServer.start(portHTTPS, keyStoreFile, storePass.toCharArray(), keyPass.toCharArray());
			}
			SimpleWebSocketServer.main(null);

			ExitReason exit = exitWait.take();

			State.stopBackgroundCleanup();
			try {
				Log.info("Closing WebSocket server");
				SimpleWebSocketServer.stopServer();
			} catch (Exception e) {
				Log.log(Level.SEVERE, "Failure in stopping WebSocket server", e);
			}

			Log.info("Closing HTTP sever");
			httpServer.stop();
			if (httpsServer != null) {
				Log.info("Closing HTTPS server");
				httpsServer.stop();
			}

			try {
				Log.info("Closing database");
				Database.close();
			} catch (Exception e) {
				Log.log(Level.SEVERE, "Failed to close database", e);
			}

			System.exit(exit.getValue());
		}
		System.exit(ExitReason.BAD_PARAM.getValue());
	}

	public enum ExitReason {
		NORMAL(0), BAD_PARAM(1), UPDATE(5);

		private final int value;

		private ExitReason(int value) {
			this.value = value;
		}

		public int getValue() {
			return value;
		}
	}

	private static boolean processArgs(String[] args) {
		int i = 0;
		if (args != null) {
			while (i < args.length) {
				switch (args[i]) {
				case HTTPPORT: {
					i++;
					String portStr = null;
					try {
						portStr = args[i];
					} catch (Exception e) {
						System.out.println(HTTPPORT + " value is missing");
						return false;
					}
					try {
						portHTTP = Integer.valueOf(portStr);
					} catch (Exception e) {
						System.out.println(HTTPPORT + " value of " + portStr + " is not valid");
						return false;
					}
					i++;
					continue;
				}

				case HTTPSPORT: {
					i++;
					String portStr = null;
					try {
						portStr = args[i];
					} catch (Exception e) {
						System.out.println(HTTPSPORT + " value is missing");
						return false;
					}
					try {
						portHTTPS = Integer.valueOf(portStr);
					} catch (Exception e) {
						System.out.println(HTTPSPORT + " value of " + portStr + " is not valid");
						return false;
					}
					i++;
					continue;
				}

				case KEYSTOREPATH: {
					i++;
					String path = null;
					try {
						path = args[i];
					} catch (Exception e) {
						System.out.println(path + " value is missing");
						return false;
					}
					File f = new File(path);
					if (f.exists() && !f.isDirectory()) {
						keyStoreFile = path;
					} else {
						System.out.println(KEYSTOREPATH + " does not exist or have read access");
					}
					i++;
					continue;
				}

				case KEYSTORECONFIGPATH: {
					i++;
					String path = null;
					try {
						path = args[i];
					} catch (Exception e) {
						System.out.println(path + " value is missing");
						return false;
					}
					File f = new File(path);
					if (f.exists() && !f.isDirectory()) {
						keyStoreFile = path;
					} else {
						System.out.println(KEYSTORECONFIGPATH + " does not exist or have read access");
						return false;
					}

					try {
						Path filePath = Path.of(path);
						String content = Files.readString(filePath);
						String[] lines = null;
						if (content.contains("\r\n")) {
							lines = content.split("\r\n");
						} else if (content.contains("\r")) {
							lines = content.split("\r");
						} else if (content.contains("\n")) {
							lines = content.split("\n");
						} else {
							System.out.println(KEYSTORECONFIGPATH + " is not properly formatted");
							return false;
						}
						for (int j = 0; j < lines.length; j++) {
							String[] keyValue = lines[j].split("=");
							if (keyValue.length != 2)
								continue;

							if (keyValue[0].contains(STOREPASS)) {
								storePass = keyValue[1];
							} else if (keyValue[0].contains(KEYPASS)) {
								keyPass = keyValue[1];
							}
						}
						if (storePass == null) {
							System.out.println(KEYSTORECONFIGPATH + " does not contain valid " + STOREPASS + "value");
							return false;
						}
						if (keyPass == null) {
							System.out.println(KEYSTORECONFIGPATH + " does not contain valid " + KEYPASS + "value");
							return false;
						}

					} catch (IOException e) {
						e.printStackTrace();
					}
					i++;
					continue;
				}

				case DBPATH: {
					i++;
					dbPath = args[i];
					i++;
					break;
				}

				case Main.WEB:
				case Main.GUI: { // Ignore
					i++;
					break;
				}

				default: {
					System.out.println("Unknown parameter: " + args[i]);
					return false;
				}
				}
			}
		}

		return true;
	}

}
