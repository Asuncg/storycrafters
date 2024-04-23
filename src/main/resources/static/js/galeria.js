// Función para seleccionar la imagen al hacer clic en ella
function selectImage(image) {
    // Removemos la clase 'selected' de todas las imágenes
    document.querySelectorAll('.gallery-img').forEach(function(item) {
        item.classList.remove('selected');
    });

    // Obtenemos el input de radio asociado a la imagen
    var radioInput = image.parentNode.querySelector('input[type="radio"]');
    // Marcamos el input de radio como seleccionado
    radioInput.checked = true;

    // Agregamos la clase 'selected' a la imagen seleccionada
    image.parentNode.classList.add('selected');
}