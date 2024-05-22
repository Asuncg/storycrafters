package es.asun.StoryCrafters.repository;

import es.asun.StoryCrafters.entity.Grupo;
import es.asun.StoryCrafters.entity.Relato;
import es.asun.StoryCrafters.entity.RelatoGrupo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface  RelatoGrupoRepository extends JpaRepository<RelatoGrupo, Integer> {

    @Query("SELECT COUNT(rg) > 0 FROM RelatoGrupo rg WHERE rg.relato.id = :idRelato AND rg.grupo.id = :idGrupo AND rg.estado IN (:estados)")
    boolean existeRelatoEnviado(@Param("idRelato") int idRelato, @Param("idGrupo") int idGrupo, @Param("estados") List<Integer> estados);
    List<RelatoGrupo> findByGrupoIs(Grupo grupo);

    Optional<RelatoGrupo> findByRelatoAndGrupo(Relato relato, Grupo grupo);

    List<RelatoGrupo> findByGrupoAndEstadoOrderByFechaPublicacionDesc(Grupo grupo, int estado);
}
