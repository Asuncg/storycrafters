package es.asun.StoryCrafters.repository;

import es.asun.StoryCrafters.entity.Relato;
import es.asun.StoryCrafters.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RelatoRepository extends JpaRepository<Relato, Integer> {
    List<Relato> findByUsuario(Usuario usuario);

    @Query("SELECT r FROM Relato r LEFT JOIN FETCH r.categorias WHERE r.id = :id")
    Relato findRelatoById(@Param("id") int id);
}
