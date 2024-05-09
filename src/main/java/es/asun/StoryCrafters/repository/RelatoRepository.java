package es.asun.StoryCrafters.repository;

import es.asun.StoryCrafters.entity.Relato;
import es.asun.StoryCrafters.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RelatoRepository extends JpaRepository<Relato, Integer> {
    List<Relato> findByUsuarioAndArchivadoFalse(Usuario usuario);

    @Query("SELECT r FROM Relato r LEFT JOIN FETCH r.categorias WHERE r.id = :id AND r.archivado = false")
    Optional<Relato> findRelatoByIdAndNotArchivado(@Param("id") int id);

    List<Relato> findRelatoByUsuario(Usuario usuario);
}
