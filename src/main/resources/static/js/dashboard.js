// Variabel untuk slideshow main video
let slideIndex = 1;
// Variabel untuk slideshow main video 
let topMovieIndex = 1;
let latestMovieIndex = 1;
let autoAdvanceEnabled = true;

// Fungsi main video slideshow
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

    // Memastikan pemutaran video play/pause dengan benar
    const videos = document.querySelectorAll('.slide-video');
    videos.forEach(video => {
        video.pause();
    });
    videos[slideIndex-1].play();
}

// fungsi slideshow movie
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

    if (!slides.length) return; // jika elemen tidak ditemukan

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

    // menyembunyikan semua slide
    slides.forEach(slide => {
        slide.style.display = "none";
        slide.classList.remove("active");
    });

    // Mereset semua titik
    dots.forEach(dot => {
        dot.classList.remove("active");
    });

    // menampilkan slide aktif
    slides[slideIndex - 1].style.display = "grid";
    slides[slideIndex - 1].classList.add("active");
    if (dots.length > 0) {
        dots[slideIndex - 1].classList.add("active");
    }
}

// fungsi otomatis untuk slideshow movie
function startAutoAdvance() {
    if (!autoAdvanceEnabled) return;

    // otomatisasi untuk movie teratas
    setInterval(() => {
        if (autoAdvanceEnabled) {
            plusMovieSlides(1, 'top');
        }
    }, 4000);

    // otomatisasi untuk movie terbaru
    setInterval(() => {
        if (autoAdvanceEnabled) {
            plusMovieSlides(1, 'latest');
        }
    }, 4000);
}

// inisialisasi saat semua dokumen sudah siap
document.addEventListener('DOMContentLoaded', () => {
    // inisialisasi slideshow main video
    showSlides(slideIndex);
    
    // otomatisasi untuk slideshow
    setInterval(() => {
        plusSlides(1);
    }, 4000); //4 detik

    // inisialisasi slideshows movie
    showMovieSlides(topMovieIndex, 'top');
    showMovieSlides(latestMovieIndex, 'latest');

    // Start auto untuk slideshows movie
    startAutoAdvance();

    // Menambah fungsi hover pause untuk slideshows movie
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