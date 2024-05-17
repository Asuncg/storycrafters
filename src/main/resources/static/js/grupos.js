var groupId;
document.addEventListener("DOMContentLoaded", function () {
    var abandonarGrupoBtns = document.querySelectorAll('.abandonar-grupo-btn');
    abandonarGrupoBtns.forEach(function (btn) {
        btn.addEventListener('click', function () {
            var grupoId = this.getAttribute('data-group-id');
            document.getElementById('grupoIdInput').value = grupoId;
        });
    });
});

function openInvitationModal(element) {
    groupId = element.getAttribute('data-group-id');

    $('#invitationEmail').val('');
    $('#invitationMessage').hide();

    $('#invitationModal').modal('show');


}

$(document).ready(function () {
    $('#sendInvitationBtn').click(function () {
        console.log("Botón 'Enviar Invitación' clickeado");
        sendInvitations();
    });

});

function sendInvitations() {
    var email = document.getElementById('invitationEmail').value.trim();

    if (email === '') {
        document.getElementById('invitationMessage').innerText = 'Por favor, ingresa un correo electrónico.';
        document.getElementById('invitationMessage').classList.remove('text-success');
        document.getElementById('invitationMessage').classList.add('text-danger');
        document.getElementById('invitationMessage').style.display = 'block';
        return;
    }

    var data = {
        groupId: groupId,
        emails: [email]
    };

    var url = '/grupos/invitar-usuarios';

    var xhr = new XMLHttpRequest();
    xhr.open('POST', url, true);
    xhr.setRequestHeader('Content-Type', 'application/json');
    xhr.onload = function () {
        if (xhr.status === 200) {
            document.getElementById('invitationMessage').innerText = 'Invitación enviada con éxito.';
            document.getElementById('invitationMessage').classList.remove('text-danger');
            document.getElementById('invitationMessage').classList.add('text-success');
            document.getElementById('invitationMessage').style.display = 'block';
        } else {
            document.getElementById('invitationMessage').innerText = 'Error al enviar la invitación.';
            document.getElementById('invitationMessage').classList.remove('text-success');
            document.getElementById('invitationMessage').classList.add('text-danger');
            document.getElementById('invitationMessage').style.display = 'block';
        }
    };

    xhr.send(JSON.stringify(data));
}

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

//CALCULAR LA NOTA MEDIA DE LOS RELATOS DEL USUARIO
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
