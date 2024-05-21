const tableLog = document.getElementById("table-logging");
tableLog.observerSkip = false;
let filterConfig = getFilter();
document.getElementById("FINEST").checked = filterConfig["FINEST"];
document.getElementById("FINER").checked = filterConfig["FINER"];
document.getElementById("FINE").checked = filterConfig["FINE"];
document.getElementById("INFO").checked = filterConfig["INFO"];
document.getElementById("WARNING").checked = filterConfig["WARNING"];
document.getElementById("SEVERE").checked = filterConfig["SEVERE"];
let autoScroll = getAutoscroll();
document.getElementById("autoscroll").checked = autoScroll;
let enableCompact = getCompact();
document.getElementById("compact").checked = enableCompact;
setCompact(enableCompact);

function levelAction(level, func, def) {
	switch (level) {
		case "FINEST": return filterConfig["FINEST"] ? def() : func();
		case "FINER": return filterConfig["FINER"] ? def() : func();
		case "FINE": return filterConfig["FINE"] ? def() : func();
		case "INFO": return filterConfig["INFO"] ? def() : func();
		case "WARNING": return filterConfig["WARNING"] ? def() : func();
		case "SEVERE": return filterConfig["SEVERE"] ? def() : func();
		default: console.log(`Unknown log level: ${level}`);
	}
}
function splitMessage(lines) {
	const messages = lines.split("\n");
	messages.pop(); // Last is empty
	let results = [];
	messages.forEach(function(item) {
		let row = [];
		row.push(item.substring(0, 23)); // Get timestamp
		row.push(item.substring(24, 31).trimEnd()); // Get level
		let firstIndex = item.indexOf(" ", 36);
		if (firstIndex == -1) { console.log(`1:parse failure ${item}`); return; }
		let tmp = item.substring(31, firstIndex).trimEnd(); // Get class
		tmp = tmp.substring(tmp.lastIndexOf(".") + 1); // Remove package name 
		row.push(tmp);
		let lastIndex = item.indexOf(" ", firstIndex + 1);
		if (lastIndex == -1) { console.log(`2:parse failure ${item}`); return; }
		row.push(item.substring(firstIndex, lastIndex)); // Get function name
		firstIndex = item.indexOf("|", lastIndex);
		if (firstIndex == -1) { console.log(`3:parse failure ${item}`); return; }
		row.push(item.substring(firstIndex + 2)); // Get message
		results.push(row);
	});
	return results;
}
function addTableMessage(lines) {
	const processedMsg = splitMessage(lines);
	if (lines.length > 128) tableLog.style.display = "none";
	processedMsg.forEach(function(line) {
		const row = tableLog.insertRow();
		row.insertCell().innerText = line[0];
		const cell1 = row.insertCell();
		cell1.classList.add(line[1]);
		cell1.innerText = line[1];
		levelAction(line[1], () => {row.style.display = "none"}, () => {});
		row.insertCell().innerText = line[2];
		row.insertCell().innerText = line[3];
		row.insertCell().innerText = line[4];
	});
	if (lines.length > 128) tableLog.style.display = "initial";
}
function scrollToPos(bottom) {
	const cont = document.getElementById("main-content-wrapper");
	const pos = bottom ? cont.children[0].scrollHeight :0;
	cont.scrollTo({top: pos, behavior: "smooth"});
}
function scrollToBottom() {
	if (tableLog.style.display != "none" && !tableLog.observerSkip && autoScroll) {
		scrollToPos(true);
	}
	tableLog.observerSkip = false;
}
new ResizeObserver(scrollToBottom).observe(document.getElementById("main-content-wrapper").children[0]);
function selectSingle(sender) {
	document.getElementById("FINEST").checked =
	document.getElementById("FINER").checked =
	document.getElementById("FINE").checked =
	document.getElementById("INFO").checked =
	document.getElementById("WARNING").checked =
	document.getElementById("SEVERE").checked = false;
	sender.getElementsByTagName("input")[0].checked = true;
	return false; // Disable context menu
}
function applyFilter() {
	filterConfig["FINEST"] = document.getElementById("FINEST").checked;
	filterConfig["FINER"] = document.getElementById("FINER").checked;
	filterConfig["FINE"] = document.getElementById("FINE").checked;
	filterConfig["INFO"] = document.getElementById("INFO").checked;
	filterConfig["WARNING"] = document.getElementById("WARNING").checked;
	filterConfig["SEVERE"] = document.getElementById("SEVERE").checked;
	setCookie("log-filter", JSON.stringify(filterConfig), 365);
	tableLog.style.display = "none"; // Better performance if table is hidden
	for (let i = 1; i < tableLog.rows.length; i++) {
		const row = tableLog.rows[i];
		levelAction(tableLog.rows[i].children[1].className, () => {row.style.display = "none"}, () => {row.style.display = "table-row"});
	}
	tableLog.observerSkip = true;
	tableLog.style.display = "initial"; // Restore table
}
function resetFilter() {
	document.getElementById("FINEST").checked =
	document.getElementById("FINER").checked =
	document.getElementById("FINE").checked =
	document.getElementById("INFO").checked =
	document.getElementById("WARNING").checked =
	document.getElementById("SEVERE").checked = true;
	applyFilter();
}
function getFilter() {
	const filterCookie = getCookie("log-filter");
	if (filterCookie == null) {
		return {
			"FINEST" : document.getElementById("FINEST").checked,
			"FINER" : document.getElementById("FINER").checked,
			"FINE" : document.getElementById("FINE").checked,
			"INFO" : document.getElementById("INFO").checked,
			"WARNING" : document.getElementById("WARNING").checked,
			"SEVERE" : document.getElementById("SEVERE").checked,
		};
	} else {
		try {
			const filter = JSON.parse(filterCookie);
			if (filter["SEVERE"] === undefined) throw new Error("Missing field");
			return filter;
		} catch (e) {
			console.log(`Failed to parse filter cookie: [${filterCookie}] error: ${e.message}`);
			eraseCookie("log-filter"); // Remove bad value
			return getFilter(); // Call again which will return default, careful of infinite loop
		}
	}
}
function getAutoscroll() {
	const scroll = getCookie("log-autoscroll");
	return (scroll == null) ? true : (scroll == COOKIE_ON);
}
function setAutoscroll(value) {
	setCookie("log-autoscroll", value == true ? COOKIE_ON : COOKIE_OFF, 365);
}
function getCompact() {
	const enabled = getCookie("log-compact");
	return (enabled == null) ? false : (enabled == COOKIE_ON);
}
function setCompact(enabled) {
	const root = document.querySelector(':root');
	root.style.setProperty('--log-pad', enabled ? "0px" : "var(--padding)");
	tableLog.observerSkip = true;
	setCookie("log-compact", enabled ? COOKIE_ON : COOKIE_OFF, 365);
}
function downloadLogFile() {
	const tmpWS = openWS("/ws/logging",
	(/*event*/) => { /* Open */ },
	(/*event*/) => { /* Close */ },
	(error) => { // Error
		console.log(`Failed to download: ${error.message}`);
	},
	(event) => { // RX
		tmpWS.close();
		let e = document.createElement('a');
		e.setAttribute('href', 'data:text/plain;charset=utf-8,' + encodeURIComponent(event.data));
		e.setAttribute('download', "logfile.txt");
		e.style.display = 'none';
		document.body.appendChild(e);
		e.click();
		document.body.removeChild(e);
	});
}
const tableWS = openWS("/ws/logging/",
(/*event*/) => { /* Open */},
(event) => { // Close
	alert("closed");
	console.log(event);
},
(error) => { // Error
	alert("error");
	console.log(error);
},
(event) => { // RX
	//console.log(event.data);
	addTableMessage(event.data);
});
