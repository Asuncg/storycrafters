function setAprobado(aprobado) {
    document.getElementById('aprobado').value = aprobado;
}

function toggleSeleccionarTodos() {
    var checkboxes = document.querySelectorAll('input[name="solicitudIds"]');
    var seleccionarTodos = document.getElementById('checkboxSeleccionarTodos');
    checkboxes.forEach(function(checkbox) {
        checkbox.checked = seleccionarTodos.checked;
    });
}

function confirmarEliminacionUsuario(link) {
    var grupoId = link.getAttribute('data-grupoId');
    var usuarioId = link.getAttribute('data-usuarioId');

    document.getElementById('confirmDeleteModalBody').textContent = '¿Estás seguro de que deseas eliminar a este usuario del grupo? Todo el contenido publicado de este usuario no se perderá y seguirá visible para el resto del grupo.';
    var modal = new bootstrap.Modal(document.getElementById('confirmDeleteModal'));
    modal.show();

    var confirmButton = document.getElementById('confirmButton');
    confirmButton.onclick = function () {
        document.getElementById('deleteUserFormGrupoId').value = grupoId;
        document.getElementById('deleteUserFormUsuarioId').value = usuarioId;

        document.getElementById('deleteUserForm').submit();
        modal.hide();
    };

    var cancelButton = document.getElementById('cancelButton');
    cancelButton.onclick = function () {
        modal.hide();
    };
    window.onclick = function (event) {
        if (event.target === modal) {
            modal.hide();
        }
    };
}

document.addEventListener('DOMContentLoaded', function() {
    document.getElementById('copy-icon').addEventListener('click', function() {
        var codigoAcceso = document.getElementById('codigo-acceso').innerText;

        var tempInput = document.createElement('input');
        tempInput.value = codigoAcceso;
        document.body.appendChild(tempInput);

        tempInput.select();
        document.execCommand('copy');

        document.body.removeChild(tempInput);
    });
});

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