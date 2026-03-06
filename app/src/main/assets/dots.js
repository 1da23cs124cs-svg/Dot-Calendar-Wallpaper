// Get the canvas
const canvas = document.getElementById("dotsCanvas");
const ctx = canvas.getContext("2d");

// Convert cm to pixels (approx)
const CM_TO_PX = 37.7952755906;

// Desired margins
const marginTop = 3 * CM_TO_PX;
const marginBottom = 1.5 * CM_TO_PX;
const marginLeft = 1 * CM_TO_PX;
const marginRight = 1 * CM_TO_PX;

// Function to resize canvas based on window size and margins
function resizeCanvas() {
  canvas.width = window.innerWidth - marginLeft - marginRight;
  canvas.height = window.innerHeight - marginTop - marginBottom;
  drawDots();
}

// Dot calendar data
const now = new Date();
const startOfYear = new Date(now.getFullYear(), 0, 1);
const endOfYear = new Date(now.getFullYear(), 11, 31);
const totalDays = Math.round((endOfYear - startOfYear) / (1000 * 60 * 60 * 24)) + 1;
const todayIndex = Math.floor((now - startOfYear) / (1000 * 60 * 60 * 24));

// Grid settings
const cols = 14;
const dotSpacing = 40; // distance between dots
const dotRadius = 8;
const rows = Math.ceil(totalDays / cols);

// Draw all dots centered in canvas
function drawDots() {
  ctx.clearRect(0, 0, canvas.width, canvas.height);

  const totalWidth = (cols - 1) * dotSpacing;
  const totalHeight = (rows - 1) * dotSpacing;

  const offsetX = (canvas.width - totalWidth) / 2;
  const offsetY = (canvas.height - totalHeight) / 2;

  for (let i = 0; i < totalDays; i++) {
    const x = offsetX + (i % cols) * dotSpacing;
    const y = offsetY + Math.floor(i / cols) * dotSpacing;

    if (i < todayIndex) {
      ctx.fillStyle = "gray"; // past days
    } else if (i === todayIndex) {
      ctx.fillStyle = "maroon"; // today
    } else {
      ctx.fillStyle = "darkgray"; // future days
    }

    ctx.beginPath();
    ctx.arc(x, y, dotRadius, 0, 2 * Math.PI);
    ctx.fill();
  }
}

// Initial draw and responsive handling
window.addEventListener("resize", resizeCanvas);
resizeCanvas();

// Optional: redraw every hour to stay updated
setInterval(drawDots, 1000 * 60 * 60);
