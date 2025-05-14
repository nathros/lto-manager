package lto.manager.web.handlers.websockets.admin;

import java.nio.ByteBuffer;

import org.java_websocket.WebSocket;

import lto.manager.common.log.Log;
import lto.manager.common.system.SystemUpdate;
import lto.manager.web.handlers.websockets.BaseWebsocketHandler;
import lto.manager.web.resource.Asset;

public class SystemUpdateWebsocketHandler extends BaseWebsocketHandler {
	public static final String PATH = Asset.PATH_WEBSOCKET_BASE + "admin/update/progress/";
	private SystemUpdate systemUpdate = null;

	@Override
	public boolean start() {
		return false;
	}

	@Override
	public void onNewConnection(WebSocket conn) {
	}

	@Override
	public void onNewMessage(final WebSocket conn, String message) {
		if (message.equals("status")) {
			if (systemUpdate == null) {
				conn.send("::status:::none");
			 } else {

			 }
		}
	}

	@Override
	public void onNewMessage(final WebSocket conn, ByteBuffer message) {
		Log.warning("No ByteBuffer message expected - message discarded");
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
