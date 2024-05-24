package es.asun.StoryCrafters.service;

import es.asun.StoryCrafters.entity.Grupo;
import es.asun.StoryCrafters.entity.Usuario;
import es.asun.StoryCrafters.model.GrupoDto;
import es.asun.StoryCrafters.model.RelatoGrupoGestionDto;
import org.springframework.ui.Model;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface GrupoService {
    void guardarGrupo(Grupo grupo);

    void crearGrupo(GrupoDto grupoDto);

    String actualizarGrupo(String idGrupo, GrupoDto grupoDto);

    List<Grupo> findAllGruposByUsuario(Usuario usuario);

    void deleteGrupoById(int idGrupo);

    Grupo findGrupoById(int grupoId);

    Optional<Grupo> findGrupoByCodigoAcceso(String codigoAcceso);

    String eliminarGrupo(Model model, String id);

    String enviarInvitacion(Map<String, Object> request, Model model);
    void abandonarGrupo(Usuario usuario, String grupoId);
    void mostrarVista(Grupo grupo, String opcion, Model model, Usuario usuario);
    void verMisRelatosGrupo(Grupo grupo, Usuario usuarioActual, Model model);
}

