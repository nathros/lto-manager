let timeout = setTimeout(update, 1000);
const uptime = document.getElementById("uptime");

async function update() {
	fetch("/api/systeminfo/",
	{
		method: "GET",
		signal: AbortSignal.timeout(3000)
	}).then((response) => {
		return response.json();
	}).then((json) => {
		// System uptime
		const softwareUpTime = json.message.nowTime - json.message.startTime;
		const softwareUpTimeDate = new Date(softwareUpTime);
		uptime.innerText = `${Math.floor(softwareUpTime / 86400000)} Days, ${softwareUpTimeDate.toISOString().slice(11, 19)}`;

		timeout = setTimeout(update, 1000); // Next update
	}).catch((error) => {
		console.log(error);
	});
}
