package es.asun.StoryCrafters.repository;

import es.asun.StoryCrafters.entity.Grupo;
import es.asun.StoryCrafters.entity.Relato;
import es.asun.StoryCrafters.entity.RelatoGrupo;
import es.asun.StoryCrafters.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface  RelatoGrupoRepository extends JpaRepository<RelatoGrupo, Integer> {

    List<RelatoGrupo> findByGrupoIs(Grupo grupo);

    Optional<RelatoGrupo> findByRelatoAndGrupo(Relato relato, Grupo grupo);

    List<RelatoGrupo> findByGrupoAndEstadoOrderByFechaPublicacionDesc(Grupo grupo, int estado);

    List<RelatoGrupo> findByRelato_UsuarioAndEstadoEquals(Usuario usuario, int estado);

}
