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

@Service
public class CategoriaServiceImpl implements CategoriaService{
    @Autowired
    private CategoriaRepository categoriaRepository;
    @Override
    public List<Categoria> findAllCategories() {

        return categoriaRepository.findAll();
    }
    @Override
    public Categoria encontrarCategoriaPorId(int id) throws CategoriaNotFoundException {
        Optional<Categoria> categoriaOptional = categoriaRepository.findById(id);
        if (categoriaOptional.isEmpty()) {
            throw new CategoriaNotFoundException("Categoria no encontrada");
        }
        return categoriaOptional.get();
    }

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

