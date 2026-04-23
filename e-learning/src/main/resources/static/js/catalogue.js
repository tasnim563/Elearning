document.addEventListener("DOMContentLoaded", () => {
    const searchInput = document.getElementById("catalog-search");
    const levelSelect = document.getElementById("catalog-level");
    const cards = Array.from(document.querySelectorAll(".course-card"));
    const emptyState = document.getElementById("catalog-empty");

    const applyFilters = () => {
        const query = (searchInput?.value || "").trim().toLowerCase();
        const level = (levelSelect?.value || "").trim().toLowerCase();
        let visibleCount = 0;

        cards.forEach((card) => {
            const haystack = [
                card.dataset.title || "",
                card.dataset.description || "",
                card.dataset.category || "",
                card.dataset.level || ""
            ].join(" ").toLowerCase();

            const cardLevel = (card.dataset.level || "").toLowerCase();
            const matchesQuery = !query || haystack.includes(query);
            const matchesLevel = !level || cardLevel === level;
            const visible = matchesQuery && matchesLevel;

            card.hidden = !visible;
            if (visible) {
                visibleCount += 1;
            }
        });

        if (emptyState) {
            emptyState.hidden = visibleCount !== 0;
        }
    };

    if (searchInput) {
        searchInput.addEventListener("input", applyFilters);
    }

    if (levelSelect) {
        levelSelect.addEventListener("change", applyFilters);
    }

    document.querySelectorAll("[data-toggle-details]").forEach((button) => {
        button.addEventListener("click", () => {
            const card = button.closest(".course-card");
            const details = card?.querySelector(".course-details");
            if (!details) {
                return;
            }

            const expanded = !details.hidden;
            details.hidden = expanded;
            button.textContent = expanded ? "Details" : "Masquer";
        });
    });
});
