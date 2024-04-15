document.addEventListener('DOMContentLoaded', function() {
    const categoriaSelect = document.getElementById('categoria');
    const relatoCards = document.querySelectorAll('.card');

    categoriaSelect.addEventListener('change', function() {

        const categoriaSeleccionada = categoriaSelect.value;

        relatoCards.forEach(function(card) {
            const categoriasRelato = Array.from(card.querySelectorAll('.card-text span')).map(span => span.textContent.trim());

            const coincide = categoriasRelato.includes(categoriaSeleccionada);

            if (categoriaSeleccionada === '' || coincide) {
                card.style.display = 'block';
            } else {
                card.style.display = 'none';
            }
        });
    });
});

