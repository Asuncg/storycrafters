package es.asun.StoryCrafters.service;

import es.asun.StoryCrafters.entity.Grupo;
import es.asun.StoryCrafters.entity.Usuario;

import java.util.List;

public interface GrupoService {
    void guardarGrupo(Grupo grupo);

    List<Grupo> findAllGruposByUsuario(Usuario usuario);

     void deleteGrupoById(int idGrupo);
}
