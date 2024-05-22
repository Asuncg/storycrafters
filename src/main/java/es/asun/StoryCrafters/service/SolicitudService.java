package es.asun.StoryCrafters.service;

import es.asun.StoryCrafters.entity.Grupo;
import es.asun.StoryCrafters.entity.Solicitud;
import es.asun.StoryCrafters.entity.Usuario;
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

    Optional<Solicitud> buscarSolicitud(Grupo grupo, Usuario usuario);
}
