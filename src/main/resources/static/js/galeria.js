// Función para seleccionar la imagen al hacer clic en ella
function selectImage(image) {
    document.querySelectorAll('.gallery-img').forEach(function(item) {
        item.classList.remove('selected');
    });

    var radioInput = image.parentNode.querySelector('input[type="radio"]');
    radioInput.checked = true;

    image.parentNode.classList.add('selected');
}

