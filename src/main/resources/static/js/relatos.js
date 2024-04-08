function negritaTexto() {
    document.execCommand('bold');
}

function cursivaTexto() {
    document.execCommand('italic');
}

function justificarTexto(alineacion) {
    document.execCommand('justify' + alineacion);
}

document.addEventListener('DOMContentLoaded', function () {
    var textoCuento = document.getElementById('texto-cuento');
    if (textoCuento) {
        textoCuento.addEventListener('input', function () {
            var caracteresEscritos = this.textContent.length;
            document.getElementById('caracteres-escritos').textContent = caracteresEscritos;
        });
    }
});

function guardarRelato() {
    // Obtener el título y el texto del relato
    document.getElementById('titulo-hidden').value = document.getElementById('titulo-relato').innerHTML;
    document.getElementById('texto-hidden').value = document.getElementById('texto-relato').innerHTML;

    // Encuentra todos los checkboxes de categorías que fueron seleccionados
    const categoriasSeleccionadas = document.querySelectorAll('input[name="categorias"]:checked');

    // Obtener los IDs de las categorías seleccionadas y almacenarlos en un array
    const idsCategoriasSeleccionadas = [];
    categoriasSeleccionadas.forEach(categoria => {
        idsCategoriasSeleccionadas.push(categoria.value);
    });

    // Asignar los IDs de categorías seleccionadas al input hidden
    document.getElementById('categorias-seleccionadas').value = idsCategoriasSeleccionadas.join(',');

    // Envía el formulario
    document.getElementById('form-relato').submit();
}
