
function switchForm(type) {
    const userForm = document.getElementById('userForm');
    const adminForm = document.getElementById('adminForm');
    const buttons = document.querySelectorAll('.type-btn');
    //kalau admin
    if (type === 'admin') {
        userForm.classList.remove('active'); //remove userform
        adminForm.classList.add('active');
        buttons[0].classList.remove('active');
        buttons[1].classList.add('active');
    } else { //kalau bukan admin
        adminForm.classList.remove('active'); //remove adminform
        userForm.classList.add('active');
        buttons[1].classList.remove('active');
        buttons[0].classList.add('active');
    }
}
