function resetSearchForm() {
    window.location.href = '/userdashboard';
}

// Initialize multiple select dropdowns
document.addEventListener('DOMContentLoaded', function () {
    new SlimSelect({
        select: '#genreSelect',
        placeholder: 'Select Genres',
        closeOnSelect: false
    });

    new SlimSelect({
        select: '#actorSelect',
        placeholder: 'Select Actors',
        closeOnSelect: false
    });
});


function flipCard(element) {
    const movieCard = element.closest('.movie-card');
    if (!movieCard) {
        // If clicking the back button
        const flipcard = element.closest('.flipcard');
        if (flipcard) {
            const movieCard = flipcard.closest('.movie-card');
            movieCard.classList.toggle('flipped');
        }
    } else {
        movieCard.classList.toggle('flipped');
    }
}

function updatePrice(movieId) {
    const duration = document.getElementById(`duration-${movieId}`).value;
    const priceElement = document.getElementById(`price-${movieId}`);

    const prices = {
        '7': parseFloat(priceElement.dataset.price7),
        '14': parseFloat(priceElement.dataset.price14),
        '30': parseFloat(priceElement.dataset.price30)
    };

    const formattedPrice = new Intl.NumberFormat('id-ID').format(prices[duration]);
    priceElement.textContent = formattedPrice;
}

// Existing navigation functions remain unchanged
function redirectToCatalog() {
    window.location.href = '/userdashboard';
}

function redirectToRentals() {
    window.location.href = '/rental';
}

function redirectToHistory() {
    window.location.href = '/history-rental';
}

function redirectToProfile() {
    window.location.href = '/profile';
}

function logout() {
    window.location.href = '/logout';
}