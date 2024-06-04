document.addEventListener("DOMContentLoaded", function () {
    var abandonarGrupoBtns = document.querySelectorAll('.abandonar-grupo-btn');
    abandonarGrupoBtns.forEach(function (btn) {
        btn.addEventListener('click', function () {
            var grupoId = this.getAttribute('data-group-id');
            document.getElementById('grupoIdInput').value = grupoId;
        });
    });

    var submitInvitacionBtn = document.getElementById('submitInvitacionBtn');
    var codigoInvitacionInput = document.getElementById('codigoInvitacion');
    var invitationMessage = document.getElementById('invitationMessage');
    var ingresarInvitacionModal = document.getElementById('ingresarInvitacionModal');

// Función para resetear el input y ocultar el mensaje
    function resetInputAndMessage() {
        codigoInvitacionInput.value = '';
        invitationMessage.style.display = 'none';
        invitationMessage.className = '';
        invitationMessage.textContent = '';
    }

    submitInvitacionBtn.addEventListener('click', function () {
        var codigoInvitacion = codigoInvitacionInput.value;
        fetch('/grupos/ingresar-invitacion', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded'
            },
            body: new URLSearchParams({codigoInvitacion: codigoInvitacion})
        })
            .then(response => {
                invitationMessage.style.display = 'block';

                if (response.ok) {
                    invitationMessage.className = 'alert alert-success';
                    invitationMessage.textContent = 'Invitación ingresada correctamente.';
                } else if (response.status === 409) {
                    invitationMessage.className = 'alert alert-danger';
                    invitationMessage.textContent = 'Necesitas configurar una firma de autor en tu perfil antes de entrar a un grupo';
                } else if (response.status === 401) {
                    invitationMessage.className = 'alert alert-danger';
                    invitationMessage.textContent = 'Ya perteneces a este grupo.';
                } else if (response.status === 400) {
                    invitationMessage.className = 'alert alert-danger';
                    invitationMessage.textContent = 'Ya has enviado una solicitud a este grupo.';
                } else {
                    invitationMessage.className = 'alert alert-danger';
                    invitationMessage.textContent = 'Hubo un error al intentar ingresar al grupo.';
                }
            })
            .catch(error => {
                invitationMessage.className = 'alert alert-danger';
                invitationMessage.textContent = 'Hubo un problema con tu operación de fetch.';
                invitationMessage.style.display = 'block';
                console.error('There has been a problem with your fetch operation:', error);
            });
    });

// Resetea el input y el mensaje cuando se hace clic en el input
    codigoInvitacionInput.addEventListener('click', resetInputAndMessage);

// Resetea el input y el mensaje cuando el modal se cierra
    ingresarInvitacionModal.addEventListener('hidden.bs.modal', resetInputAndMessage);

});

function openInvitationModal(element) {
    groupId = element.getAttribute('data-group-id');
    document.getElementById('invitationEmail').value = '';
    document.getElementById('invitationMessageModal').style.display = 'none'; // Cambiado aquí

    var invitationModal = new bootstrap.Modal(document.getElementById('invitationModal'));
    invitationModal.show();
}

document.addEventListener("DOMContentLoaded", function () {
    var sendInvitationBtn = document.getElementById('sendInvitationBtn');
    if (sendInvitationBtn) {
        sendInvitationBtn.addEventListener('click', function () {
            sendInvitation();
        });
    }

    var invitationEmailInput = document.getElementById('invitationEmail');
    invitationEmailInput.addEventListener('click', function () {
        var invitationMessage = document.getElementById('invitationMessageModal');
        invitationMessage.style.display = 'none';
    });
});

function isValidEmail(email) {
    var emailRegex = /^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$/;
    return emailRegex.test(email);
}

function sendInvitation() {
    var email = document.getElementById('invitationEmail').value.trim();
    var invitationMessage = document.getElementById('invitationMessageModal');

    if (email === '') {
        invitationMessage.innerText = 'Por favor, ingresa un correo electrónico.';
        invitationMessage.classList.remove('text-success');
        invitationMessage.classList.add('text-danger');
        invitationMessage.style.display = 'block';
        return;
    }

    if (!isValidEmail(email)) {
        invitationMessage.innerText = 'Por favor, ingresa un correo electrónico válido.';
        invitationMessage.classList.remove('text-success');
        invitationMessage.classList.add('text-danger');
        invitationMessage.style.display = 'block';
        return;
    }

    var url = `/grupos/invitar-usuarios?groupId=${groupId}&email=${encodeURIComponent(email)}`;

    var xhr = new XMLHttpRequest();
    xhr.open('POST', url, true);
    xhr.onload = function () {
        if (xhr.status === 200) {
            invitationMessage.innerText = 'Invitación enviada con éxito.';
            invitationMessage.classList.remove('text-danger');
            invitationMessage.classList.add('text-success');
            document.getElementById('invitationEmail').value = '';
        } else if (xhr.status === 409) {
            invitationMessage.innerText = 'Ya hay un usuario con este correo en el grupo.';
            invitationMessage.classList.remove('text-success');
            invitationMessage.classList.add('text-danger');
        } else {
            invitationMessage.innerText = 'Error al enviar la invitación.';
            invitationMessage.classList.remove('text-success');
            invitationMessage.classList.add('text-danger');
        }
        invitationMessage.style.display = 'block';
    };

    xhr.send();
}