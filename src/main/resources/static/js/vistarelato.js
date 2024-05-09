function publicarRelato() {
    var idRelato = document.getElementById('relatoId').value;
    var grupoId = document.getElementById('grupoId').value;

    if (!grupoId || !idRelato) {
        alert('Debe seleccionar un grupo');
        return;
    }

    var xhr = new XMLHttpRequest();
    xhr.open('POST', '/relato/publicar-relato', true);
    xhr.setRequestHeader('Content-Type', 'application/json');

    xhr.onload = function () {
        if (xhr.status === 200) {
            // La solicitud se completó correctamente
            // Cerrar el modal actual
            $('#publicarRelatoModal').modal('hide');

            // Mostrar el modal de éxito
            $('#modalExito').modal('show');
            setTimeout(function () {
                $('#modalExito').modal('hide');
            }, 3000);
            // Ocultar el modal de éxito después de 3 segundos
            setTimeout(function () {
                $('#modalExito').modal('hide');
            }, 3000);
        } else if (xhr.status === 400) {
            // El relato ya ha sido enviado a ese grupo
            // Cerrar el modal actual
            $('#publicarRelatoModal').modal('hide');

            // Mostrar el modal de error
            $('#modalError').modal('show');
            setTimeout(function () {
                $('#modalError').modal('hide');
            }, 3000);
        } else {
            // La solicitud falló
            console.error('Error al publicar el relato: ', xhr.statusText);
        }
    };

    var data = {
        idRelato: idRelato,
        idGrupo: grupoId
    };

    var jsonData = JSON.stringify(data);
    xhr.send(jsonData);
}
