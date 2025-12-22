package lto.manager.web.handlers.websockets;

import java.nio.ByteBuffer;

import org.java_websocket.WebSocket;

import lto.manager.common.ExternalProcess;
import lto.manager.web.handlers.http.templates.models.QueryModel;

public class ServerTimeHandler extends BaseWebsocketHandler {
	public static final String PATH = "/time/";

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
		} catch (InterruptedException e) {
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

	@Override
	public void onNewConnection(WebSocket conn, QueryModel queryModel) {
		// TODO Auto-generated method stub

	}

}
