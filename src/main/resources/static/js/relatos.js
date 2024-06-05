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
    var textoCuento = document.getElementById('texto-relato');
    if (textoCuento) {
        textoCuento.addEventListener('keyup', function () {
            var caracteresEscritos = this.textContent.length;
            document.getElementById('caracteres-escritos').textContent = caracteresEscritos;
        });
    }
});

document.addEventListener('DOMContentLoaded', function () {
    var textoCuento = document.getElementById('texto-relato');
    if (textoCuento) {
        // Inicializa el contador con la longitud del texto actual
        var caracteresEscritos = textoCuento.textContent.length;
        document.getElementById('caracteres-escritos').textContent = caracteresEscritos;

        textoCuento.addEventListener('input', function () {
            // Limitar la longitud del texto a 4000 caracteres
            if (this.textContent.length > 4000) {
                this.textContent = this.textContent.substring(0, 4000);
                alert('Has alcanzado el límite máximo de 4000 caracteres.');
            }
            var caracteresEscritos = this.textContent.length;
            document.getElementById('caracteres-escritos').textContent = caracteresEscritos;
        });
    }
});

function guardarRelato() {
    var titulo = document.getElementById('titulo-relato').innerHTML;
    var texto = document.getElementById('texto-relato').innerHTML;
    var idImagen = document.getElementById('idImagen').value;
    var idRelato = document.getElementById('idRelato').value;

    var categoriasSeleccionadas = obtenerCategoriasSeleccionadas();

    if (!titulo) {
        $('#modalTituloError').modal('show');
        return;
    }

    var relatoDto = {
        id: idRelato,
        titulo: titulo,
        texto: texto,
        idImagen: idImagen,
        categorias: categoriasSeleccionadas
    };

    var url = '/relato/guardar-relato';

    // Petición Ajax
    var xhr = new XMLHttpRequest();
    xhr.open('POST', url, true);
    xhr.setRequestHeader('Content-Type', 'application/json');
    xhr.onload = function () {
        if (xhr.status === 200) {
            var idRelato = xhr.responseText;
            handleGuardarRelatoResponse(idRelato);
            cambiosPendientes = false;
            // Mostrar el modal de éxito
            $('#modalExito').modal('show');
            // Ocultar el modal después de 3 segundos
            setTimeout(function () {
                $('#modalExito').modal('hide');
            }, 3000);
        } else {
            // Mostrar el modal de error
            $('#modalError').modal('show');
        }
    };

    xhr.send(JSON.stringify(relatoDto));
}


function obtenerCategoriasSeleccionadas() {
    var categoriasSeleccionadas = [];
    document.querySelectorAll('input[name="categorias"]:checked').forEach(function (categoria) {
        categoriasSeleccionadas.push(categoria.value);
    });
    return categoriasSeleccionadas;
}

var cambiosPendientes = false;

document.addEventListener('DOMContentLoaded', function () {
    var tituloRelato = document.getElementById('titulo-relato');

    if (tituloRelato) {
        tituloRelato.addEventListener('input', function () {
            if (this.textContent.length > 50) {
                this.textContent = this.textContent.substring(0, 50);
                alert('Has alcanzado el límite máximo de 50 caracteres para el título.');
            }
        });
    }

    var textoRelato = document.getElementById('texto-relato');

    if (tituloRelato) {
        tituloRelato.addEventListener('input', function () {
            cambiosPendientes = true;
        });
    }

    if (textoRelato) {
        textoRelato.addEventListener('input', function () {
            cambiosPendientes = true;
        });
    }

    window.addEventListener('beforeunload', function (e) {
        if (cambiosPendientes) {
            var mensaje = 'Tienes cambios sin guardar. ¿Estás seguro de que quieres salir?';
            e.returnValue = mensaje;
            return mensaje;
        }
    });

});

function handleGuardarRelatoResponse(idRelato) {
    document.getElementById('idRelato').value = idRelato;
}

