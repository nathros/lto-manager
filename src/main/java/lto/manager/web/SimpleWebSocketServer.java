package lto.manager.web;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import lto.manager.common.database.Options;
import lto.manager.common.database.tables.records.RecordOptions.OptionsSetting;
import lto.manager.common.log.Log;
import lto.manager.common.state.State;
import lto.manager.web.handlers.Handlers;
import lto.manager.web.handlers.http.BaseHTTPHandler;

public class SimpleWebSocketServer extends WebSocketServer {
	private final static int EVENT_CODE_PATH_NOT_FOUND = 3001;
	private static WebSocketServer server = null;

	public SimpleWebSocketServer(InetSocketAddress address) {
		super(address);
	}

	@Override
	public void onOpen(WebSocket conn, ClientHandshake handshake) {
		// conn.send("Welcome to the server!"); //This method sends a message to the new
		// client
		// broadcast( "new connection: " + handshake.getResourceDescriptor() ); //This
		// method sends a message to all clients connected
		// System.out.println("new connection to " + conn.getRemoteSocketAddress());
		if (!validateSession(handshake)) {
			Log.info("User not logged in, Websocket connection rejected");
			return;
		}
		String path = handshake.getResourceDescriptor();
		String query = "";
		int index = path.indexOf('?');
		if (index > 0) {
			query = path.substring(index + 1, path.length());
			path = path.substring(0, index);
		}
		System.out.println(query);
		var handler = Handlers.websocketHandlers.get(path);
		if (handler != null) {
			handler.addNewConnection(conn);
		} else {
			conn.close(EVENT_CODE_PATH_NOT_FOUND, "path: " + path + " not found");
			Log.warning("Refused incoming websocket connection from unknown path: " + path);
		}
	}

	@Override
	public void onClose(WebSocket conn, int code, String reason, boolean remote) {
		var handler = Handlers.websocketHandlers.get(conn.getResourceDescriptor());
		final String detailedInfo = "Websocket closed " + conn.getRemoteSocketAddress() + ", exit code: " + code
				+ ", reason: " + reason;
		if (handler != null) {
			if (handler.removeConnection(conn) == false) {
				Log.warning("Websocket closed from unknown client: " + conn.getResourceDescriptor() + " "
						+ detailedInfo);
			}
		} else {
			Log.warning("Websocket closed from unknown path: " + conn.getResourceDescriptor() + " " + detailedInfo);
		}
		if (code > 1001) {
			Log.fine(detailedInfo);
		}
	}

	@Override
	public void onMessage(WebSocket conn, String message) {
		var handler = Handlers.websocketHandlers.get(conn.getResourceDescriptor());
		if (handler != null) {
			handler.onNewMessage(conn, message);
		} else {
			Log.warning("Websocket message from unknown path: " + conn.getResourceDescriptor());
		}
	}

	@Override
	public void onMessage(WebSocket conn, ByteBuffer message) {
		var handler = Handlers.websocketHandlers.get(conn.getResourceDescriptor());
		if (handler != null) {
			handler.onNewMessage(conn, message);
		} else {
			Log.warning("Websocket message from unknown path: " + conn.getResourceDescriptor());
		}
	}

	@Override
	public void onError(WebSocket conn, Exception ex) {
		if (conn == null) {
			Log.severe("Websocket error on null connection: " + ex);
		} else {
			var handler = Handlers.websocketHandlers.get(conn.getResourceDescriptor());
			handler.removeConnection(conn);
			Log.severe("Websocket error on connection " + conn.getRemoteSocketAddress() + ":" + ex);
		}
	}

	@Override
	public void onStart() {
		Log.info("Websocket server started at ws://" + getAddress().getHostName() + ":" + getAddress().getPort());
	}

	private boolean validateSession(ClientHandshake handshake) {
		if ((boolean) Options.getData(OptionsSetting.ENABLE_LOGIN)) {
			final var cookie = handshake.getFieldValue("Cookie");
			final var cookieMap = BaseHTTPHandler.getCookieKeyPairs(cookie);
			final String session = cookieMap.get(BaseHTTPHandler.COOKIE_SESSION);
			if (!State.isLoginSessionValid(session)) {
				return false;
			}
		}
		return true;
	}

	public static void main(String[] args) {
		final String host = "0.0.0.0";
		final int port = 8887;
		server = new SimpleWebSocketServer(new InetSocketAddress(host, port));
		server.start();
	}

	public static boolean stopServer() throws InterruptedException {
		if (server == null) {
			return false;
		} else {
			server.stop();
			return true;
		}
	}
}