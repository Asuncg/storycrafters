package es.asun.StoryCrafters.repository;

import es.asun.StoryCrafters.entity.Imagen;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImagenesRepository extends JpaRepository<Imagen, Integer> {
    Imagen findById(int id);
}
