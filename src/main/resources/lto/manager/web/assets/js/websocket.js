function test() {
	const host = "ws://" + location.hostname + ":8887/time";
	console.log(host);
	const ws = new WebSocket(host);

	ws.onopen = (event) => {
		console.log("OPEN" + event);
	};

	ws.onclose = (event) => {
		if (event.wasClean) {
			console.log("CLOSED ok");
		} else {
			console.log("CLOSED bad");
		}
		console.log(`[close] Connection closed cleanly, code=${event.code} reason=${event.reason}`)
	};

	ws.onerror = (error) => {
		console.log("ERROR " + error);
	};

	ws.onmessage = (event) => {
		console.log("RX " + event.data);
	};
}
