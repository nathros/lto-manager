package lto.manager.web.handlers.websockets.admin;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Level;

import org.java_websocket.WebSocket;

import lto.manager.common.log.Log;
import lto.manager.web.handlers.websockets.BaseWebsocketHandler;
import lto.manager.web.resource.Asset;

public class LoggingWebsocketHandler extends BaseWebsocketHandler {
	public static final String PATH = Asset.PATH_WEBSOCKET_BASE + "logging";

	@Override
	public boolean start() {
		return false;
	}

	@Override
	public void onNewMessage(final WebSocket conn, String message) {
		if ("all".equals(message)) { // Request from client to get all messages
			final String logPath = Log.getLogFilePath() + ".0";
			File file = new File(logPath);
			if (!file.exists()) {
				conn.send(Log.generateLogMessageAsString(Level.SEVERE,
						"Cannot find log file: " + logPath));
				return;
			} else if (!file.canRead()) {
				conn.send(Log.generateLogMessageAsString(Level.SEVERE,
						"Cannot read log file: " + logPath));
				return;
			}
			try {
				String content = Files.readString(Paths.get(logPath));
				conn.send(content);
			} catch (IOException e) {
				conn.send(Log.generateLogMessageAsString(Level.SEVERE,
						"Failure with log file: " + logPath + " error: " + e.getMessage()));
			}
		}
	}

	@Override
	public void onNewMessage(final WebSocket conn, ByteBuffer message) {
		Log.warning("ByteBuffer message not expected for LoggingHandler - message discarded");
	}

	public void publishNewMessage(final String message) {
		for (final var client : conn) {
			if (client.isOpen()) {
				client.send(message);
			}
		}
	}

	public boolean hasClient() {
		return conn.size() > 0;
	}
}
