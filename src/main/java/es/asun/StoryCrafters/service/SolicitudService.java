package es.asun.StoryCrafters.service;

import es.asun.StoryCrafters.entity.Grupo;
import es.asun.StoryCrafters.entity.Solicitud;

import java.util.List;

public interface SolicitudService {

    void gestionarSolicitud(Solicitud solicitud);

    void guardarSolicitud(Solicitud solicitud);

    List<Solicitud> buscarSolicitudesPorGrupo(Grupo grupo);

    Solicitud buscarSolicitudPorId(int id);

}
