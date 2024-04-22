package es.asun.StoryCrafters.repository;

import es.asun.StoryCrafters.entity.Imagenes;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImagenesRepository extends JpaRepository<Imagenes, Integer> {
    Imagenes findById(int id);
}
