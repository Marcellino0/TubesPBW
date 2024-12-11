let slideIndex = 1;
showSlides(slideIndex);

function plusSlides(n) {
    showSlides(slideIndex += n);
}

function currentSlide(n) {
    showSlides(slideIndex = n);
}

function showSlides(n) {
    let sections = document.querySelectorAll('.section');
    let dots = document.querySelectorAll('.dot');
    
    // Reset slide index if out of bounds
    if (n > sections.length) {slideIndex = 1}
    if (n < 1) {slideIndex = sections.length}

    // Hide all sections
    sections.forEach(section => {
        section.classList.remove('active');
    });

    // Deactivate all dots
    dots.forEach(dot => {
        dot.classList.remove('active');
    });

    // Show current section
    sections[slideIndex - 1].classList.add('active');
    
    // Activate current dot
    dots[slideIndex - 1].classList.add('active');
}