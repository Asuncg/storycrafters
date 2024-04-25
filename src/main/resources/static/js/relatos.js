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



function guardarRelatoSalir() {

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


function guardarRelato() {
    // Obtener los datos del formulario
    var titulo = document.getElementById('titulo-relato').innerHTML;
    var texto = document.getElementById('texto-relato').innerHTML;
    var idImagen = document.getElementById('idImagen').value;

    // Obtener las categorías seleccionadas
    var categoriasSeleccionadas = obtenerCategoriasSeleccionadas();

    // Crear un objeto relatoDto
    var relatoDto = {
        titulo: titulo,
        texto: texto,
        idImagen: idImagen,
        categorias: categoriasSeleccionadas
    };

    // Realizar la petición Ajax
    var xhr = new XMLHttpRequest();
    xhr.open('POST', '/relato/guardar-relato', true);
    xhr.setRequestHeader('Content-Type', 'application/json');
    xhr.onload = function () {
        if (xhr.status === 200) {
            cambiosPendientes = false;
            // Mostrar el modal de éxito
            $('#modalExito').modal('show');
            // Ocultar el modal después de 3 segundos
            setTimeout(function(){
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

// Variable para controlar si se han realizado cambios
var cambiosPendientes = false;

// Detectar cambios en el editor
document.getElementById('titulo-relato').addEventListener('input', function() {
    cambiosPendientes = true;
});

document.getElementById('texto-relato').addEventListener('input', function() {
    cambiosPendientes = true;
});

window.addEventListener('beforeunload', function(e) {
    if (cambiosPendientes) {
        e.returnValue = true;
    }
});


//ACTUALIZACION DE RELATO

function actualizarRelato() {
    // Obtener los datos del formulario
    var idRelato = document.getElementById('idRelato').value;
    var titulo = document.getElementById('titulo-relato').innerHTML;
    var texto = document.getElementById('texto-relato').innerHTML;
    var idImagen = document.getElementById('idImagen').value;

    // Obtener las categorías seleccionadas
    var categoriasSeleccionadas = obtenerCategoriasSeleccionadas();

    // Crear un objeto relatoDto
    var relatoDto = {
        id: idRelato,
        titulo: titulo,
        texto: texto,
        idImagen: idImagen,
        categorias: categoriasSeleccionadas
    };

    // Realizar la petición Ajax para actualizar el relato
    var xhr = new XMLHttpRequest();
    xhr.open('PUT', '/relato/actualizar-relato', true);
    xhr.setRequestHeader('Content-Type', 'application/json');
    xhr.onload = function () {
        if (xhr.status === 200) {
            cambiosPendientes = false;
            // Mostrar el modal de éxito
            $('#modalExito').modal('show');
            // Ocultar el modal después de 3 segundos
            setTimeout(function(){
                $('#modalExito').modal('hide');
            }, 3000);
        } else {
            // Mostrar el modal de error
            $('#modalError').modal('show');
        }
    };

    xhr.send(JSON.stringify(relatoDto));
}

