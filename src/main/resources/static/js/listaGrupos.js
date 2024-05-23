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
        var invitationMessage = document.getElementById('invitationMessageModal');
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
