package lto.manager.web.handlers.websockets.admin;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Level;

import org.java_websocket.WebSocket;

import lto.manager.common.log.Log;
import lto.manager.common.log.Log.LogFile;
import lto.manager.web.handlers.http.pages.admin.advanced.LoggingHandler;
import lto.manager.web.handlers.http.templates.models.QueryModel;
import lto.manager.web.handlers.websockets.BaseWebsocketHandler;
import lto.manager.web.resource.Asset;

public class LoggingWebsocketHandler extends BaseWebsocketHandler {
	public static final String PATH = Asset.PATH_WEBSOCKET_BASE + "logging/";

	@Override
	public boolean start() {
		return false;
	}

	@Override
	public void onNewConnection(WebSocket conn, QueryModel queryModel) {
		final String logPath = Log.getLogFilePath(queryModel.getEnumOrdinal(LoggingHandler.QUERY_FILE, LogFile.Main)) + ".0";
		File file = new File(logPath);
		if (!file.exists()) {
			conn.send(Log.generateLogMessageAsString(Level.SEVERE, "Cannot find log file: " + logPath));
			return;
		} else if (!file.canRead()) {
			conn.send(Log.generateLogMessageAsString(Level.SEVERE, "Cannot read log file: " + logPath));
			return;
		}
		try {
			final String content = Files.readString(Paths.get(logPath));
			conn.send(content);
		} catch (IOException e) {
			conn.send(Log.generateLogMessageAsString(Level.SEVERE,
					"Failure with log file: " + logPath + " error: " + e.getMessage()));
		}
	}

	@Override
	public void onNewMessage(final WebSocket conn, String message) {
		Log.warning("No message expected - message discarded");
	}

	@Override
	public void onNewMessage(final WebSocket conn, ByteBuffer message) {
		Log.warning("No message expected - message discarded");
	}

	public void publishNewMessage(final String message, final LogFile file) {
		for (final var client : conn) {
			if (client.isOpen()) {
				var query = queryMap.get(client.hashCode());
				if (query != null) {
					LogFile current = query.getEnumOrdinal(LoggingHandler.QUERY_FILE, LogFile.Main);
					if (file == current) {
						client.send(message);
					}
				}
			}
		}
	}

	public boolean hasClient() {
		return conn.size() > 0;
	}

}
