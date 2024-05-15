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

function mostrarPestana(idPestana) {
    // Ocultar todas las pestañas
    var pestanas = document.querySelectorAll('.pestana');
    pestanas.forEach(function (elemento) {
        elemento.style.display = 'none';
    });

    document.getElementById(idPestana).style.display = 'block';

    if (idPestana === 'aRevisar') {
        var relatosPendientes = document.querySelectorAll('.list-group-item');

        relatosPendientes.forEach(function (elemento) {
            if (elemento.querySelector('.col-sm-4').innerText !== 'Pendiente') {
                elemento.style.display = 'none';
            } else {
                elemento.style.display = 'flex';
            }
        });
    }
}

function toggleSeleccionarTodos() {
    var checkboxes = document.querySelectorAll('input[name="solicitud"]');
    var checkboxSeleccionarTodos = document.getElementById('checkboxSeleccionarTodos');
    checkboxes.forEach(function (checkbox) {
        checkbox.checked = checkboxSeleccionarTodos.checked;
    });
}

function mostrarModalMensaje(mensaje) {
    document.getElementById('messageModalBody').textContent = mensaje;
    var modal = new bootstrap.Modal(document.getElementById('messageModal'));
    modal.show();
}

function aceptarSeleccionadas() {
    var solicitudesSeleccionadas = obtenerSolicitudesSeleccionadas();
    var grupoId = document.getElementById('grupoId').value;
    if (solicitudesSeleccionadas.length > 0) {
        enviarSolicitudesSeleccionadas(solicitudesSeleccionadas, 'aceptar', grupoId);
    } else {
        mostrarModalMensaje('No se han seleccionado solicitudes para aceptar.');
    }
}

function rechazarSeleccionadas() {
    var solicitudesSeleccionadas = obtenerSolicitudesSeleccionadas();
    var grupoId = document.getElementById('grupoId').value;
    if (solicitudesSeleccionadas.length > 0) {
        enviarSolicitudesSeleccionadas(solicitudesSeleccionadas, 'rechazar', grupoId);
    } else {
        mostrarModalMensaje('No se han seleccionado solicitudes para rechazar.');
    }
}

function obtenerSolicitudesSeleccionadas() {
    var checkboxes = document.querySelectorAll('input[name="solicitud"]:checked');
    var solicitudesSeleccionadas = [];
    checkboxes.forEach(function (checkbox) {
        solicitudesSeleccionadas.push(checkbox.value);
    });
    return solicitudesSeleccionadas;
}

function enviarSolicitudesSeleccionadas(solicitudesSeleccionadas, accion, grupoId) {
    var data = {
        accion: accion,
        grupoId: grupoId,
        solicitudIds: solicitudesSeleccionadas
    };

    var xhr = new XMLHttpRequest();
    xhr.open('POST', '/grupos/gestionar-solicitudes', true);
    xhr.setRequestHeader('Content-Type', 'application/json');
    xhr.onload = function () {
        if (xhr.status === 200) {

            mostrarModalMensaje(xhr.responseText);

            solicitudesSeleccionadas.forEach(function (solicitudId) {
                var solicitudElement = document.querySelector('input[name="solicitud"][value="' + solicitudId + '"]').closest('.list-group-item');
                solicitudElement.remove();
            });
        } else {
            mostrarModalMensaje(xhr.responseText);
        }
    };
    xhr.send(JSON.stringify(data));
}

//SACAR USUARIO DE UN GRUPO
function confirmarEliminacionUsuario(link) {
    var grupoId = link.getAttribute('data-grupoId');
    var usuarioId = link.getAttribute('data-usuarioId');

    var mensaje = '¿Estás seguro de que deseas eliminar a este usuario del grupo? Todo el contenido publicado de este usuario no se perderá y seguirá visible para el resto del grupo.';

    // Mostrar el modal
    document.getElementById('confirmDeleteModalBody').textContent = mensaje;
    var modal = new bootstrap.Modal(document.getElementById('confirmDeleteModal'));
    modal.show();

    // Al hacer clic en el botón de confirmar
    var confirmButton = document.getElementById('confirmButton');
    confirmButton.onclick = function () {
        eliminarUsuarioDelGrupo(grupoId, usuarioId);
        modal.hide();
    };

    // Al hacer clic en el botón de cancelar o fuera del modal, cerrar el modal
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

function eliminarUsuarioDelGrupo(grupoId, usuarioId) {
    var data = {
        grupoId: grupoId,
        usuarioId: usuarioId
    };

    var xhr = new XMLHttpRequest();
    xhr.open('POST', '/grupos/eliminar-usuario', true);
    xhr.setRequestHeader('Content-Type', 'application/json');
    xhr.onload = function () {
        if (xhr.status === 200) {
            mostrarModalMensaje('Usuario eliminado del grupo exitosamente.');
            var usuarioElementId = 'usuario_' + usuarioId; // Generar el id del elemento de usuario
            var usuarioElement = document.getElementById(usuarioElementId); // Obtener el elemento de usuario por su id
            if (usuarioElement) {
                usuarioElement.remove(); // Eliminar el elemento de usuario del DOM
            }
        } else {
            mostrarModalMensaje('Error al eliminar usuario del grupo: ' + xhr.responseText);
        }
    };
    xhr.send(JSON.stringify(data));
}


