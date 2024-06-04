//Hago la publicación de relato haciendo uso de prueba de las librerías de JQuery
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
            $('#publicarRelatoModal').modal('hide');

            $('#modalExito').modal('show');
            setTimeout(function () {
                $('#modalExito').modal('hide');
            }, 3000);
            setTimeout(function () {
                $('#modalExito').modal('hide');
            }, 3000);
        } else if (xhr.status === 400) {
            $('#publicarRelatoModal').modal('hide');

            $('#modalError').modal('show');
            setTimeout(function () {
                $('#modalError').modal('hide');
            }, 3000);
        } else {
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
