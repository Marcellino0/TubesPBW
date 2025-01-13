function resetSearchForm() {
    window.location.href = '/userdashboard';
}

document.addEventListener('DOMContentLoaded', function () {
    //dropdown buat genre
    new SlimSelect({
        select: '#genreSelect',
        placeholder: 'Select Genres',
        closeOnSelect: false
    });
    //dropdown buat aktor
    new SlimSelect({
        select: '#actorSelect',
        placeholder: 'Select Actors',
        closeOnSelect: false
    });
});

//untuk balikin card sinopsis
function flipCard(element) {
    const movieCard = element.closest('.movie-card'); //mencari elemen terdekat dengan class movie-card
    if (!movieCard) {//kalau movie card tidak ada
        const flipcard = element.closest('.flipcard');//mencari elemen terdekat dengan class flipcard
        if (flipcard) {//kalau field card ditemukan
            const movieCard = flipcard.closest('.movie-card');
            movieCard.classList.toggle('flipped');
        }
    } else {
        movieCard.classList.toggle('flipped');
    }
}
//update harga film dari durasi
function updatePrice(movieId) {
    const duration = document.getElementById(`duration-${movieId}`).value; //ambil durasi dari dropdown
    const priceElement = document.getElementById(`price-${movieId}`); //ambil harga

    const prices = { //harga per data (per hari nya) dari data price
        '7': parseFloat(priceElement.dataset.price7),
        '14': parseFloat(priceElement.dataset.price14),
        '30': parseFloat(priceElement.dataset.price30)
    };
    //harga satuan rupiah
    const formattedPrice = new Intl.NumberFormat('id-ID').format(prices[duration]);
    priceElement.textContent = formattedPrice; //nampilin harga
}

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