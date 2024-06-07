package es.asun.StoryCrafters.repository;

import es.asun.StoryCrafters.entity.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoriaRepository extends JpaRepository<Categoria, Integer> {

}
