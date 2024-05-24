$(document).ready(function() {
    // Agregar campo de correo electrónico
    $('#addEmailBtn').click(function() {
        var emailField = '<div class="form-group email-field">' +
            '<input type="email" class="form-control" name="email" placeholder="Correo electrónico" required>' +
            '<button type="button" class="btn btn-danger remove-email">x</button>' +
            '</div>';
        $('#emailContainer').append(emailField);
    });

    // Eliminar campo de correo electrónico
    $('#emailContainer').on('click', '.remove-email', function() {
        $(this).closest('.email-field').remove();
    });

    // Validar formulario antes de enviar
    $('#invitationForm').submit(function(event) {
        var valid = true;
        $(this).find('input[type="email"]').each(function() {
            if (!$(this).val().trim()) {
                valid = false;
                return false; // Detener el bucle si se encuentra un campo vacío
            }
        });
        if (!valid) {
            alert('Por favor, complete todos los campos de correo electrónico.');
            event.preventDefault(); // Evitar el envío del formulario si hay campos vacíos
        }
    });
});