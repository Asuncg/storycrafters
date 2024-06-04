document.addEventListener('DOMContentLoaded', function () {
    const categoriaSelect = document.getElementById('categoria');
    const searchInput = document.getElementById('searchInput');
    const relatosContainer = document.getElementById('relatos');

    function filterRelatos() {
        const categoriaSeleccionada = categoriaSelect.value.toLowerCase();
        const query = searchInput.value.toLowerCase();
        const relatos = Array.from(relatosContainer.children);

        relatos.forEach(function (relato) {
            const tituloRelato = relato.querySelector('.card-title').textContent.toLowerCase();
            const categoriasRelato = Array.from(relato.querySelectorAll('.categorias')).map(input => input.value.toLowerCase());
            const coincideCategoria = categoriaSeleccionada === '' || categoriasRelato.includes(categoriaSeleccionada);
            const coincideBusqueda = query === '' || tituloRelato.includes(query);

            if (coincideCategoria && coincideBusqueda) {
                relato.style.display = 'block';
            } else {
                relato.style.display = 'none';
            }
        });
    }

    categoriaSelect.addEventListener('change', filterRelatos);
    searchInput.addEventListener('input', filterRelatos);
});
