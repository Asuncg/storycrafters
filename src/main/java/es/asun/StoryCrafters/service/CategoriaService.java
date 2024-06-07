package es.asun.StoryCrafters.service;

import es.asun.StoryCrafters.entity.Categoria;
import es.asun.StoryCrafters.exceptions.CategoriaNotFoundException;
import es.asun.StoryCrafters.model.RelatoDto;

import java.util.List;

public interface CategoriaService {

    List<Categoria> findAllCategories();

    Categoria encontrarCategoriaPorId(int id) throws CategoriaNotFoundException;

    List<Categoria> encontrarCategoriasPorRelato(RelatoDto relatoDto) throws CategoriaNotFoundException;
}

