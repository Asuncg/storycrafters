package es.asun.StoryCrafters.service;

import es.asun.StoryCrafters.entity.Grupo;
import es.asun.StoryCrafters.entity.RelatoGrupo;

import java.util.List;
import java.util.Optional;

public interface RelatoGrupoService {
    Optional<RelatoGrupo> findRelatoGrupoById (int idRelatoGrupo);
    List<RelatoGrupo> findRelatoGrupoByGrupoIs(Grupo grupo);

    void guardarRelatoGrupo(RelatoGrupo relatoGrupo);

    boolean existeRelatoEnviado(int idRelato, int idGrupo);
}
