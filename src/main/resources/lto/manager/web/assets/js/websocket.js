function openWS(path, open, close, error, message) {
	const host = `ws://${location.hostname}:8887${path}`;
	const ws = new WebSocket(host);
	ws.onopen = open;
	ws.onclose = close;
	ws.onerror = error;
	ws.onmessage = message;
	return ws;
}
