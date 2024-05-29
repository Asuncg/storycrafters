function calcularMedia() {
    var checkboxes = document.querySelectorAll('input[name="relatoIds"]:checked');
    var totalCalificaciones = 0;
    var numRelatosSeleccionados = checkboxes.length;

    if (numRelatosSeleccionados > 0) {
        checkboxes.forEach(function(checkbox) {
            totalCalificaciones += parseFloat(checkbox.parentNode.parentNode.querySelector('.col-sm-2').innerText);
        });

        var media = totalCalificaciones / numRelatosSeleccionados;

        var mediaCalificacionElement = document.getElementById('media-calificacion');
        mediaCalificacionElement.innerText = media.toFixed(2);
    } else {
        var mediaCalificacionElement = document.getElementById('media-calificacion');
        mediaCalificacionElement.innerText = "0.00";
    }
}

function toggleSeleccionarTodosLosRelatos() {
    var checkboxes = document.querySelectorAll('input[name="relatoIds"]');
    var seleccionarTodos = document.getElementById('checkboxSeleccionarTodosLosRelatos');
    checkboxes.forEach(function(checkbox) {
        checkbox.checked = seleccionarTodos.checked;
    });
}
