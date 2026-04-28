// Modern Interactions for E-Learning Platform

// Course card hover effects
document.addEventListener('DOMContentLoaded', function() {
    // Animate course cards on scroll
    const observerOptions = {
        threshold: 0.1,
        rootMargin: '0px 0px -50px 0px'
    };

    const observer = new IntersectionObserver(function(entries) {
        entries.forEach(entry => {
            if (entry.isIntersecting) {
                entry.target.style.animation = 'fadeInUp 0.6s ease forwards';
                observer.unobserve(entry.target);
            }
        });
    }, observerOptions);

    // Observe course cards
    document.querySelectorAll('.course-card').forEach(card => {
        card.style.opacity = '0';
        observer.observe(card);
    });

    // Category pill interactions
    const categoryPills = document.querySelectorAll('.category-item');
    categoryPills.forEach(pill => {
        pill.addEventListener('click', function(e) {
            e.preventDefault();
            
            // Remove active class from all pills
            categoryPills.forEach(p => p.classList.remove('active'));
            
            // Add active class to clicked pill
            this.classList.add('active');
            
            // Add ripple effect
            const ripple = document.createElement('span');
            const rect = this.getBoundingClientRect();
            const size = Math.max(rect.width, rect.height);
            const x = e.clientX - rect.left - size / 2;
            const y = e.clientY - rect.top - size / 2;
            
            ripple.style.cssText = `
                position: absolute;
                width: ${size}px;
                height: ${size}px;
                left: ${x}px;
                top: ${y}px;
                background: rgba(255, 255, 255, 0.3);
                border-radius: 50%;
                transform: scale(0);
                animation: ripple 0.6s ease-out;
                pointer-events: none;
            `;
            
            this.style.position = 'relative';
            this.style.overflow = 'hidden';
            this.appendChild(ripple);
            
            setTimeout(() => ripple.remove(), 600);
        });
    });

    // Search functionality
    const searchInputs = document.querySelectorAll('input[type="search"], input[placeholder*="search"]');
    searchInputs.forEach(input => {
        let searchTimeout;
        
        input.addEventListener('input', function() {
            clearTimeout(searchTimeout);
            const query = this.value.trim();
            
            if (query.length > 2) {
                searchTimeout = setTimeout(() => {
                    performSearch(query);
                }, 300);
            }
        });
    });

    // Rating interactions
    const starRatings = document.querySelectorAll('.star-rating');
    starRatings.forEach(rating => {
        const stars = rating.querySelectorAll('.star');
        let currentRating = 0;

        stars.forEach((star, index) => {
            star.addEventListener('mouseenter', function() {
                highlightStars(index + 1);
            });

            star.addEventListener('click', function() {
                currentRating = index + 1;
                setRating(currentRating);
            });
        });

        rating.addEventListener('mouseleave', function() {
            highlightStars(currentRating);
        });

        function highlightStars(rating) {
            stars.forEach((star, index) => {
                if (index < rating) {
                    star.classList.add('filled');
                } else {
                    star.classList.remove('filled');
                }
            });
        }

        function setRating(rating) {
            highlightStars(rating);
            // Here you could send the rating to the server
            console.log('Rating set to:', rating);
        }
    });

    // Smooth scroll for anchor links
    document.querySelectorAll('a[href^="#"]').forEach(anchor => {
        anchor.addEventListener('click', function(e) {
            e.preventDefault();
            const target = document.querySelector(this.getAttribute('href'));
            if (target) {
                target.scrollIntoView({
                    behavior: 'smooth',
                    block: 'start'
                });
            }
        });
    });

    // Lazy loading for images
    const lazyImages = document.querySelectorAll('img[data-src]');
    const imageObserver = new IntersectionObserver(function(entries) {
        entries.forEach(entry => {
            if (entry.isIntersecting) {
                const img = entry.target;
                img.src = img.dataset.src;
                img.classList.remove('lazy');
                imageObserver.unobserve(img);
            }
        });
    });

    lazyImages.forEach(img => {
        img.classList.add('lazy');
        imageObserver.observe(img);
    });

    // Toast notifications
    window.showToast = function(message, type = 'info') {
        const toast = document.createElement('div');
        toast.className = `toast toast-${type}`;
        toast.textContent = message;
        
        Object.assign(toast.style, {
            position: 'fixed',
            bottom: '20px',
            right: '20px',
            padding: '12px 20px',
            borderRadius: '8px',
            color: 'white',
            fontWeight: '500',
            zIndex: '10000',
            transform: 'translateX(100%)',
            transition: 'transform 0.3s ease'
        });

        // Set background color based on type
        switch(type) {
            case 'success':
                toast.style.background = '#10b981';
                break;
            case 'error':
                toast.style.background = '#ef4444';
                break;
            case 'warning':
                toast.style.background = '#f59e0b';
                break;
            default:
                toast.style.background = '#3b82f6';
        }

        document.body.appendChild(toast);
        
        // Animate in
        setTimeout(() => {
            toast.style.transform = 'translateX(0)';
        }, 100);

        // Remove after 3 seconds
        setTimeout(() => {
            toast.style.transform = 'translateX(100%)';
            setTimeout(() => toast.remove(), 300);
        }, 3000);
    };

    // Form validation enhancements
    const forms = document.querySelectorAll('form');
    forms.forEach(form => {
        const inputs = form.querySelectorAll('input, textarea, select');
        
        inputs.forEach(input => {
            input.addEventListener('blur', function() {
                validateField(this);
            });

            input.addEventListener('input', function() {
                if (this.classList.contains('error')) {
                    validateField(this);
                }
            });
        });

        form.addEventListener('submit', function(e) {
            let isValid = true;
            inputs.forEach(input => {
                if (!validateField(input)) {
                    isValid = false;
                }
            });

            if (!isValid) {
                e.preventDefault();
                showToast('Please correct the errors in the form', 'error');
            }
        });
    });

    function validateField(field) {
        const value = field.value.trim();
        let isValid = true;
        let errorMessage = '';

        // Remove existing error
        const existingError = field.parentNode.querySelector('.error-message');
        if (existingError) {
            existingError.remove();
        }
        field.classList.remove('error');

        // Validation rules
        if (field.hasAttribute('required') && !value) {
            isValid = false;
            errorMessage = 'This field is required';
        } else if (field.type === 'email' && value && !isValidEmail(value)) {
            isValid = false;
            errorMessage = 'Please enter a valid email address';
        } else if (field.type === 'password' && value && value.length < 8) {
            isValid = false;
            errorMessage = 'Password must be at least 8 characters long';
        }

        if (!isValid) {
            field.classList.add('error');
            const errorElement = document.createElement('div');
            errorElement.className = 'error-message';
            errorElement.textContent = errorMessage;
            errorElement.style.cssText = `
                color: #ef4444;
                font-size: 0.875rem;
                margin-top: 4px;
            `;
            field.parentNode.appendChild(errorElement);
        }

        return isValid;
    }

    function isValidEmail(email) {
        return /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email);
    }

    // Loading states for buttons
    const buttons = document.querySelectorAll('button[type="submit"]');
    buttons.forEach(button => {
        const form = button.closest('form');
        if (form) {
            form.addEventListener('submit', function() {
                button.disabled = true;
                button.innerHTML = '<span class="spinner"></span> Loading...';
                
                // Reset after 5 seconds (in case of network issues)
                setTimeout(() => {
                    button.disabled = false;
                    button.innerHTML = button.dataset.originalText || 'Submit';
                }, 5000);
            });
            
            // Store original text
            button.dataset.originalText = button.innerHTML;
        }
    });

    // Add CSS for spinner
    const style = document.createElement('style');
    style.textContent = `
        .spinner {
            display: inline-block;
            width: 16px;
            height: 16px;
            border: 2px solid rgba(255, 255, 255, 0.3);
            border-radius: 50%;
            border-top-color: white;
            animation: spin 0.8s linear infinite;
        }
        
        @keyframes spin {
            to { transform: rotate(360deg); }
        }
        
        @keyframes ripple {
            to {
                transform: scale(4);
                opacity: 0;
            }
        }
        
        .error {
            border-color: #ef4444 !important;
        }
        
        .error-message {
            color: #ef4444;
            font-size: 0.875rem;
            margin-top: 4px;
        }
    `;
    document.head.appendChild(style);
});

// Search functionality (placeholder)
function performSearch(query) {
    console.log('Searching for:', query);
    // Implement actual search logic here
    // This could make an API call to your backend
}

// Utility functions
function debounce(func, wait) {
    let timeout;
    return function executedFunction(...args) {
        const later = () => {
            clearTimeout(timeout);
            func(...args);
        };
        clearTimeout(timeout);
        timeout = setTimeout(later, wait);
    };
}

function throttle(func, limit) {
    let inThrottle;
    return function() {
        const args = arguments;
        const context = this;
        if (!inThrottle) {
            func.apply(context, args);
            inThrottle = true;
            setTimeout(() => inThrottle = false, limit);
        }
    };
}

// Export functions for global use
window.ModernInteractions = {
    showToast,
    performSearch,
    debounce,
    throttle
};
