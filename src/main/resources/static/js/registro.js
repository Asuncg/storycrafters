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


function togglePassword(fieldId) {
    var field = document.getElementById(fieldId);
    var fieldType = field.getAttribute('type');
    if (fieldType === 'password') {
        field.setAttribute('type', 'text');
    } else {
        field.setAttribute('type', 'password');
    }
}

function validarCoincidenciaContrasena() {
    var password = document.getElementById("password").value;
    var confirmPassword = document.getElementById("confirmPassword");
    var passwordMatchError = document.getElementById("passwordMatchError");

    if (password !== confirmPassword.value) {
        confirmPassword.classList.add("is-invalid");
        passwordMatchError.innerText = "Las contraseñas no coinciden.";
        $('#passwordMismatchModal').modal('show'); // Muestra el modal
    } else {
        confirmPassword.classList.remove("is-invalid");
        passwordMatchError.innerText = "";
    }
}

document.addEventListener("DOMContentLoaded", function() {
    // Obtener los elementos de los campos y el botón de registro
    var firstNameInput = document.getElementById("firstName");
    var lastNameInput = document.getElementById("lastName");
    var emailInput = document.getElementById("email");
    var passwordInput = document.getElementById("password");
    var confirmPasswordInput = document.getElementById("confirmPassword");
    var registrationButton = document.querySelector("button[type='submit']");

    // Agregar un evento de escucha a los campos para verificar cambios
    [firstNameInput, lastNameInput, emailInput, passwordInput, confirmPasswordInput].forEach(function(input) {
        input.addEventListener("input", validarCampos);
    });

    // Función para validar los campos y habilitar o deshabilitar el botón de registro
    function validarCampos() {
        var firstName = firstNameInput.value.trim();
        var lastName = lastNameInput.value.trim();
        var email = emailInput.value.trim();
        var password = passwordInput.value;
        var confirmPassword = confirmPasswordInput.value;

        // Validar la longitud de la contraseña
        var longitudMinima = password.length >= 8;

        // Mostrar mensaje de error si la longitud de la contraseña es insuficiente
        var passwordLengthError = document.getElementById("passwordLengthError");
        if (!longitudMinima) {
            passwordLengthError.textContent = "La contraseña debe tener al menos 8 caracteres.";
            passwordInput.classList.add("is-invalid");
        } else {
            passwordLengthError.textContent = "";
            passwordInput.classList.remove("is-invalid");
        }

        // Verificar si los campos obligatorios están completos y si las contraseñas coinciden
        var camposCompletos = firstName && email && password && confirmPassword;
        var contrasenasCoinciden = password === confirmPassword;

        // Habilitar o deshabilitar el botón de registro en consecuencia
        if (camposCompletos && contrasenasCoinciden && longitudMinima) {
            registrationButton.removeAttribute("disabled");
        } else {
            registrationButton.setAttribute("disabled", "disabled");
        }
    }
});



