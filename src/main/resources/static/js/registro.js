document.getElementById("formRegistro").onsubmit = function(event) {
    var password = document.getElementById("password").value;
    var confirmPassword = document.getElementById("confirmPassword").value;

    if (password !== confirmPassword) {
        alert("Las contraseñas no coinciden.");
        event.preventDefault(); // Prevenir que el formulario se envíe
    }
}