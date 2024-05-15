// Variable global para almacenar el ID del grupo
var groupId;
document.addEventListener("DOMContentLoaded", function() {
    var abandonarGrupoBtns = document.querySelectorAll('.abandonar-grupo-btn');
    abandonarGrupoBtns.forEach(function(btn) {
        btn.addEventListener('click', function() {
            var grupoId = this.getAttribute('data-group-id');
            document.getElementById('grupoIdInput').value = grupoId;
        });
    });
});


// Función para abrir el modal de invitación
function openInvitationModal(element) {
    // Guardar el ID del grupo en la variable global
    groupId = element.getAttribute('data-group-id');

    // Limpiar el campo de correo electrónico y el mensaje
    $('#invitationEmail').val('');
    $('#invitationMessage').hide();


    // Mostrar el modal
    $('#invitationModal').modal('show');


}

$(document).ready(function() {
    // Vincular evento click del botón "Enviar Invitación"
    $('#sendInvitationBtn').click(function() {
        console.log("Botón 'Enviar Invitación' clickeado");
        sendInvitations();
    });

});

function sendInvitations() {
    // Obtener el correo electrónico
    var email = document.getElementById('invitationEmail').value.trim();

    // Verificar que el correo electrónico no esté vacío
    if (email === '') {
        document.getElementById('invitationMessage').innerText = 'Por favor, ingresa un correo electrónico.';
        document.getElementById('invitationMessage').classList.remove('text-success');
        document.getElementById('invitationMessage').classList.add('text-danger');
        document.getElementById('invitationMessage').style.display = 'block';
        return;
    }

    // Datos a enviar en la solicitud
    var data = {
        groupId: groupId,
        emails: [email]
    };

    // Definir la URL de la solicitud Ajax
    var url = '/grupos/invitar-usuarios';

    // Realizar la petición Ajax
    var xhr = new XMLHttpRequest();
    xhr.open('POST', url, true);
    xhr.setRequestHeader('Content-Type', 'application/json');
    xhr.onload = function () {
        if (xhr.status === 200) {
            // Mostrar mensaje de éxito
            document.getElementById('invitationMessage').innerText = 'Invitación enviada con éxito.';
            document.getElementById('invitationMessage').classList.remove('text-danger');
            document.getElementById('invitationMessage').classList.add('text-success');
            document.getElementById('invitationMessage').style.display = 'block';
        } else {
            // Mostrar mensaje de error
            document.getElementById('invitationMessage').innerText = 'Error al enviar la invitación.';
            document.getElementById('invitationMessage').classList.remove('text-success');
            document.getElementById('invitationMessage').classList.add('text-danger');
            document.getElementById('invitationMessage').style.display = 'block';
        }
    };

    // Convertir los datos a JSON y enviar la solicitud
    xhr.send(JSON.stringify(data));
}

function mostrarPestana(idPestana) {
    // Ocultar todas las pestañas
    var pestanas = document.querySelectorAll('.pestana');
    pestanas.forEach(function (elemento) {
        elemento.style.display = 'none';
    });

    // Mostrar la pestaña seleccionada
    document.getElementById(idPestana).style.display = 'block';

    // Si la pestaña es "A revisar", filtrar relatos pendientes
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
    checkboxes.forEach(function(checkbox) {
        checkbox.checked = checkboxSeleccionarTodos.checked;
    });
}

function aceptarSeleccionadas() {
    var solicitudesSeleccionadas = obtenerSolicitudesSeleccionadas();
    var grupoId = document.getElementById('grupoId').value;
    if (solicitudesSeleccionadas.length > 0) {
        enviarSolicitudesSeleccionadas(solicitudesSeleccionadas, 'aceptar', grupoId);
    } else {
        alert('No se han seleccionado solicitudes para aceptar.');
    }
}

function rechazarSeleccionadas() {
    var solicitudesSeleccionadas = obtenerSolicitudesSeleccionadas();
    var grupoId = document.getElementById('grupoId').value;
    if (solicitudesSeleccionadas.length > 0) {
        enviarSolicitudesSeleccionadas(solicitudesSeleccionadas, 'rechazar', grupoId);
    } else {
        alert('No se han seleccionado solicitudes para rechazar.');
    }
}

function obtenerSolicitudesSeleccionadas() {
    var checkboxes = document.querySelectorAll('input[name="solicitud"]:checked');
    var solicitudesSeleccionadas = [];
    checkboxes.forEach(function(checkbox) {
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

    // Crear la solicitud XMLHttpRequest
    var xhr = new XMLHttpRequest();
    xhr.open('POST', '/grupos/gestionar-solicitudes', true);
    xhr.setRequestHeader('Content-Type', 'application/json');
    xhr.onload = function () {
        if (xhr.status === 200) {
            // Remover las solicitudes gestionadas de la interfaz
            solicitudesSeleccionadas.forEach(function(solicitudId) {
                var solicitudElement = document.querySelector('input[name="solicitud"][value="' + solicitudId + '"]').closest('.list-group-item');
                solicitudElement.remove();
            });
        } else {
            alert('Error al procesar las solicitudes: ' + xhr.responseText);
        }
    };
    // Enviar los datos convertidos a JSON
    xhr.send(JSON.stringify(data));
}

