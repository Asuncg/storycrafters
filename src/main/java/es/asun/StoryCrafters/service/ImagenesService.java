package es.asun.StoryCrafters.service;

import es.asun.StoryCrafters.entity.Imagen;

import java.util.List;

public interface ImagenesService {
    List<Imagen> findAllImagenes();

    Imagen findImageById(int id);
}
