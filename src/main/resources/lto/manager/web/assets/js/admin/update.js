function openUpdateWS() {
	const progress = document.getElementById("progress");
	if (!progress) return; // Will only exist if can update
	const progressInline = document.getElementById("progress-inline");

	const ws = openWS("/ws/admin/update/progress/",
	(/* event */) => { // Open
		ws.send("status"); // Request status
	},
	(event) => { // Close
		//
	},
	(error) => { // Error
		//
	},
	(event) => { // RX
		console.log(event.data);
		if (event.data.startsWith("::")) {
			const split = event.data.split(":::");
			switch (split[0]) {
				case "::status":
					if (split[1] == "none") {
						// Do nothing
					} else {
						inlineMessage(progressInline, "Update already in progress", InlineMessage.Warning, null, true);
					}
					break;
				default:
			}
		} else {
			progress.value += event.data;
		}
	});
}

function performUpdate() {
	const progress = document.getElementById("progress");
	progress.style.display = "";
}

function startUpdate() {
	//showToastCallback(Toast.Warning, "Are you sure?", () => { performUpdate(); }, true);
	performUpdate();
}
