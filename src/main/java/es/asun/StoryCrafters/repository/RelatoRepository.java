package es.asun.StoryCrafters.repository;

import es.asun.StoryCrafters.entity.Relato;
import es.asun.StoryCrafters.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RelatoRepository extends JpaRepository<Relato, Integer> {
    Relato findById(int id);

    List<Relato> findByUsuario(Usuario usuario);
}
