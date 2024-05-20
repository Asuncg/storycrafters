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
                relato.style.display = 'block';
            } else {
                relato.style.display = 'none';
            }
        });
    });
});