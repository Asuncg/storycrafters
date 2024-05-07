package es.asun.StoryCrafters.repository;

import es.asun.StoryCrafters.entity.Grupo;
import es.asun.StoryCrafters.entity.RelatoGrupo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface  RelatoGrupoRepository extends JpaRepository<RelatoGrupo, Integer> {


    List<RelatoGrupo> findByGrupoIs(Grupo grupo);
}
