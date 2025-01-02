
function switchForm(type) {
    const userForm = document.getElementById('userForm');
    const adminForm = document.getElementById('adminForm');
    const buttons = document.querySelectorAll('.type-btn');

    if (type === 'admin') {
        userForm.classList.remove('active');
        adminForm.classList.add('active');
        buttons[0].classList.remove('active');
        buttons[1].classList.add('active');
    } else {
        adminForm.classList.remove('active');
        userForm.classList.add('active');
        buttons[1].classList.remove('active');
        buttons[0].classList.add('active');
    }
}
