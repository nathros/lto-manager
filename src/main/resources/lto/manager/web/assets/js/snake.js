var snake = new function() { // Isolates this code from other JavaScript

var showGrid = false; // Debug, toggle with � Tilde key.
var frameTime;

var speedIncrease = 2; // Frame time decrease amount when snake gets bigger.
var frameTimeStart = 100; // How long a frame lasts in milliseconds, adjust game speed.
var frameTimeMin = 20; // Minimum frame time.

var direction = {
	"LEFT": "LEFT",
	"UP": "UP",
	"RIGHT": "RIGHT",
	"DOWN": "DOWN"
};
var snakeDirection;
var snake = [];
var cellsXNum = 36;
var cellsYNum = 24;
var food;
var gameActive = false;
var gameEnd = false;
var showSplash = true;
var acceptInput = true;
var score = 0;

var canvas;
var ctx;
var drawInterval;
var bodyOverflowOriginal;

createSnakeGame();

function createSnakeGame() { // Create canvas, is exists then replace.
	canvas = document.getElementById("snake-canvas");
	if (canvas != null) {
		destroySnake();
	}
	canvas = document.createElement("canvas");
	ctx = canvas.getContext("2d");
	canvas.id = "snake-canvas";
	canvas.width = window.innerWidth;
	canvas.height = window.innerHeight;
	canvas.style.top = "0px";
	canvas.style.left = "0px";
	canvas.style.width = "100%";
	canvas.style.height = "100%";
	canvas.style.position = "fixed";
	bodyOverflowOriginal = document.body.style.overflow;
	document.body.style.overflow = "hidden";
	canvas.style.zIndex = "1000";
	document.body.appendChild(canvas);
	window.addEventListener("keydown", snakeKeyDown);
	window.addEventListener("resize", draw);
	canvas.addEventListener("click", click, false);
	canvas.addEventListener("mousemove", mouse, false);
	init();
	draw(false);
}



function init() {
	frameTime = frameTimeStart;
	gameEnd = false;
	acceptInput = true;
	score = 0;
	snakeDirection = direction.RIGHT;
	snake = [];
	snake.push({
		x: Math.floor(cellsXNum / 4),
		y: Math.floor(cellsYNum / 4)
	});
	food = { x: 0, y: 0 };
}

function mouse(e) { // Change mouse on cross top right corner.
	var yPos = e.pageY - window.pageYOffset;
	var xPos = e.pageX - window.pageXOffset;
	var cellSize = getCellSize();
	cellSize = cellSize > 24 ? cellSize : 24;
	cellSize = (cellSize * 1.5) * 1.25;
	if ((xPos > (window.innerWidth - cellSize)) && ((yPos - (cellSize / 4)) < cellSize))
		canvas.style.cursor = "pointer";
	else
		canvas.style.cursor = "default";
}

function click(e) { // Click cross to close snake.
	var yPos = e.pageY - window.pageYOffset;
	var xPos = e.pageX - window.pageXOffset;
	var cellSize = getCellSize();
	cellSize = cellSize > 24 ? cellSize : 24;
	cellSize = (cellSize * 1.5) * 1.25;
	if ((xPos > (window.innerWidth - cellSize)) && ((yPos - (cellSize / 4)) < cellSize)) destroySnake();
}

function newFood() { // TODO It is possible for new food to spawn on last snake segment.
	var xf = Math.floor(Math.random() * cellsXNum);
	var yf = Math.floor(Math.random() * cellsYNum);

	while (collision(xf, yf)) {
		xf++;
		if (xf > cellsXNum) {
			xf = 0;
			yf++;
		}
		if (yf > cellsYNum) {
			yf = 0;
		}
	}

	food = {
		x: xf,
		y: yf
	};
	// Speed game up every time snake gets bigger.
	frameTime -= speedIncrease;
	if (frameTime < frameTimeMin) frameTime = frameTimeMin;
}

function destroySnake() {
	var c = document.getElementById("snake-canvas");
	if (c != null) c.parentElement.removeChild(c); // Better: c.remove(); But IE strikes again.
	window.removeEventListener("keydown", snakeKeyDown);
	window.removeEventListener("resize", draw);
	window.removeEventListener("click", click);
	window.removeEventListener("mousemove", mouse);
	window.clearInterval(drawInterval);
	gameActive = false;
	gameEnd = false;
	showSplash = true;
	document.body.style.overflow = bodyOverflowOriginal;
}

function restartSnake() {
	window.clearInterval(drawInterval);
	init();
	newFood();	
	drawInterval = window.setTimeout(draw, frameTime, true); // Start game, draw calls update game state.
}

function drawSplash() {
	ctx.save();
	ctx.shadowColor = "white";
	ctx.shadowBlur = 3;
	var cellSize = getCellSize();
	ctx.font = "bold " + cellSize + "px Lucida Sans Unicode";
	var yPos = Math.floor(window.innerHeight / 2);
	
	var t1 = "You have stumbled upon...";
	var t1w = ctx.measureText(t1).width;	
	var t2 = "...a hidden game of snake";
	var t2w = ctx.measureText(t2).width;
	
	ctx.fillText(t1, Math.floor((window.innerWidth - t1w) / 2), yPos - (cellSize * 2));
	ctx.fillText(t1, Math.floor((window.innerWidth - t1w) / 2), yPos - (cellSize * 2));
	ctx.fillText(t2, Math.floor((window.innerWidth - t2w) / 2), yPos - cellSize + 5);
	ctx.fillText(t2, Math.floor((window.innerWidth - t2w) / 2), yPos - cellSize + 5);
	
	ctx.font = cellSize + "px Lucida Sans Unicode";
	var t3 = "Press [Space] to start";
	var t3w = ctx.measureText(t3).width;
	ctx.fillText(t3, Math.floor((window.innerWidth - t3w) / 2), yPos + (cellSize * 2));
	ctx.fillText(t3, Math.floor((window.innerWidth - t3w) / 2), yPos + (cellSize * 2));
	
	var t4 = "Controls: Arrow keys [\u2190][\u2192][\u2191][\u2193] or [W][A][S][D]";
	var t4w = ctx.measureText(t4).width;
	var y = Math.floor(((window.innerHeight - (cellsYNum * cellSize)) / 2) + (cellSize * cellsYNum) - (cellSize / 2));
	ctx.fillText(t4, Math.floor((window.innerWidth - t4w) / 2), y);
	ctx.fillText(t4, Math.floor((window.innerWidth - t4w) / 2), y);
	ctx.restore(); 
}

function gameOver() {
	gameActive = false;
	gameEnd = true;
	window.clearInterval(drawInterval);
	ctx.save();
	ctx.shadowColor = "white";
	ctx.shadowBlur = 3;
	var cellSize = getCellSize();
	ctx.font = "bold " + cellSize + "px Lucida Sans Unicode";
	var yPos = Math.floor(window.innerHeight / 2);
 
	var t2 = "GAME OVER";
	var t2w = ctx.measureText(t2).width;
	ctx.fillText(t2, Math.floor((window.innerWidth - t2w) / 2), yPos - cellSize + 5);
	ctx.fillText(t2, Math.floor((window.innerWidth - t2w) / 2), yPos - cellSize + 5);
	ctx.beginPath();
	
	ctx.font = cellSize + "px Lucida Sans Unicode";
	var t3 = "[Space] restart, [Esc] exit";
	var t3w = ctx.measureText(t3).width;
	ctx.fillText(t3, Math.floor((window.innerWidth - t3w) / 2), yPos + (cellSize * 2));
	ctx.fillText(t3, Math.floor((window.innerWidth - t3w) / 2), yPos + (cellSize * 2));

	
	var t4 = "Score: " + score;
	var t4w = ctx.measureText(t4).width;
	var y = Math.floor(((window.innerHeight - (cellsYNum * cellSize)) / 2) + (cellSize * cellsYNum) - (cellSize / 2));
	ctx.fillText(t4, Math.floor((window.innerWidth - t4w) / 2), y);
	ctx.fillText(t4, Math.floor((window.innerWidth - t4w) / 2), y);
	ctx.restore()
}

function snakeKeyDown(e) {
	switch (e.which) {
	case 65: // A.
	case 37: // left Arrow.
		if (acceptInput) if ((snake.length < 2) || (snakeDirection != direction.RIGHT)) snakeDirection = direction.LEFT;
		acceptInput = false;
		break;

	case 68: // D.
	case 39: // Right Arrow.
		if (acceptInput) if ((snake.length < 2) || (snakeDirection != direction.LEFT)) snakeDirection = direction.RIGHT;
		acceptInput = false;
		break;

	case 87: // W.
	case 38: // Up Arrow.
		if (acceptInput) if ((snake.length < 2) || (snakeDirection != direction.DOWN)) snakeDirection = direction.UP;
		acceptInput = false;
		break;

	case 83: // S.
	case 40: // Down Arrow.
		if (acceptInput) if ((snake.length < 2) || (snakeDirection != direction.UP)) snakeDirection = direction.DOWN;
		acceptInput = false;
		break;

	case 32: // Space.
		restartSnake();
		gameActive = true;
		showSplash = false;
		break;

	case 27: // Escape.
		destroySnake();
		break;

	case 223: // Tilde.
	case 192:
		showGrid = !showGrid;
		draw(false);
		break;
	}
}

function getCellSize() {
	var xCellSize = Math.floor(window.innerWidth / (cellsXNum + 2));
	var yCellSize = Math.floor(window.innerHeight / (cellsYNum + 2));
	return xCellSize <= yCellSize ? xCellSize : yCellSize;;
}

function collision(x, y) {
	if ((x < 0) || (x > (cellsXNum - 1)) || (y < 0) || (y > cellsYNum - 1)) return true; // Edge of area.
	var i;
	for (i = 0; i < snake.length - 1; i++) { // Snake with it self, not last segment as this will move.
		if ((snake[i].x == x) && (snake[i].y == y)) return true;		
	}
	return false;
}

function updateGameState() {
	var head = {
		x: 0,
		y: 0
	};
	head.x = snake[0].x;
	head.y = snake[0].y;

	switch (snakeDirection) {
	case direction.LEFT:
		head.x--;
		break;

	case direction.RIGHT:
		head.x++;
		break;

	case direction.UP:
		head.y--;
		break;

	case direction.DOWN:
		head.y++;
		break;
	}

	if (collision(head.x, head.y)) return true;

	snake.splice(0, 0, head);
	if ((head.x == food.x) && (head.y == food.y)) {
		score += 10;
		snake = snake.slice(0, snake.length);
		if ((cellsXNum * cellsYNum) == snake.length) {
			gameOver();
		} else {
			newFood();
		}
	} else {
		snake = snake.slice(0, snake.length - 1);
	}
	acceptInput = true;
}

function drawRoundRect(x, y, cellSize, r) {
	ctx.beginPath();	
	ctx.moveTo(x + r, y);
	ctx.arcTo(x + cellSize, y, x + cellSize, y + cellSize, r);
	ctx.arcTo(x + cellSize, y + cellSize, x, y + cellSize, r);
	ctx.arcTo(x, y + cellSize, x, y, r);
	ctx.arcTo(x, y, x + cellSize, y, r);
	ctx.closePath();
	ctx.fill();
}

function draw(advanceGameState) {
	if (gameActive && advanceGameState) {
		if (updateGameState()) {
			gameOver();
			return;
		}
	}

	let ratio = window.devicePixelRatio;
	if (ratio != 1) {
		ratio = window.devicePixelRatio;
		canvas.width = Math.ceil(window.innerWidth * ratio);
		canvas.height = Math.ceil(window.innerHeight * ratio);
		canvas.getContext("2d").scale(ratio, ratio);
	} else {
		canvas.width = window.innerWidth;
		canvas.height = window.innerHeight;
	}

	var cellSize = getCellSize();

	var xStart = Math.floor((window.innerWidth - (cellsXNum * cellSize)) / 2);
	var yStart = Math.floor((window.innerHeight - (cellsYNum * cellSize)) / 2);

	ctx.clearRect(0, 0, canvas.width, canvas.height);

	// Dark background.
	ctx.save();
	ctx.fillStyle = "rgba(0, 0, 0, 0.9)";
	ctx.fillRect(0, 0, canvas.width, canvas.height);
	ctx.restore();
	
	// Play area shadow.
	ctx.save();
	ctx.shadowColor = "black";
	ctx.shadowBlur = 20;
	ctx.fillRect(xStart, yStart, cellsXNum * cellSize, cellsYNum * cellSize);
	ctx.clearRect(xStart, yStart, cellsXNum * cellSize, cellsYNum * cellSize);
	ctx.restore();
	ctx.save();
	ctx.fillStyle = "rgba(255, 255, 255, 0.9)";
	ctx.fillRect(xStart, yStart, cellsXNum * cellSize, cellsYNum * cellSize);
	ctx.restore();

	// Draw exit cross
	var crossSize = cellSize > 24 ? cellSize : 24;
	crossSize = crossSize * 1.5;
	ctx.save();
	ctx.lineCap = "square"; 
	ctx.lineWidth = 4;
	ctx.strokeStyle = "white";
	ctx.fillStyle = "white";
	//ctx.shadowColor = "white"; // Shadow for cross and text
	//ctx.shadowBlur = 2;
	ctx.beginPath();
	const edge = 3
	ctx.moveTo(window.innerWidth - crossSize, edge);
	ctx.lineTo(window.innerWidth - edge, crossSize);
	ctx.moveTo(window.innerWidth - edge, edge);
	ctx.lineTo(window.innerWidth - crossSize, crossSize);
	ctx.stroke();
	ctx.font = " " + Math.floor(crossSize / 2.5) + "px Lucida Sans Unicode";
	ctx.fillText("close", window.innerWidth - crossSize, crossSize + Math.floor(crossSize / 2.5));	
	ctx.restore();

	if (showSplash) {
		drawSplash();
	} else {
		// Draw snake.
		ctx.save();
		var i, x, y;
		for (i = 0; i < snake.length; i++) {
			x = xStart + (snake[i].x * cellSize);
			y = yStart + (snake[i].y * cellSize);
			if (i == 0) ctx.fillStyle = "blue";
			else if (i == 1) ctx.fillStyle = "darkblue";
			else if (i == 2) ctx.fillStyle = "black";
			drawRoundRect(x + 1, y + 1, cellSize - 1, Math.floor(cellSize / 4));
		}

		// Draw food.
		x = xStart + (food.x * cellSize);
		y = yStart + (food.y * cellSize);
		ctx.fillStyle = "red";
		//ctx.shadowColor = "yellow";
		//ctx.shadowBlur = 3;
		//var grd=ctx.createRadialGradient(75,50,5,90,60,100);
		//grd.addColorStop(0,"red");
		//grd.addColorStop(1,"black");
		//ctx.fillStyle=grd;
		drawRoundRect(x + 1, y + 1, cellSize - 1, Math.floor(cellSize / 4));
		ctx.restore();
	}

	if (gameEnd) {
		gameOver();
	}

	if (showGrid) drawGrid(cellSize, xStart, yStart);
	if (advanceGameState == null || advanceGameState == true) drawInterval = window.setTimeout(draw, frameTime, true);
}

function drawGrid(cellSize, xStart, yStart) {
	var i;
	for (i = 0; i <= cellsYNum; i++) {
		ctx.beginPath();
		ctx.moveTo(xStart, yStart + 0.5 + (cellSize * i));
		ctx.lineTo(window.innerWidth - xStart, yStart + 0.5 + (cellSize * i));
		ctx.stroke();
	}
	for (i = 0; i <= cellsXNum; i++) {
		ctx.beginPath();
		ctx.moveTo(xStart + 0.5 + (cellSize * i), yStart);
		ctx.lineTo(xStart + 0.5 + (cellSize * i), window.innerHeight - yStart);
		ctx.stroke();
	}
	ctx.save();
	ctx.font = cellSize + "px Monospace";
	ctx.fillStyle = "green";
	ctx.fillText("frameTime     : " + frameTime, xStart, yStart + cellSize);
	ctx.fillText("frameTimeStart: " + frameTimeStart, xStart, yStart + (cellSize * 2));
	ctx.fillText("frameTimeMin  : " + frameTimeMin, xStart, yStart + (cellSize * 3));
	ctx.fillText("snakeDirection: " + snakeDirection, xStart, yStart + (cellSize * 4));
	ctx.fillText("snakeHeadX    : " + snake[0].x, xStart, yStart + (cellSize * 5));
	ctx.fillText("snakeHeadY    : " + snake[0].y, xStart, yStart + (cellSize * 6));
	ctx.fillText("snakeFoodX    : " + food.x, xStart, yStart + (cellSize * 7));
	ctx.fillText("snakeFoodY    : " + food.y, xStart, yStart + (cellSize * 8));
	ctx.restore();
}

return {
	createSnake: function()  {
		createSnakeGame();
	}
}

};
