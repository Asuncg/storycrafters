package es.asun.StoryCrafters.service;

import es.asun.StoryCrafters.entity.Grupo;
import es.asun.StoryCrafters.entity.Solicitud;
import es.asun.StoryCrafters.entity.Usuario;
import es.asun.StoryCrafters.repository.SolicitudRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class SolicitudServiceImpl implements SolicitudService {

    @Autowired
    private SolicitudRepository solicitudRepository;

    @Autowired
    private GrupoService grupoService;

    @Override
    public void eliminarSolicitud(Solicitud solicitud) {
        solicitudRepository.delete(solicitud);
    }

    @Override
    @Transactional
    public void guardarSolicitud(Solicitud solicitud) {
        Optional<Solicitud> solicitudExistente = solicitudRepository.findByGrupoAndUsuario(solicitud.getGrupo(), solicitud.getUsuario());
        if (solicitudExistente.isEmpty()) {
            solicitudRepository.save(solicitud);
        }
    }

    @Override
    public List<Solicitud> buscarSolicitudesPorGrupo(Grupo grupo) {
        return solicitudRepository.findByGrupo(grupo);
    }

    @Override
    public Solicitud buscarSolicitudPorId(int id) {
        return solicitudRepository.findById(id);
    }

    @Transactional
    @Override
    public void aceptarSolicitudes(String grupoId, List<Integer> solicitudIds) {
        try {
            for (Integer solicitudId : solicitudIds) {
                Solicitud solicitud = buscarSolicitudPorId(solicitudId);

                Usuario usuario = solicitud.getUsuario();

                Grupo grupo = solicitud.getGrupo();
                grupo.getUsuarios().add(usuario);

                grupoService.guardarGrupo(grupo);

                eliminarSolicitud(solicitud);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error al gestionar las solicitudes", e);
        }
    }

    @Override
    public void eliminarSolicitudes(String grupoId, List<Integer> solicitudIds) {
        for (Integer solicitudId : solicitudIds) {
            Solicitud solicitud = buscarSolicitudPorId(solicitudId);
            eliminarSolicitud(solicitud);
        }
    }

    @Override
    public Optional<Solicitud> buscarSolicitud(Grupo grupo, Usuario usuario) {
        return solicitudRepository.findByGrupoAndUsuario(grupo, usuario);
    }
}