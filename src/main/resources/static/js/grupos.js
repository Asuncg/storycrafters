document.addEventListener("DOMContentLoaded", function () {
    var abandonarGrupoBtns = document.querySelectorAll('.abandonar-grupo-btn');
    abandonarGrupoBtns.forEach(function (btn) {
        btn.addEventListener('click', function () {
            var grupoId = this.getAttribute('data-group-id');
            document.getElementById('grupoIdInput').value = grupoId;
        });
    });

    var submitInvitacionBtn = document.getElementById('submitInvitacionBtn');
    submitInvitacionBtn.addEventListener('click', function () {
        var codigoInvitacion = document.getElementById('codigoInvitacion').value;
        fetch('/grupos/ingresar-invitacion', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded'
            },
            body: new URLSearchParams({ codigoInvitacion: codigoInvitacion })
        })
            .then(response => {
                if (!response.ok) {
                    return response.json().then(data => {
                        throw new Error(data.message || 'Hubo un error al intentar ingresar al grupo.');
                    });
                }
                return response.json();
            })
            .then(data => {
                var invitationMessage = document.getElementById('invitationMessage');
                invitationMessage.style.display = 'block';
                if (data.status === 'success') {
                    invitationMessage.className = 'alert alert-success';
                    invitationMessage.textContent = data.message;
                } else {
                    invitationMessage.className = 'alert alert-danger';
                    invitationMessage.textContent = data.message;
                }
            })
            .catch(error => {
                var invitationMessage = document.getElementById('invitationMessage');
                invitationMessage.className = 'alert alert-danger';
                invitationMessage.textContent = error.message;
                invitationMessage.style.display = 'block';
                console.error('There has been a problem with your fetch operation:', error);
            });
    });
});

function openInvitationModal(element) {
    groupId = element.getAttribute('data-group-id');
    document.getElementById('invitationEmail').value = '';
    document.getElementById('invitationMessageModal').style.display = 'none'; // Cambiado aquí

    var invitationModal = new bootstrap.Modal(document.getElementById('invitationModal'));
    invitationModal.show();
}

// Función para manejar las invitaciones
function sendInvitations() {
    var email = document.getElementById('invitationEmail').value.trim();

    if (email === '') {
        document.getElementById('invitationMessageModal').innerText = 'Por favor, ingresa un correo electrónico.';
        document.getElementById('invitationMessageModal').classList.remove('text-success');
        document.getElementById('invitationMessageModal').classList.add('text-danger');
        document.getElementById('invitationMessageModal').style.display = 'block';
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
        var invitationMessage = document.getElementById('invitationMessageModal'); // Cambiado aquí
        if (xhr.status === 200) {
            invitationMessage.innerText = 'Invitación enviada con éxito.';
            invitationMessage.classList.remove('text-danger');
            invitationMessage.classList.add('text-success');
        } else {
            invitationMessage.innerText = 'Error al enviar la invitación.';
            invitationMessage.classList.remove('text-success');
            invitationMessage.classList.add('text-danger');
        }
        invitationMessage.style.display = 'block';
    };

    xhr.send(JSON.stringify(data));
}

document.addEventListener('DOMContentLoaded', function () {
    var sendInvitationBtn = document.getElementById('sendInvitationBtn');
    if (sendInvitationBtn) {
        sendInvitationBtn.addEventListener('click', function () {
            sendInvitations();
        });
    }
});



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
