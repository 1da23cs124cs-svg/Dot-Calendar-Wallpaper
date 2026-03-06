const dotsContainer = document.getElementById("dots");

const now = new Date();
const startOfYear = new Date(now.getFullYear(), 0, 1);
const endOfYear = new Date(now.getFullYear(), 11, 31);

const totalDays =
  Math.round((endOfYear - startOfYear) / (1000 * 60 * 60 * 24)) + 1;

const todayIndex =
  Math.floor((now - startOfYear) / (1000 * 60 * 60 * 24));

for (let i = 0; i < totalDays; i++) {
  const dot = document.createElement("div");
  dot.classList.add("dot");

  if (i < todayIndex) {
    dot.classList.add("past");
  }

  dotsContainer.appendChild(dot);
}
