function testWS(sender) {
	if (testWS.ws !== undefined) {
		testWS.ws.close();
		sender.classList.remove("background-green");
		sender.classList.remove("background-error");
		sender.innerText = "Connect";
		testWS.ws = undefined;
		return;
	}
	if (testWS.event === undefined) {
		testWS.event = document.getElementById("ws-event");
		testWS.event.value = "";
		testWS.rx = document.getElementById("ws-rx");
		testWS.rx.value = "";
	}
	const path = document.getElementById("ws-path").value;
	if (path === "") {
		alert("Please choose a path as none has been selected");
		return;
	}
	sender.classList.add("background-green");
	sender.classList.remove("background-error");
	sender.innerText = "Close";

	testWS.ws = openWS(path,
	(event) => { // Open
		testWS.event.value += `Open new connection path=[${event.target.url}]\n`;
	},
	(event) => { // Close
		sender.classList.remove("background-green");
		sender.classList.add("background-error");
		sender.innerText = "Closed";
		testWS.event.value += `Connection closed, code=${event.code} clean=${event.wasClean} reason=${event.reason}\n`;
	},
	(error) => { // Error
		testWS.event.value += `Error [${error}]\n`;
		testWS.event.scrollTo({top: testWS.event.scrollHeight, behavior: "smooth"});
	},
	(event) => { // RX
		if (event.data.endsWith("\n")) {
			testWS.rx.value += event.data;
		} else {
			testWS.rx.value += event.data + "\n";
		}
		testWS.rx.scrollTo({top: testWS.rx.scrollHeight, behavior: "smooth"});
	});
}