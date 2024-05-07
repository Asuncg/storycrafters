function publicarRelato() {
    // Obtener el id del relato y el id del grupo seleccionado
    var idRelato = document.getElementById('relatoId').value;
    var grupoId = document.getElementById('grupoId').value;

    if (!grupoId || !idRelato) {
        alert('Debe seleccionar un grupo');
        return;
    }

    // Crear un objeto XMLHttpRequest
    var xhr = new XMLHttpRequest();

    // Configurar la solicitud AJAX
    xhr.open('POST', '/relato/publicar-relato', true);
    xhr.setRequestHeader('Content-Type', 'application/json');

    // Manejar la respuesta del servidor
    xhr.onload = function () {
        if (xhr.status === 200) {
            // La solicitud se completó correctamente
            // Mostrar el mensaje de confirmación en el modal
            document.getElementById('mensaje').innerText = "El relato se ha enviado para su revisión previa publicación.";
            document.getElementById('mensaje').style.display = 'block';

            // Opcionalmente, puedes actualizar el contenido del modal aquí si es necesario
        } else {
            // La solicitud falló
            console.error('Error al publicar el relato: ', xhr.statusText);
        }
    };

    var data = {
        idRelato: idRelato,
        idGrupo: grupoId
    };

    // Enviar la solicitud al servidor
    xhr.send(JSON.stringify(data));
}