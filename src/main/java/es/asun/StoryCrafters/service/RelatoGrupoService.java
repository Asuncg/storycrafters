package es.asun.StoryCrafters.service;

import es.asun.StoryCrafters.entity.Grupo;
import es.asun.StoryCrafters.entity.Relato;
import es.asun.StoryCrafters.entity.RelatoGrupo;

import java.util.List;
import java.util.Optional;

public interface RelatoGrupoService {
    Optional<RelatoGrupo> findRelatoGrupoById (int idRelatoGrupo);
    List<RelatoGrupo> findRelatoGrupoByGrupoIs(Grupo grupo);

    void guardarRelatoGrupo(RelatoGrupo relatoGrupo);

    Optional<RelatoGrupo> findRelatoGrupoByRelatoAndGrupo(Relato relato, Grupo grupo);

    List<RelatoGrupo> buscarRelatosGrupo(Grupo grupo, int estado);

    void eliminarRelatoGrupo (int idrelato);

}
