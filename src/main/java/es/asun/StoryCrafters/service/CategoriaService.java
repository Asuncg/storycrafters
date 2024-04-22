package es.asun.StoryCrafters.service;


import es.asun.StoryCrafters.entity.Categoria;
import es.asun.StoryCrafters.entity.Relato;

import java.util.List;

public interface CategoriaService {

    List<Categoria> findAllCategories();

}
