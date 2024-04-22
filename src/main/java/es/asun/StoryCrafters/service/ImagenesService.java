package es.asun.StoryCrafters.service;

import es.asun.StoryCrafters.entity.Imagenes;

import java.util.List;

public interface ImagenesService {
    List<Imagenes> findAllImagenes();

    Imagenes findImageById(int id);
}
