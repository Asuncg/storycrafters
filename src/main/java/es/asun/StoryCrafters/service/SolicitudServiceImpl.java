package es.asun.StoryCrafters.service;

import es.asun.StoryCrafters.entity.Grupo;
import es.asun.StoryCrafters.entity.Solicitud;
import es.asun.StoryCrafters.repository.SolicitudRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SolicitudServiceImpl implements SolicitudService{

    @Autowired
    private SolicitudRepository solicitudRepository;
    @Override
    public void gestionarSolicitud(Solicitud solicitud) {
        solicitudRepository.delete(solicitud);
    }

    @Override
    public void guardarSolicitud(Solicitud solicitud) {
        solicitudRepository.save(solicitud);
    }

    @Override
    public List<Solicitud> buscarSolicitudesPorGrupo(Grupo grupo) {
        return solicitudRepository.findByGrupo(grupo);
    }

    @Override
    public Solicitud buscarSolicitudPorId(int id) {
        return solicitudRepository.findById(id);
    }
}
