export function el(tag, attrs = {}, children = []) {
    const node = document.createElement(tag);
    Object.entries(attrs || {}).forEach(([key, value]) => {
        if (value === undefined || value === null) {
            return;
        }
        if (key === "class") {
            node.className = String(value);
            return;
        }
        if (key.startsWith("on") && typeof value === "function") {
            node.addEventListener(key.slice(2).toLowerCase(), value);
            return;
        }
        if (key === "dataset") {
            Object.entries(value).forEach(([k, v]) => {
                node.dataset[k] = String(v);
            });
            return;
        }
        node.setAttribute(key, String(value));
    });

    const append = (child) => {
        if (child === undefined || child === null) return;
        if (Array.isArray(child)) {
            child.forEach(append);
            return;
        }
        node.appendChild(typeof child === "string" ? document.createTextNode(child) : child);
    };
    append(children);
    return node;
}

export function CourseCard(course) {
    const cover = course.imageUrl
        ? el("img", { src: course.imageUrl, alt: course.titre })
        : el("div", { class: "card-cover-fallback" }, ["Course"]);

    return el("article", { class: "card course-card2" }, [
        el("a", { class: "card-cover", href: `/cours/${course.id}` }, [cover]),
        el("div", { class: "card-body" }, [
            el("div", { class: "card-meta" }, [
                el("span", { class: "pill" }, [course.categorie || "Cours"]),
                el("span", { class: "pill soft" }, [course.niveau || "Niveau"])
            ]),
            el("h2", { class: "card-title" }, [course.titre || "Cours"]),
            el("p", { class: "card-copy clamped-copy" }, [course.description || ""])
        ])
    ]);
}

export function LessonList(modules, onSelect, selectedLessonId) {
    const items = [];
    (modules || []).forEach((moduleItem) => {
        items.push(el("div", { class: "lesson-group" }, [
            el("p", { class: "lesson-group-title" }, [moduleItem.titre || "Module"])
        ]));
        (moduleItem.lecons || []).forEach((lesson) => {
            const active = String(lesson.id) === String(selectedLessonId);
            items.push(el("button", {
                class: `lesson-link ${active ? "active" : ""}`,
                type: "button",
                onClick: () => onSelect(lesson)
            }, [
                el("span", { class: "lesson-link-title" }, [lesson.titre || "Lecon"]),
                el("span", { class: "lesson-link-meta" }, [`${lesson.dureeMin || 0} min`])
            ]));
        });
    });
    return el("div", { class: "lesson-list2" }, items);
}

export function ProgressBar(current, total) {
    const pct = total <= 0 ? 0 : Math.round((current / total) * 100);
    return el("div", { class: "quiz-progress" }, [
        el("div", { class: "quiz-progress-top" }, [
            el("span", { class: "quiz-progress-label" }, [`${current}/${total}`]),
            el("span", { class: "quiz-progress-pct" }, [`${pct}%`])
        ]),
        el("div", { class: "quiz-progress-track" }, [
            el("div", { class: "quiz-progress-fill", style: `width:${pct}%` }, [])
        ])
    ]);
}

export function QuestionCard(question, state, onAnswer) {
    const locked = state.locked;
    const choices = (question.choix || []).map((choice, idx) => {
        const picked = state.pickedIndex === idx;
        const correct = question.bonneReponse === idx;
        const show = locked && (picked || correct);
        const cls = [
            "quiz-choice2",
            picked ? "picked" : "",
            show && correct ? "correct" : "",
            show && picked && !correct ? "wrong" : "",
            locked ? "locked" : ""
        ].filter(Boolean).join(" ");

        return el("button", {
            type: "button",
            class: cls,
            onClick: () => {
                if (locked) return;
                onAnswer(idx);
            }
        }, [choice]);
    });

    const feedback = locked
        ? el("div", { class: "quiz-feedback" }, [
            el("span", { class: state.isCorrect ? "ok" : "no" }, [state.isCorrect ? "Correct" : "Incorrect"]),
            question.explication ? el("p", { class: "quiz-explain" }, [question.explication]) : null
        ])
        : null;

    return el("div", { class: "quiz-card" }, [
        el("h2", { class: "quiz-q" }, [question.intitule || "Question"]),
        el("div", { class: "quiz-choices2" }, choices),
        feedback
    ]);
}

export function ResultScreen(score, total, onRestart) {
    const pct = total <= 0 ? 0 : Math.round((score / total) * 100);
    const msg = pct >= 80 ? "Excellent." : pct >= 50 ? "Bien." : "Reessaie.";
    return el("div", { class: "result-card2" }, [
        el("p", { class: "eyebrow" }, ["Resultat"]),
        el("h2", {}, [msg]),
        el("div", { class: "result-score2" }, [
            el("span", {}, ["Score"]),
            el("strong", {}, [`${score}/${total}`])
        ]),
        el("button", { type: "button", class: "button-primary compact-button", onClick: onRestart }, ["Recommencer"])
    ]);
}

