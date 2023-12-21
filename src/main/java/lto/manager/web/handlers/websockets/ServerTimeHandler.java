package lto.manager.web.handlers.websockets;

import java.io.IOException;
import java.nio.ByteBuffer;

import org.java_websocket.WebSocket;

import lto.manager.common.ExternalProcess;

public class ServerTimeHandler extends BaseWebsocketHandler {
	public static final String PATH = "/time";

	private ExternalProcess ep;

	@Override
	public boolean start() {
		if (ep != null)
			return false;
		ep = new ExternalProcess() {

			@Override
			public void onProcessExit() {

			}
		};

		try {
			ep.start(null, "repeat.sh");
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public void onNewMessage(final WebSocket conn, String message) {

	}

	@Override
	public void onNewMessage(final WebSocket conn, ByteBuffer message) {

	}

}
