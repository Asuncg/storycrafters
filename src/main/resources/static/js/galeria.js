function selectImage(element) {
    const imageId = element.getAttribute('data-imagen-id');

    if (imageId === '1') {
        var noImageModal = new bootstrap.Modal(document.getElementById('noImageModal'));
        noImageModal.show();
    } else {
        var modalImage = document.getElementById('selectedImage');
        modalImage.src = element.src;

        var confirmationModal = new bootstrap.Modal(document.getElementById('confirmationModal'));
        confirmationModal.show();
    }
}
