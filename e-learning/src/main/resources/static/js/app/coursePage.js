import { LessonList, ProgressBar, QuestionCard, ResultScreen, el } from "./components.js";

const courseId = window.__COURSE_ID__;
const root = document.getElementById("course-root");
const navRoot = document.getElementById("lesson-nav");

function skeleton() {
    return el("div", { class: "course-skeleton" }, [
        el("div", { class: "skeleton-line w60" }),
        el("div", { class: "skeleton-line w90" }),
        el("div", { class: "skeleton-cover" })
    ]);
}

function courseHeader(course) {
    return el("section", { class: "course-header" }, [
        el("div", { class: "course-header-media" }, [
            course.imageUrl ? el("img", { src: course.imageUrl, alt: course.titre }) : el("div", { class: "card-cover-fallback" }, ["Course"])
        ]),
        el("div", { class: "course-header-copy" }, [
            el("p", { class: "eyebrow" }, [course.categorie || "Cours"]),
            el("h1", {}, [course.titre || "Cours"]),
            el("p", { class: "lead" }, [course.description || ""])
        ])
    ]);
}

function readingPane(lesson, onStartQuiz) {
    return el("section", { class: "reading" }, [
        el("div", { class: "reading-top" }, [
            el("div", {}, [
                el("p", { class: "eyebrow" }, [`Lecon ${lesson.ordre}`]),
                el("h2", {}, [lesson.titre || "Lecon"]),
                el("p", { class: "reading-meta" }, [`${lesson.dureeMin || 0} min`])
            ]),
            el("button", { class: "button-primary compact-button", type: "button", onClick: onStartQuiz }, ["Quiz"])
        ]),
        el("article", { class: "reading-body" }, [
            el("p", {}, [lesson.contenu || ""])
        ])
    ]);
}

function quizPane(lesson, quiz, onClose) {
    const questions = quiz.questions || [];
    let index = 0;
    let score = 0;
    let state = { locked: false, pickedIndex: null, isCorrect: false };

    const container = el("section", { class: "quiz-pane" }, []);
    const header = el("div", { class: "quiz-pane-header" }, [
        el("div", {}, [
            el("p", { class: "eyebrow" }, ["Quiz"]),
            el("h2", {}, [quiz.titre || "Quiz"]),
            el("p", { class: "reading-meta" }, [lesson.titre || ""])
        ]),
        el("button", { class: "button-secondary compact-button", type: "button", onClick: onClose }, ["Fermer"])
    ]);

    const body = el("div", { class: "quiz-pane-body" }, []);
    const footer = el("div", { class: "quiz-pane-footer" }, []);

    function render() {
        body.replaceChildren();
        footer.replaceChildren();

        if (index >= questions.length) {
            body.appendChild(ResultScreen(score, questions.length, () => {
                index = 0;
                score = 0;
                state = { locked: false, pickedIndex: null, isCorrect: false };
                render();
            }));
            return;
        }

        const q = questions[index];
        body.appendChild(ProgressBar(index + 1, questions.length));

        body.appendChild(QuestionCard(q, state, (pickedIdx) => {
            const ok = pickedIdx === q.bonneReponse;
            state = { locked: true, pickedIndex: pickedIdx, isCorrect: ok };
            if (ok) score += 1;

            // subtle animation: lock then enable next
            render();
            footer.appendChild(el("button", {
                type: "button",
                class: "button-primary compact-button",
                onClick: () => {
                    index += 1;
                    state = { locked: false, pickedIndex: null, isCorrect: false };
                    render();
                }
            }, [index + 1 === questions.length ? "Terminer" : "Suivant"]));
        }));
    }

    container.appendChild(header);
    container.appendChild(body);
    container.appendChild(footer);
    render();
    return container;
}

async function fetchCourse() {
    const res = await fetch(`/api/courses/${courseId}`);
    if (!res.ok) throw new Error("Course not found");
    return await res.json();
}

async function fetchQuiz(lessonId) {
    const res = await fetch(`/api/quizzes/${lessonId}`);
    if (!res.ok) return null;
    return await res.json();
}

root.replaceChildren(skeleton());

try {
    const course = await fetchCourse();
    root.replaceChildren();
    root.appendChild(courseHeader(course));

    const modules = course.modules || [];
    const firstLesson = modules.flatMap(m => m.lecons || [])[0];
    let selected = firstLesson;

    const main = el("div", { class: "course-main" }, []);
    root.appendChild(main);

    function renderLessonNav() {
        navRoot.replaceChildren(LessonList(modules, (lesson) => {
            selected = lesson;
            renderMain();
            renderLessonNav();
        }, selected?.id));
    }

    async function renderMain() {
        main.replaceChildren();
        if (!selected) {
            main.appendChild(el("div", { class: "empty-panel" }, ["Aucune lecon."]));
            return;
        }

        main.appendChild(readingPane(selected, async () => {
            const quiz = await fetchQuiz(selected.id);
            if (!quiz) {
                main.appendChild(el("div", { class: "toast" }, ["Pas de quiz pour cette lecon."]));
                setTimeout(() => document.querySelector(".toast")?.remove(), 2200);
                return;
            }
            const pane = quizPane(selected, quiz, () => {
                pane.remove();
            });
            main.prepend(pane);
        }));
    }

    renderLessonNav();
    await renderMain();
} catch (e) {
    root.replaceChildren(el("div", { class: "empty-panel" }, ["Erreur de chargement."]));
}

