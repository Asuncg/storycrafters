document.addEventListener("DOMContentLoaded", function() {
    var calificacionInput = document.getElementById("calificacion");
    var aprobarBtn = document.getElementById("aprobarBtn");
    var rechazarBtn = document.getElementById("rechazarBtn");

    calificacionInput.addEventListener("input", function() {
        var value = parseFloat(calificacionInput.value);
        if (value >= 0 && value <= 10) {
            aprobarBtn.disabled = false;
            rechazarBtn.disabled = false;
            calificacionInput.setCustomValidity("");
        } else {
            aprobarBtn.disabled = true;
            rechazarBtn.disabled = true;
            calificacionInput.setCustomValidity("La calificación debe estar entre 0 y 10.");
        }
    });
});