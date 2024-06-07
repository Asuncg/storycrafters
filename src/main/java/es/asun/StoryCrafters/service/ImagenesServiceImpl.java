package es.asun.StoryCrafters.service;

import es.asun.StoryCrafters.entity.Imagen;
import es.asun.StoryCrafters.repository.ImagenesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Implementación del servicio para la gestión de imágenes.
 */
@Service
public class ImagenesServiceImpl implements ImagenesService{

    @Autowired
    private ImagenesRepository imagenesRepository;

    /**
     * Recupera todas las imágenes.
     * @return Lista de todas las imágenes.
     */
    @Override
    public List<Imagen> findAllImagenes() {
        return imagenesRepository.findAll();
    }

    /**
     * Encuentra una imagen por su ID.
     * @param id ID de la imagen a encontrar.
     * @return La imagen encontrada, o null si no se encuentra.
     */
    @Override
    public Imagen findImageById(int id) {
        return imagenesRepository.findById(id);
    }
}
