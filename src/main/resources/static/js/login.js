// Fungsi untuk mengganti tampilan antara form login user dan admin
function switchForm(type) {
    // Mengambil referensi ke element form
    const userForm = document.getElementById('userForm');
    const adminForm = document.getElementById('adminForm');
    const buttons = document.querySelectorAll('.type-btn');

    // Jika yang dipilih adalah form admin
    if (type === 'admin') {
        // Sembunyikan form user
        userForm.classList.remove('active');
        // Tampilkan form admin
        adminForm.classList.add('active');
        // Ubah status tombol: nonaktifkan tombol user, aktifkan tombol admin
        buttons[0].classList.remove('active');
        buttons[1].classList.add('active');
    } 
    // Jika yang dipilih adalah form user
    else {
        // Sembunyikan form admin
        adminForm.classList.remove('active');
        // Tampilkan form user
        userForm.classList.add('active');
        // Ubah status tombol: nonaktifkan tombol admin, aktifkan tombol user
        buttons[1].classList.remove('active');
        buttons[0].classList.add('active');
    }
}