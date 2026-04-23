import { CourseCard, el } from "./components.js";

const root = document.getElementById("courses-root");
const search = document.getElementById("course-search");
const level = document.getElementById("course-level");

function skeletonGrid() {
    return el("div", { class: "skeleton-grid" }, Array.from({ length: 6 }).map(() =>
        el("div", { class: "skeleton-card" }, [
            el("div", { class: "skeleton-cover" }),
            el("div", { class: "skeleton-line w60" }),
            el("div", { class: "skeleton-line w90" })
        ])
    ));
}

function applyFilters(courses) {
    const q = (search?.value || "").trim().toLowerCase();
    const lvl = (level?.value || "").trim().toLowerCase();
    return courses.filter((c) => {
        const hay = `${c.titre || ""} ${c.description || ""} ${c.categorie || ""} ${c.niveau || ""}`.toLowerCase();
        const okQ = !q || hay.includes(q);
        const okL = !lvl || (String(c.niveau || "").toLowerCase() === lvl);
        return okQ && okL;
    });
}

async function loadCourses() {
    root.replaceChildren(skeletonGrid());
    const res = await fetch("/api/courses");
    if (!res.ok) throw new Error("Failed to load courses");
    const data = await res.json();
    return Array.isArray(data) ? data : [];
}

let allCourses = [];

function render() {
    const filtered = applyFilters(allCourses);
    if (filtered.length === 0) {
        root.replaceChildren(el("div", { class: "empty-panel" }, ["Aucun cours."]));
        return;
    }
    root.replaceChildren(...filtered.map(CourseCard));
}

try {
    allCourses = await loadCourses();
    render();
    search?.addEventListener("input", render);
    level?.addEventListener("change", render);
} catch (e) {
    root.replaceChildren(el("div", { class: "empty-panel" }, ["Erreur de chargement."]));
}

