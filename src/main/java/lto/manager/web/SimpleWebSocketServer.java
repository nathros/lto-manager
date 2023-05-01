package lto.manager.web;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import lto.manager.web.handlers.Handlers;

public class SimpleWebSocketServer extends WebSocketServer {
	private final static int EVENT_CODE_PATH_NOT_FOUND = 3001;

	public SimpleWebSocketServer(InetSocketAddress address) {
		super(address);
	}

	@Override
	public void onOpen(WebSocket conn, ClientHandshake handshake) {
		//conn.send("Welcome to the server!"); //This method sends a message to the new client
		//broadcast( "new connection: " + handshake.getResourceDescriptor() ); //This method sends a message to all clients connected
		//System.out.println("new connection to " + conn.getRemoteSocketAddress());


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
			//conn.send("ERROR: " + path + " not found");
			conn.close(EVENT_CODE_PATH_NOT_FOUND, "path: " + path + " not found");
		}
	}

	@Override
	public void onClose(WebSocket conn, int code, String reason, boolean remote) {
		System.out.println("closed " + conn.getRemoteSocketAddress() + " with exit code " + code + " additional info: " + reason);
	}

	@Override
	public void onMessage(WebSocket conn, String message) {
		System.out.println("received message from "	+ conn.getRemoteSocketAddress() + ": " + message);
		conn.send("echo " + message);

	}

	@Override
	public void onMessage( WebSocket conn, ByteBuffer message ) {
		System.out.println("received ByteBuffer from "	+ conn.getRemoteSocketAddress());
	}

	@Override
	public void onError(WebSocket conn, Exception ex) {
		System.err.println("an error occurred on connection " + conn.getRemoteSocketAddress()  + ":" + ex);
	}

	@Override
	public void onStart() {
		System.out.println("Websocket server started at ws://" +  getAddress().getHostName() + ":" + getAddress().getPort());
	}


	public static void main(String[] args) {
		String host = "0.0.0.0";
		int port = 8887;
		WebSocketServer server = new SimpleWebSocketServer(new InetSocketAddress(host, port));
		server.start();

	}
}