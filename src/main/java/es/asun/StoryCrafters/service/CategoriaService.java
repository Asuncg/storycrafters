package es.asun.StoryCrafters.service;


import es.asun.StoryCrafters.entity.Categoria;
import es.asun.StoryCrafters.entity.Relato;

import java.util.List;
import java.util.Optional;

public interface CategoriaService {

    List<Categoria> findAllCategories();

    Optional<Categoria> findById(int id);
}

