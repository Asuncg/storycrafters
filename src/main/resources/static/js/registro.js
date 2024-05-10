document.addEventListener("DOMContentLoaded", function() {
    document.getElementById("password").addEventListener("input", validatePassword);
    document.getElementById("confirmPassword").addEventListener("input", validatePassword);
});

function validatePassword() {
    var password = document.getElementById("password").value;
    var confirmPassword = document.getElementById("confirmPassword").value;
    var passwordMatchError = document.getElementById("passwordMatchError");

    if (password !== confirmPassword) {
        passwordMatchError.textContent = "Las contraseñas no coinciden";
    } else {
        passwordMatchError.textContent = "";
    }
}

document.querySelector("form").addEventListener("submit", function(event) {
    var password = document.getElementById("password").value;
    var confirmPassword = document.getElementById("confirmPassword").value;

    if (password !== confirmPassword) {
        event.preventDefault(); // Evita que se envíe el formulario
        document.getElementById("passwordMatchError").textContent = "Las contraseñas no coinciden";
    }
});

function togglePassword(fieldId) {
    var field = document.getElementById(fieldId);
    var fieldType = field.getAttribute('type');
    if (fieldType === 'password') {
        field.setAttribute('type', 'text');
    } else {
        field.setAttribute('type', 'password');
    }
}
