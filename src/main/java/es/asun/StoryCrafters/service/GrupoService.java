package es.asun.StoryCrafters.service;

import es.asun.StoryCrafters.entity.Grupo;
import es.asun.StoryCrafters.entity.Usuario;

import java.util.List;
import java.util.Optional;

public interface GrupoService {
    void guardarGrupo(Grupo grupo);

    List<Grupo> findAllGruposByUsuario(Usuario usuario);

     void deleteGrupoById(int idGrupo);

     Optional<Grupo> findGrupoById(int grupoId);
}

