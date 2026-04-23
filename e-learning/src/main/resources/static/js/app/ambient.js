const root = document.documentElement;

function clamp(v, min, max) {
  return Math.min(max, Math.max(min, v));
}

function setVar(name, value) {
  root.style.setProperty(name, value);
}

function onMove(e) {
  const w = window.innerWidth || 1;
  const h = window.innerHeight || 1;
  const x = (e.clientX / w) * 2 - 1;
  const y = (e.clientY / h) * 2 - 1;
  setVar("--mx", x.toFixed(3));
  setVar("--my", y.toFixed(3));
}

let lastY = 0;
function onScroll() {
  const y = window.scrollY || 0;
  // subtle parallax drift (small numbers for “cinematic” feel)
  const dy = clamp((y - lastY) / 800, -0.06, 0.06);
  lastY = y;
  const cur = parseFloat(getComputedStyle(root).getPropertyValue("--py") || "0") || 0;
  setVar("--py", (cur + dy).toFixed(3));
}

function init() {
  setVar("--mx", "0");
  setVar("--my", "0");
  setVar("--py", "0");
  window.addEventListener("mousemove", onMove, { passive: true });
  window.addEventListener("scroll", onScroll, { passive: true });
}

if (document.readyState === "loading") {
  document.addEventListener("DOMContentLoaded", init);
} else {
  init();
}

