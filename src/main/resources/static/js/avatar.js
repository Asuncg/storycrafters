// Función para seleccionar un avatar
function selectAvatar(element) {
    var selectedAvatarUrl = element.getAttribute('src');
    var selectedAvatarId = element.getAttribute('data-id');

    document.getElementById('current-avatar').src = selectedAvatarUrl;
    document.getElementById('selected-avatar-id').value = selectedAvatarId;
}

// Función para guardar los cambios
function saveChanges() {
    var selectedAvatarId = document.getElementById('selected-avatar-id').value;
    if (selectedAvatarId) {
        // Enviar el formulario para guardar el avatar seleccionado
        document.getElementById('avatar-form').submit();
    } else {
        alert("Por favor, selecciona un avatar antes de guardar los cambios.");
    }
}
