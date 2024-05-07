document.addEventListener('DOMContentLoaded', function () {
    const categoriaSelect = document.getElementById('categoria');
    const relatosContainer = document.getElementById('relatos');

    categoriaSelect.addEventListener('change', function () {
        const categoriaSeleccionada = categoriaSelect.value;
        const relatos = Array.from(relatosContainer.children);

        relatos.forEach(function (relato) {
            const categoriasRelato = Array.from(relato.querySelectorAll('.categorias')).map(input => input.value);
            const coincide = categoriaSeleccionada === '' || categoriasRelato.includes(categoriaSeleccionada);

            if (coincide) {
                relato.style.display = 'block'; // Asegúrate de que el relato sea visible
            } else {
                relato.style.display = 'none'; // Oculta el relato que no coincide
            }
        });
    });

    // Función para abrir el modal de eliminación de relato y capturar el ID del relato
    function openDeleteModal(element) {
        let relatoId = element.getAttribute('th:data-group-id');
        let deleteModal = document.getElementById('deleteModal');
        deleteModal.classList.add('show');
        deleteModal.style.display = 'block';
        document.getElementById('confirmDeleteBtn').setAttribute('data-relato-id', relatoId);
    }

    // Función para eliminar el relato
    document.getElementById('confirmDeleteBtn').addEventListener('click', function () {
        let relatoId = this.getAttribute('data-relato-id');
        // Aquí puedes enviar una solicitud al servidor para eliminar el relato con el ID relatoId
        // Puedes utilizar AJAX o fetch para enviar la solicitud al backend
        // Después de eliminar el relato, puedes recargar la página o actualizar la lista de relatos
        // Por ahora, solo lo mostraré en la consola
        console.log('Eliminando relato con ID:', relatoId);
        let deleteModal = document.getElementById('deleteModal');
        deleteModal.classList.remove('show');
        deleteModal.style.display = 'none';
    });
});