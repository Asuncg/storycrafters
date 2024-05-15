package es.asun.StoryCrafters.service;

import es.asun.StoryCrafters.entity.Grupo;
import es.asun.StoryCrafters.entity.Solicitud;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface SolicitudService {

    void eliminarSolicitud(Solicitud solicitud);

    void guardarSolicitud(Solicitud solicitud);

    List<Solicitud> buscarSolicitudesPorGrupo(Grupo grupo);

    Solicitud buscarSolicitudPorId(int id);

    @Transactional
    public void aceptarSolicitudes(String grupoId, List<Integer> solicitudIds);

    void eliminarSolicitudes(String grupoId, List<Integer> solicitudIds);
}
