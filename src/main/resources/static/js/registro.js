function togglePassword(id) {
    const passwordField = document.getElementById(id);
    const toggleIcon = document.querySelector(`#toggle${id.charAt(0).toUpperCase() + id.slice(1)}`);
    if (passwordField.type === "password") {
        passwordField.type = "text";
        toggleIcon.classList.remove("bi-eye");
        toggleIcon.classList.add("bi-eye-slash");
    } else {
        passwordField.type = "password";
        toggleIcon.classList.remove("bi-eye-slash");
        toggleIcon.classList.add("bi-eye");
    }
}

document.addEventListener("DOMContentLoaded", function () {
    var firstNameInput = document.getElementById("firstName");
    var lastNameInput = document.getElementById("lastName");
    var emailInput = document.getElementById("email");
    var passwordInput = document.getElementById("password");
    var confirmPasswordInput = document.getElementById("confirmPassword");
    var registrationButton = document.querySelector("button[type='submit']");

    [firstNameInput, lastNameInput, emailInput, passwordInput, confirmPasswordInput].forEach(function (input) {
        input.addEventListener("input", validarCampos);
        input.addEventListener("blur", validarCampos);
    });

    function validarCampos() {
        var firstName = firstNameInput.value.trim();
        var lastName = lastNameInput.value.trim();
        var email = emailInput.value.trim();
        var password = passwordInput.value;
        var confirmPassword = confirmPasswordInput.value;

        var longitudMinima = password.length >= 8;

        var emailValido = /^[\w-]+(\.[\w-]+)*@([\w-]+\.)+[a-zA-Z]{2,7}$/.test(email);


        var emailError = document.getElementById("emailError");
        if (emailError) {
            if (!emailValido && email !== "") {
                emailError.textContent = "Formato de correo electrónico inválido.";
                emailInput.classList.add("is-invalid");
            } else {
                emailError.textContent = "";
                emailInput.classList.remove("is-invalid");
            }
        }

        var passwordLengthError = document.getElementById("passwordLengthError");
        if (passwordLengthError) {
            if (!longitudMinima && password !== "") {
                passwordLengthError.textContent = "La contraseña debe tener al menos 8 caracteres.";
                passwordInput.classList.add("is-invalid");
            } else {
                passwordLengthError.textContent = "";
                passwordInput.classList.remove("is-invalid");
            }
        }

        var passwordMatchError = document.getElementById("passwordMatchError");
        if (passwordMatchError) {
            if (password !== confirmPassword && confirmPassword !== "") { // Validar solo si el campo no está vacío
                passwordMatchError.textContent = "Las contraseñas no coinciden.";
                confirmPasswordInput.classList.add("is-invalid");
            } else {
                passwordMatchError.textContent = "";
                confirmPasswordInput.classList.remove("is-invalid");
            }
        }

        var camposCompletos = firstName && email && password && confirmPassword;
        var contrasenasCoinciden = password === confirmPassword;

        if (camposCompletos && contrasenasCoinciden && longitudMinima && emailValido) {
            registrationButton.removeAttribute("disabled");
        } else {
            registrationButton.setAttribute("disabled", "disabled");
        }
    }

    document.getElementById("togglePassword").addEventListener("click", function() {
        togglePassword('password');
    });
    document.getElementById("toggleConfirmPassword").addEventListener("click", function() {
        togglePassword('confirmPassword');
    });

    const successAlert = document.querySelector(".alert.alert-info");
    if (successAlert && successAlert.textContent.includes("Te has registrado correctamente")) {
        setTimeout(function() {
            window.location.href = "/login";
        }, 3000);
    }
});
