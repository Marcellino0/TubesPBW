// Variabel untuk slideshow video utama
let slideIndex = 1;

// Variabel untuk slideshow film
let topMovieIndex = 1;
let latestMovieIndex = 1;
let autoAdvanceEnabled = true;

// Fungsi slideshow video utama
function plusSlides(n) {
    showSlides(slideIndex += n);
}

function currentSlide(n) {
    showSlides(slideIndex = n);
}

function showSlides(n) {
    const slides = document.getElementsByClassName("slides");
    const dots = document.getElementsByClassName("dot");

    if (n > slides.length) { slideIndex = 1 }
    if (n < 1) { slideIndex = slides.length }

    Array.from(slides).forEach(slide => {
        slide.style.display = "none";
    });

    Array.from(dots).forEach(dot => {
        dot.classList.remove("active");
    });

    slides[slideIndex - 1].style.display = "block";
    dots[slideIndex - 1].classList.add("active");

    const videos = document.querySelectorAll('.slide-video');
    videos.forEach(video => {
        video.pause();
    });
    videos[slideIndex - 1].play();
}

// Fungsi slideshow film
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

    if (!slides.length) return;

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

    slides.forEach(slide => {
        slide.style.display = "none";
        slide.classList.remove("active");
    });
    dots.forEach(dot => {
        dot.classList.remove("active");
    });

    // Tampilkan slide aktif
    slides[slideIndex - 1].style.display = "grid";
    slides[slideIndex - 1].classList.add("active");
    if (dots.length > 0) {
        dots[slideIndex - 1].classList.add("active");
    }
}

// Fungsi perpindahan otomatis untuk slideshow film
function startAutoAdvance() {
    if (!autoAdvanceEnabled) return;

    // Perpindahan otomatis untuk Film Teratas
    setInterval(() => {
        if (autoAdvanceEnabled) {
            plusMovieSlides(1, 'top');
        }
    }, 2000);

    // Perpindahan otomatis untuk Film Terbaru
    setInterval(() => {
        if (autoAdvanceEnabled) {
            plusMovieSlides(1, 'latest');
        }
    }, 3000);
}

// Inisialisasi semua komponen ketika dokumen siap
document.addEventListener('DOMContentLoaded', () => {
    // Inisialisasi slideshow video utama
    showSlides(slideIndex);

    // Perpindahan otomatis slideshow utama
    setInterval(() => {
        plusSlides(1);
    }, 4000);

    // Inisialisasi slideshow film
    showMovieSlides(topMovieIndex, 'top');
    showMovieSlides(latestMovieIndex, 'latest');
    startAutoAdvance();

    // Tambahkan fungsi jeda saat hover untuk slideshow film
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