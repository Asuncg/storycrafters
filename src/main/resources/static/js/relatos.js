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
        textoCuento.addEventListener('input', function() {
            var caracteresEscritos = this.textContent.length;
            document.getElementById('caracteres-escritos').textContent = caracteresEscritos;
        });
    }
});

function getContent(){
document.getElementById('form-relato').addEventListener('submit', function() {
    console.log('Formulario enviado');
    debugger;
    document.getElementById('titulo-hidden').value = document.getElementById('titulo-relato').innerText;
    document.getElementById('texto-hidden').value = document.getElementById('texto-relato').innerText;
    titulo = document.getElementById('titulo-hidden').value;
    texto = document.getElementById('texto-hidden').value;
    const llamada = {titulo, texto};
    console.log(llamada);
});}
