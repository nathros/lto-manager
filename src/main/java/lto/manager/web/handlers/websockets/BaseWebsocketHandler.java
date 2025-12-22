package lto.manager.web.handlers.websockets;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.java_websocket.WebSocket;

import lto.manager.web.handlers.http.templates.models.QueryModel;

public abstract class BaseWebsocketHandler {
	protected List<WebSocket> conn = new ArrayList<WebSocket>();
	protected HashMap<Integer, QueryModel> queryMap = new HashMap<Integer, QueryModel>();
	protected static final int SERVER_ERROR = 1011;

	public void addNewConnection(WebSocket ws, QueryModel queryModel) {
		conn.add(ws);
		this.queryMap.put(ws.hashCode(), queryModel);
		onNewConnection(ws, queryModel);
	}

	public boolean removeConnection(WebSocket ws) {
		queryMap.remove(ws.hashCode());
		return conn.remove(ws); // TODO keep old for WebsocketListConnectionHandler
	}

	public final List<WebSocket> getConnections() {
		return conn;
	}

	public abstract void onNewMessage(final WebSocket conn, final String message);
	public abstract void onNewMessage(final WebSocket conn, final ByteBuffer message);
	public abstract void onNewConnection(final WebSocket conn, QueryModel queryModel);
	public abstract boolean start();
}
