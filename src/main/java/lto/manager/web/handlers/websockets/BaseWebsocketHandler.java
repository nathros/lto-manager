package lto.manager.web.handlers.websockets;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import org.java_websocket.WebSocket;

public abstract class BaseWebsocketHandler {
	protected List<WebSocket> conn = new ArrayList<WebSocket>();
	protected static final int SERVER_ERROR = 1011;

	public void addNewConnection(WebSocket ws) {
		conn.add(ws);
		onNewConnection(ws);
	}

	public boolean removeConnection(WebSocket ws) {
		return conn.remove(ws); // TODO keep old for WebsocketListConnectionHandler
	}

	public final List<WebSocket> getConnections() {
		return conn;
	}

	public abstract void onNewMessage(final WebSocket conn, final String message);
	public abstract void onNewMessage(final WebSocket conn, final ByteBuffer message);
	public abstract void onNewConnection(final WebSocket conn);
	public abstract boolean start();
}
