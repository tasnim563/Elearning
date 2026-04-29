import { LessonList, ProgressBar, QuestionCard, ResultScreen, el } from "./components.js";

const courseId = window.__COURSE_ID__;
const root = document.getElementById("course-root");
const navRoot = document.getElementById("lesson-nav");
const csrf = window.__CSRF__ || {};

// Reviews functionality
let selectedRating = 0;

async function fetchReviews() {
    const res = await fetch(`/api/avis/cours/${courseId}`);
    if (!res.ok) return [];
    return await res.json();
}

async function fetchReviewStats() {
    const res = await fetch(`/api/avis/cours/${courseId}/stats`);
    if (!res.ok) return { averageNote: 0, totalAvis: 0 };
    return await res.json();
}

async function fetchMyReview() {
    const res = await fetch(`/api/avis/my-avis/${courseId}`);
    if (!res.ok) return null;
    return await res.json();
}

async function submitReview(rating, comment) {
    const headers = { "Content-Type": "application/json", "Accept": "application/json" };
    if (csrf.token) {
        headers["X-CSRF-TOKEN"] = csrf.token;
    }
    const res = await fetch("/api/avis", {
        method: "POST",
        headers,
        credentials: "include",
        body: JSON.stringify({ coursId: courseId, note: rating, commentaire: comment })
    });
    if (!res.ok) throw new Error("Unable to submit review");
    return await res.json();
}

function renderStars(rating, containerId) {
    const container = document.getElementById(containerId);
    if (!container) return;
    
    const stars = container.querySelectorAll('.star');
    stars.forEach((star, index) => {
        if (index < Math.floor(rating)) {
            star.classList.add('filled');
        } else {
            star.classList.remove('filled');
        }
    });
}

function renderReviewForm(myReview) {
    const formContainer = document.getElementById('review-form-container');
    const starInput = document.getElementById('star-input');
    const commentInput = document.getElementById('review-comment');
    const submitBtn = document.getElementById('submit-review');
    
    if (!formContainer) return;
    
    if (myReview) {
        formContainer.querySelector('h3').textContent = 'Modifier mon avis';
        selectedRating = myReview.note;
        commentInput.value = myReview.commentaire || '';
    } else {
        formContainer.querySelector('h3').textContent = 'Laisser un avis';
        selectedRating = 0;
        commentInput.value = '';
    }
    
    renderStars(selectedRating, 'star-input');
    
    starInput.querySelectorAll('button').forEach(btn => {
        btn.addEventListener('click', () => {
            selectedRating = parseInt(btn.dataset.rating);
            renderStars(selectedRating, 'star-input');
        });
    });
    
    submitBtn.onclick = async () => {
        try {
            const comment = commentInput.value;
            await submitReview(selectedRating, comment);
            await loadReviews();
            formContainer.querySelector('h3').textContent = 'Modifier mon avis';
        } catch (error) {
            console.error('Error submitting review:', error);
        }
    };
}

function renderReviewsList(reviews) {
    const reviewsList = document.getElementById('reviews-list');
    if (!reviewsList) return;
    
    reviewsList.innerHTML = '';
    
    reviews.forEach(review => {
        const reviewCard = document.createElement('div');
        reviewCard.className = 'review-card';
        reviewCard.innerHTML = `
            <div class="review-header">
                <span class="review-author">${review.apprenant?.nom || 'Anonyme'}</span>
                <span class="review-date">${new Date(review.dateAvis).toLocaleDateString('fr-FR')}</span>
            </div>
            <div class="review-rating">
                ${renderStaticStars(review.note)}
            </div>
            <div class="review-content">
                ${review.commentaire || ''}
            </div>
        `;
        reviewsList.appendChild(reviewCard);
    });
}

function renderStaticStars(rating) {
    let stars = '';
    for (let i = 1; i <= 5; i++) {
        stars += `<span class="star ${i <= rating ? 'filled' : ''}">★</span>`;
    }
    return stars;
}

async function loadReviews() {
    try {
        const [reviews, stats, myReview] = await Promise.all([
            fetchReviews(),
            fetchReviewStats(),
            fetchMyReview()
        ]);
        
        // Update stats
        document.getElementById('average-rating').textContent = stats.averageNote.toFixed(1);
        document.getElementById('total-reviews').textContent = `(${stats.totalAvis} avis)`;
        renderStars(stats.averageNote, 'star-rating-display');
        
        // Render reviews list
        renderReviewsList(reviews);
        
        // Render form
        renderReviewForm(myReview);
    } catch (error) {
        console.error('Error loading reviews:', error);
    }
}

// Certificate functionality
function checkCertificateEligibility(progress) {
    const certificateSection = document.getElementById('certificate-section');
    const certificateDownload = document.getElementById('certificate-download');
    
    if (!certificateSection) return;
    
    if (progress >= 100) {
        certificateSection.style.display = 'block';
        certificateDownload.href = `/certificate/download/${courseId}`;
    } else {
        certificateSection.style.display = 'none';
    }
}

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

function renderRichText(content) {
    const blocks = String(content || "")
        .split(/\n{2,}/)
        .map(block => block.trim())
        .filter(Boolean);

    if (blocks.length === 0) {
        return [el("p", { class: "reading-empty" }, ["Contenu vide."])];
    }

    return blocks.flatMap((block) => {
        const lines = block.split("\n").map(line => line.trim()).filter(Boolean);
        const isList = lines.every(line => line.startsWith("- ") || line.startsWith("• "));

        if (isList) {
            return [
                el("ul", { class: "reading-list" }, lines.map(line =>
                    el("li", {}, [line.replace(/^[-•]\s+/, "")])
                ))
            ];
        }

        return lines.map(line => {
            if (/^#+\s+/.test(line)) {
                return el("h3", { class: "reading-subtitle" }, [line.replace(/^#+\s+/, "")]);
            }
            return el("p", {}, [line]);
        });
    });
}

function readingPane(lesson, onStartQuiz, onComplete, nextLesson, onNext) {
    return el("section", { class: "reading" }, [
        el("div", { class: "reading-top" }, [
            el("div", {}, [
                el("p", { class: "eyebrow" }, [`Lecon ${lesson.ordre}`]),
                el("h2", {}, [lesson.titre || "Lecon"]),
                el("div", { class: "reading-meta" }, [
                    el("span", { class: "pill soft" }, [`${lesson.dureeMin || 0} min`]),
                    el("span", { class: "pill" }, [lesson.completee ? "Terminee" : "Lecture guidee"])
                ])
            ]),
            el("div", { class: "reading-actions" }, [
                el("button", {
                    class: `button-secondary compact-button ${lesson.completee ? "is-complete" : ""}`,
                    type: "button",
                    disabled: lesson.completee ? "disabled" : null,
                    onClick: onComplete
                }, [lesson.completee ? "Terminee" : "Marquer termine"]),
                nextLesson ? el("button", { class: "button-secondary compact-button", type: "button", onClick: onNext }, ["Lecon suivante"]) : null,
                el("button", { class: "button-primary compact-button", type: "button", onClick: onStartQuiz }, ["Quiz"])
            ])
        ]),
        el("article", { class: "reading-body" }, [
            ...renderRichText(lesson.contenu)
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

async function completeLesson(lessonId) {
    const headers = { "Accept": "application/json", "Content-Type": "application/json" };
    if (csrf.token) {
        headers["X-CSRF-TOKEN"] = csrf.token;
    }
    console.log('Attempting to complete lesson:', lessonId);
    console.log('Course ID:', courseId);
    const url = `/api/progress/lessons/${lessonId}/complete`;
    console.log('API URL:', url);

    const res = await fetch(url, {
        method: "POST",
        headers,
        credentials: "include"
    });

    console.log('Complete lesson response status:', res.status);
    const responseText = await res.text();
    console.log('Response body:', responseText);

    if (!res.ok) {
        console.error('Failed to complete lesson:', res.status, responseText);
        let message = "Unable to save progress";
        if (res.status === 401) {
            message = "Session expired. Please log in again.";
        } else if (res.status === 403) {
            message = "Access denied.";
        } else if (res.status === 404) {
            message = "Lesson not found.";
        }
        const error = new Error(message);
        error.status = res.status;
        throw error;
    }
    const data = JSON.parse(responseText);
    console.log('Lesson completion successful:', data);
    return data;
}

function flattenLessons(modules) {
    return (modules || []).flatMap(module => module.lecons || []);
}

root.replaceChildren(skeleton());

try {
    const course = await fetchCourse();
    root.replaceChildren();
    root.appendChild(courseHeader(course));

    const modules = course.modules || [];
    const lessons = flattenLessons(modules);
    
    // Debug: log lessons to check completion status
    console.log('Lessons loaded:', lessons.map(l => ({ id: l.id, titre: l.titre, completee: l.completee })));
    
    const firstIncomplete = lessons.find(lesson => !lesson.completee);
    const firstLesson = firstIncomplete || lessons[0];
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

        const moduleIndex = modules.findIndex(module => (module.lecons || []).some(lesson => String(lesson.id) === String(selected.id)));
        const currentModule = moduleIndex >= 0 ? modules[moduleIndex] : null;
        const selectedIndex = lessons.findIndex(lesson => String(lesson.id) === String(selected.id));
        const nextLesson = selectedIndex >= 0 ? lessons[selectedIndex + 1] : null;
        const completedCount = lessons.filter(lesson => lesson.completee).length;
        const coursePct = lessons.length === 0 ? 0 : Math.round((completedCount / lessons.length) * 100);

        // Check certificate eligibility
        checkCertificateEligibility(coursePct);

        main.appendChild(el("div", { class: "course-focus" }, [
            el("div", { class: "course-focus-card" }, [
                el("p", { class: "eyebrow" }, [currentModule ? currentModule.titre : "Parcours"]),
                el("h3", {}, [selected.titre || "Lecon"]),
                el("p", { class: "muted-copy" }, [currentModule?.description || ""])
            ]),
            el("div", { class: "course-focus-card course-focus-card-soft" }, [
                el("p", { class: "eyebrow" }, ["Progression"]),
                el("strong", {}, [`${coursePct}%`]),
                el("p", { class: "muted-copy" }, [`${completedCount}/${lessons.length} lecons terminees`])
            ])
        ]));

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
        }, async () => {
            try {
                await completeLesson(selected.id);
                selected.completee = true;
                // Update the lesson in the lessons array as well
                const lessonInArray = lessons.find(l => String(l.id) === String(selected.id));
                if (lessonInArray) {
                    lessonInArray.completee = true;
                }
                renderLessonNav();
                await renderMain();
                main.prepend(el("div", { class: "toast success-toast" }, ["Progression sauvegardee."]));
                setTimeout(() => document.querySelector(".toast")?.remove(), 2200);
            } catch (error) {
                const message = error?.message || "Connectez-vous pour sauvegarder la progression.";
                main.prepend(el("div", { class: "toast error-toast" }, [message]));
                setTimeout(() => document.querySelector(".toast")?.remove(), 2600);
            }
        }, nextLesson, async () => {
            if (!nextLesson) return;
            selected = nextLesson;
            renderLessonNav();
            await renderMain();
        }));
    }

    renderLessonNav();
    await renderMain();
    
    // Load reviews on page load
    loadReviews();
} catch (e) {
    root.replaceChildren(el("div", { class: "empty-panel" }, ["Erreur de chargement."]));
}
