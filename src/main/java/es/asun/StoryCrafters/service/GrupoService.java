package es.asun.StoryCrafters.service;

import es.asun.StoryCrafters.entity.Grupo;
import es.asun.StoryCrafters.entity.Usuario;
import es.asun.StoryCrafters.exceptions.GrupoException;
import es.asun.StoryCrafters.model.GrupoDto;
import org.springframework.ui.Model;

import java.util.List;
import java.util.Optional;

public interface GrupoService {
    void guardarGrupo(Grupo grupo);

    void crearGrupo(GrupoDto grupoDto);

    String actualizarGrupo(String idGrupo, GrupoDto grupoDto) throws GrupoException;

    List<Grupo> findAllGruposByUsuario(Usuario usuario);

    void deleteGrupoById(int idGrupo);

    Grupo findGrupoById(int grupoId) throws GrupoException;

    Optional<Grupo> findGrupoByCodigoAcceso(String codigoAcceso);

    void eliminarGrupo(int id);

    void enviarInvitacion(int idGrupo, List<String> emails) throws GrupoException;
    void abandonarGrupo(Usuario usuario, String grupoId) throws GrupoException;
    void mostrarVista(Grupo grupo, String opcion, Model model, Usuario usuario) throws GrupoException;
    void verMisRelatosGrupo(Grupo grupo, Usuario usuarioActual, Model model);
    Boolean existeCodigoAcceso(String codigoAcceso);
}

