package lto.manager.web.handlers.websockets;

import java.util.ArrayList;
import java.util.List;

import org.java_websocket.WebSocket;

public abstract class BaseWebsocketHandler {
	protected List<WebSocket> conn = new ArrayList<WebSocket>();

	public void addNewConnection(WebSocket ws) {
		conn.add(ws);
	}

	public void removeConnection(WebSocket ws) {
		conn.remove(ws);
	}

	public abstract void onNewMessage(final String message);

	public abstract boolean start();


}
