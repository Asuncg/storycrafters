package es.asun.StoryCrafters.service;

import es.asun.StoryCrafters.entity.Categoria;
import es.asun.StoryCrafters.exceptions.CategoriaNotFoundException;
import es.asun.StoryCrafters.model.RelatoDto;
import es.asun.StoryCrafters.repository.CategoriaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Implementación del servicio de gestión de categorías.
 */
@Service
public class CategoriaServiceImpl implements CategoriaService {

    /**
     * Repositorio para acceder a los datos de las categorías.
     */
    @Autowired
    private CategoriaRepository categoriaRepository;

    /**
     * Obtiene todas las categorías disponibles.
     * @return Lista de categorías.
     */
    @Override
    public List<Categoria> findAllCategories() {
        return categoriaRepository.findAll();
    }

    /**
     * Encuentra una categoría por su identificador único.
     * @param id Identificador único de la categoría.
     * @return La categoría encontrada.
     * @throws CategoriaNotFoundException Si no se encuentra la categoría con el identificador dado.
     */
    @Override
    public Categoria encontrarCategoriaPorId(int id) throws CategoriaNotFoundException {
        Optional<Categoria> categoriaOptional = categoriaRepository.findById(id);
        if (categoriaOptional.isEmpty()) {
            throw new CategoriaNotFoundException("Categoria no encontrada");
        }
        return categoriaOptional.get();
    }

    /**
     * Encuentra las categorías asociadas a un relato.
     * @param relatoDto DTO del relato que contiene las categorías.
     * @return Lista de categorías asociadas al relato.
     * @throws CategoriaNotFoundException Si una de las categorías asociadas al relato no se encuentra.
     */
    @Override
    public List<Categoria> encontrarCategoriasPorRelato(RelatoDto relatoDto) throws CategoriaNotFoundException {
        List<Categoria> listaCategorias = new ArrayList<>();
        for (Integer idCategoria : relatoDto.getCategorias()) {
            Categoria categoria = this.encontrarCategoriaPorId(idCategoria);
            listaCategorias.add(categoria);
        }
        return listaCategorias;
    }

}
