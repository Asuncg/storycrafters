package es.asun.StoryCrafters.service;

import es.asun.StoryCrafters.entity.Grupo;
import es.asun.StoryCrafters.entity.Solicitud;
import es.asun.StoryCrafters.entity.Usuario;
import es.asun.StoryCrafters.exceptions.GrupoException;
import es.asun.StoryCrafters.exceptions.SolicitudException;
import es.asun.StoryCrafters.exceptions.UsuarioException;
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

    @Override
    public void ingresarInvitacion(Usuario usuario, String codigoInvitacion) throws SolicitudException, GrupoException, UsuarioException {
        Optional<Grupo> grupoOptional = grupoService.findGrupoByCodigoAcceso(codigoInvitacion);

        if (grupoOptional.isEmpty()) {
            throw new GrupoException("Hubo un error al intentar ingresar al grupo.");
        }

        Grupo grupo = grupoOptional.get();

        if (grupo.getUsuarios().contains(usuario)) {
            throw new GrupoException("Ya eres miembro de este grupo.");
        }

        if (usuario.getFirmaAutor().isEmpty()) {
            throw new UsuarioException("Debes tener una firma de Autor para poder publicar tus obras antes de entrar a un grupo. Configúrala en tu perfil!");
        }

        Optional<Solicitud> solicitudOptional = this.buscarSolicitud(grupo, usuario);

        if (solicitudOptional.isPresent()) {
            throw new SolicitudException("Ya has enviado una solicitud de ingreso para este grupo.");
        }

        Solicitud solicitud = new Solicitud();
        solicitud.setGrupo(grupo);
        solicitud.setUsuario(usuario);
        this.guardarSolicitud(solicitud);
    }
}