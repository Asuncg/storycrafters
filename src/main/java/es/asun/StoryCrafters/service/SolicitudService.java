package es.asun.StoryCrafters.service;

import es.asun.StoryCrafters.entity.Grupo;
import es.asun.StoryCrafters.entity.Solicitud;
import es.asun.StoryCrafters.entity.Usuario;
import es.asun.StoryCrafters.exceptions.GrupoException;
import es.asun.StoryCrafters.exceptions.UsuarioException;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface SolicitudService {

    void eliminarSolicitud(Solicitud solicitud);

    void guardarSolicitud(Solicitud solicitud);

    List<Solicitud> buscarSolicitudesPorGrupo(Grupo grupo);

    Solicitud buscarSolicitudPorId(int id);

    @Transactional
    void aceptarSolicitudes(String grupoId, List<Integer> solicitudIds);

    void eliminarSolicitudes(String grupoId, List<Integer> solicitudIds);

    Optional<Solicitud> buscarSolicitudPorGrupoYUsuario(Grupo grupo, Usuario usuario);

    void ingresarInvitacion(Usuario usuario, String codigoInvitacion) throws GrupoException, UsuarioException;

    boolean existenSolicitudesPendientes(Grupo grupo);

}
