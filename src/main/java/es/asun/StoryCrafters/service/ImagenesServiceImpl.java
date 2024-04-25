package es.asun.StoryCrafters.service;

import es.asun.StoryCrafters.entity.Imagen;
import es.asun.StoryCrafters.repository.ImagenesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class ImagenesServiceImpl implements ImagenesService{
    @Autowired
    private ImagenesRepository imagenesRepository;
    @Override
    public List<Imagen> findAllImagenes() {
        return imagenesRepository.findAll();
    }

    @Override
    public Imagen findImageById(int id) {
        return imagenesRepository.findById(id);
    }
}
