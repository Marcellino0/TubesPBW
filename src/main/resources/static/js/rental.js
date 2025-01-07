// Fungsi redirect untuk admin panel
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

// Mendapatkan path sekarang
let currentPath = window.location.pathname;

// Menghapus class aktif dari semua menu item
document.querySelectorAll('.sidebar-menu li').forEach(item => {
    item.classList.remove('active');
});

// Menambah class aktif berdasarkan path yang sekarang
if (currentPath === '/rental') {
    document.querySelector('.sidebar-menu li[onclick="redirectToRentals()"]').classList.add('active');
} else if (currentPath === '/userdashboard') {
    document.querySelector('.sidebar-menu li[onclick="redirectToCatalog()"]').classList.add('active');
} else if (currentPath === '/history-rental') {
    document.querySelector('.sidebar-menu li[onclick="redirectToHistory()"]').classList.add('active');
} else if (currentPath === '/profile') {
    document.querySelector('.sidebar-menu li[onclick="redirectToProfile()"]').classList.add('active');
}