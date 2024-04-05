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
    console.log('Formulario enviado');
    document.getElementById('titulo-hidden').value = document.getElementById('titulo-relato').innerHTML;
    document.getElementById('texto-hidden').value = document.getElementById('texto-relato').innerHTML;

    document.getElementById('form-relato').submit();
}