function openWS(path, open, close, error, message) {
	const protocol = document.location.protocol.includes("s") ? "wss" : "ws";
	const host = `${protocol}://${location.hostname}:8887${path}`;
	const ws = new WebSocket(host);
	ws.onopen = open;
	ws.onclose = close;
	ws.onerror = error;
	ws.onmessage = message;
	return ws;
}
