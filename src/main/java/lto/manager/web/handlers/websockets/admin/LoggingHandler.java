package lto.manager.web.handlers.websockets.admin;

import lto.manager.web.handlers.websockets.BaseWebsocketHandler;

public class LoggingHandler extends BaseWebsocketHandler {
	public static final String PATH = "/ws/logging";

	@Override
	public boolean start() {
		return false;
	}

	@Override
	public void onNewMessage(String message) {


	}


}
