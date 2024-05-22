document.addEventListener("DOMContentLoaded", function() {
    var passwordInput = document.getElementById("password");
    var togglePasswordButton = document.getElementById("togglePassword");

    // Agregar evento de clic al botón de alternar contraseña
    togglePasswordButton.addEventListener("click", function() {
        togglePassword(passwordInput);
    });
});

function togglePassword(passwordInput) {
    var fieldType = passwordInput.getAttribute('type');
    if (fieldType === 'password') {
        passwordInput.setAttribute('type', 'text');
    } else {
        passwordInput.setAttribute('type', 'password');
    }
}