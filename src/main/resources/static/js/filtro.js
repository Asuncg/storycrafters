document.addEventListener('DOMContentLoaded', function() {
    const categoriaSelect = document.getElementById('categoria');
    const relatoRows = document.querySelectorAll('.relatoRow');

    console.log("Script cargado");

    categoriaSelect.addEventListener('change', function() {
        console.log("Selección de categoría cambiada");

        const categoriaSeleccionada = categoriaSelect.value;
        console.log("Categoría seleccionada:", categoriaSeleccionada);

        relatoRows.forEach(function(row) {
            const categoriasRelato = Array.from(row.querySelectorAll('li')).map(li => li.textContent.trim());
            console.log("Categorías del relato:", categoriasRelato);

            const coincide = categoriasRelato.includes(categoriaSeleccionada);
            console.log("¿Coincide la categoría seleccionada con las categorías del relato?", coincide);

            if (categoriaSeleccionada === '' || coincide) {
                row.style.display = 'table-row';
            } else {
                row.style.display = 'none';
            }
        });
    });
});

