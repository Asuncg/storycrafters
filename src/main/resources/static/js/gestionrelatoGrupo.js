// CONFIRMACION PARA ELIMINAR UN RELATO DE GRUPO
var RelatoGrupoId;
document.addEventListener('DOMContentLoaded', function() {
    function confirmDelete(element, estado, vista) {
        RelatoGrupoId = element.getAttribute('data-relatoGrupo-id');
        let grupoId = element.getAttribute('data-grupo-id');
        let message;
        if (estado === 0) {
            message = "¿Estás seguro que quieres borrar este relato? Dejará de estar publicado en este grupo y el resto de usuarios no podrán verlo.";
        } else if (estado === 1) {
            message = "¿Deseas cancelar la revisión de este relato?";
        } else {
            message = "¿Deseas borrar este relato? Podrás volver a enviarlo para su revisión desde tus relatos.";
        }
        document.getElementById('deleteModalBody').innerText = message;
        document.getElementById('confirmDeleteButton').href = '/grupos/' + grupoId + '/eliminar-relato-grupo/' + RelatoGrupoId + '/' + vista;
        let deleteModal = new bootstrap.Modal(document.getElementById('deleteModal'));
        deleteModal.show();
    }

    function closeModal() {
        let deleteModal = bootstrap.Modal.getInstance(document.getElementById('deleteModal'));
        if (deleteModal) {
            deleteModal.hide();
        }
    }

    document.querySelectorAll('.close, .btn-secondary').forEach(function(button) {
        button.addEventListener('click', closeModal);
    });

    window.confirmDelete = confirmDelete;
});
