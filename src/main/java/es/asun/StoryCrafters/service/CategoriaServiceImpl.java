package es.asun.StoryCrafters.service;

import es.asun.StoryCrafters.entity.Categoria;
import es.asun.StoryCrafters.repository.CategoriaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class CategoriaServiceImpl implements CategoriaService{
    @Autowired
    private CategoriaRepository categoriaRepository;
    @Override
    public List<Categoria> findAllCategories() {

        return categoriaRepository.findAll();
    }

}
