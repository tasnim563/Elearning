// ============================================
// QUIZ INTERACTIONS
// Interactive answer cards, selection states, animations
// ============================================

document.addEventListener('DOMContentLoaded', () => {
    // Handle answer card selection
    const answerCards = document.querySelectorAll('.answer-card');
    
    answerCards.forEach(card => {
        const radio = card.querySelector('input[type="radio"]');
        
        card.addEventListener('click', () => {
            // Remove selected state from all cards in the same question
            const questionCard = card.closest('.question-card');
            const allCardsInQuestion = questionCard.querySelectorAll('.answer-card');
            
            allCardsInQuestion.forEach(c => {
                c.classList.remove('selected');
                const r = c.querySelector('input[type="radio"]');
                if (r) r.checked = false;
            });
            
            // Add selected state to clicked card
            card.classList.add('selected');
            if (radio) radio.checked = true;
            
            // Add haptic feedback (if supported)
            if (navigator.vibrate) {
                navigator.vibrate(10);
            }
        });
        
        // Add keyboard support
        card.addEventListener('keydown', (e) => {
            if (e.key === 'Enter' || e.key === ' ') {
                e.preventDefault();
                card.click();
            }
        });
        
        // Make card focusable
        card.setAttribute('tabindex', '0');
        card.setAttribute('role', 'radio');
        card.setAttribute('aria-checked', 'false');
        
        // Update aria-checked on selection
        radio.addEventListener('change', () => {
            const allCardsInQuestion = card.closest('.question-card').querySelectorAll('.answer-card');
            allCardsInQuestion.forEach(c => {
                c.setAttribute('aria-checked', 'false');
            });
            card.setAttribute('aria-checked', 'true');
        });
    });
    
    // Add smooth scroll to first question on load
    const firstQuestion = document.querySelector('.question-card');
    if (firstQuestion) {
        firstQuestion.scrollIntoView({ behavior: 'smooth', block: 'start' });
    }
    
    // Animate progress dots on scroll
    const progressDots = document.querySelectorAll('.progress-dot');
    const questionCards = document.querySelectorAll('.question-card');
    
    const observerOptions = {
        threshold: 0.5,
        rootMargin: '-20% 0px -20% 0px'
    };
    
    const questionObserver = new IntersectionObserver((entries) => {
        entries.forEach(entry => {
            if (entry.isIntersecting) {
                const index = Array.from(questionCards).indexOf(entry.target);
                progressDots.forEach((dot, i) => {
                    dot.classList.remove('current');
                    if (i === index) {
                        dot.classList.add('current');
                    } else if (i < index) {
                        dot.classList.add('completed');
                    }
                });
            }
        });
    }, observerOptions);
    
    questionCards.forEach(card => questionObserver.observe(card));
    
    // Add submit button animation
    const submitBtn = document.querySelector('.btn-quiz-primary');
    if (submitBtn) {
        submitBtn.addEventListener('click', () => {
            submitBtn.innerHTML = '<span class="btn-loading">Soumission...</span>';
            submitBtn.disabled = true;
        });
    }
    
    // Add confetti effect for high scores (on result page)
    const resultScore = document.querySelector('.result-score');
    if (resultScore) {
        const score = parseInt(resultScore.textContent);
        if (score >= 80) {
            createConfetti();
        }
    }
});

// Confetti effect function
function createConfetti() {
    const colors = ['#8B5CF6', '#3B82F6', '#06B6D4', '#EC4899', '#10B981'];
    const confettiCount = 100;
    
    for (let i = 0; i < confettiCount; i++) {
        const confetti = document.createElement('div');
        confetti.style.cssText = `
            position: fixed;
            width: 10px;
            height: 10px;
            background: ${colors[Math.floor(Math.random() * colors.length)]};
            left: ${Math.random() * 100}vw;
            top: -10px;
            border-radius: ${Math.random() > 0.5 ? '50%' : '0'};
            animation: confettiFall ${2 + Math.random() * 3}s linear forwards;
            z-index: 9999;
        `;
        document.body.appendChild(confetti);
        
        setTimeout(() => confetti.remove(), 5000);
    }
    
    // Add confetti animation styles
    const style = document.createElement('style');
    style.textContent = `
        @keyframes confettiFall {
            to {
                transform: translateY(100vh) rotate(720deg);
                opacity: 0;
            }
        }
    `;
    document.head.appendChild(style);
}

console.log('Quiz interactions loaded 🧠');
