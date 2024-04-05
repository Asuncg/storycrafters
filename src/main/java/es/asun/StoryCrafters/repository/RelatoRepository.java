package es.asun.StoryCrafters.repository;

import es.asun.StoryCrafters.entity.Relato;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RelatoRepository extends JpaRepository<Relato, Integer> {
    Relato findById(int id);
}
