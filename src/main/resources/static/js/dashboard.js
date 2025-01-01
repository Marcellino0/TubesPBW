// Variables for main video slideshow
let slideIndex = 1;

// Variables for movie slideshows
let topMovieIndex = 1;
let latestMovieIndex = 1;
let autoAdvanceEnabled = true;

// Main video slideshow functions
function plusSlides(n) {
    showSlides(slideIndex += n);
}

function currentSlide(n) {
    showSlides(slideIndex = n);
}

function showSlides(n) {
    const slides = document.getElementsByClassName("slides");
    const dots = document.getElementsByClassName("dot");
    
    if (n > slides.length) {slideIndex = 1}    
    if (n < 1) {slideIndex = slides.length}
    
    Array.from(slides).forEach(slide => {
        slide.style.display = "none";
    });
    
    Array.from(dots).forEach(dot => {
        dot.classList.remove("active");
    });
    
    slides[slideIndex-1].style.display = "block";
    dots[slideIndex-1].classList.add("active");

    // Ensure videos play/pause correctly
    const videos = document.querySelectorAll('.slide-video');
    videos.forEach(video => {
        video.pause();
    });
    videos[slideIndex-1].play();
}

// Movie slideshow functions
function plusMovieSlides(n, section) {
    if (section === 'top') {
        showMovieSlides(topMovieIndex += n, 'top');
    } else {
        showMovieSlides(latestMovieIndex += n, 'latest');
    }
}

function currentMovieSlide(n, section) {
    if (section === 'top') {
        showMovieSlides(topMovieIndex = n, 'top');
    } else {
        showMovieSlides(latestMovieIndex = n, 'latest');
    }
}

function showMovieSlides(n, section) {
    const slides = document.querySelectorAll(`#${section}MoviesSlideshow .movie-slide`);
    const dots = document.querySelectorAll(`#${section}MoviesSlideshow .movie-dot`);

    if (!slides.length) return; // Guard clause if elements not found

    let slideIndex = section === 'top' ? topMovieIndex : latestMovieIndex;

    if (n > slides.length) {
        slideIndex = 1;
        if (section === 'top') topMovieIndex = 1;
        else latestMovieIndex = 1;
    }
    if (n < 1) {
        slideIndex = slides.length;
        if (section === 'top') topMovieIndex = slides.length;
        else latestMovieIndex = slides.length;
    }

    // Hide all slides
    slides.forEach(slide => {
        slide.style.display = "none";
        slide.classList.remove("active");
    });

    // Reset all dots
    dots.forEach(dot => {
        dot.classList.remove("active");
    });

    // Show active slide
    slides[slideIndex - 1].style.display = "grid";
    slides[slideIndex - 1].classList.add("active");
    if (dots.length > 0) {
        dots[slideIndex - 1].classList.add("active");
    }
}

// Auto advance function for movie slideshows
function startAutoAdvance() {
    if (!autoAdvanceEnabled) return;

    // Auto advance for Top Movies
    setInterval(() => {
        if (autoAdvanceEnabled) {
            plusMovieSlides(1, 'top');
        }
    }, 5000);

    // Auto advance for Latest Movies
    setInterval(() => {
        if (autoAdvanceEnabled) {
            plusMovieSlides(1, 'latest');
        }
    }, 5000);
}

// Initialize everything when document is ready
document.addEventListener('DOMContentLoaded', () => {
    // Initialize main video slideshow
    showSlides(slideIndex);
    
    // Auto advance main slideshow
    setInterval(() => {
        plusSlides(1);
    }, 5000);

    // Initialize movie slideshows
    showMovieSlides(topMovieIndex, 'top');
    showMovieSlides(latestMovieIndex, 'latest');

    // Start auto advance for movie slideshows
    startAutoAdvance();

    // Add hover pause functionality for movie slideshows
    const slideshows = document.querySelectorAll('.movie-slideshow');
    slideshows.forEach(slideshow => {
        slideshow.addEventListener('mouseenter', () => {
            autoAdvanceEnabled = false;
        });

        slideshow.addEventListener('mouseleave', () => {
            autoAdvanceEnabled = true;
        });
    });
});