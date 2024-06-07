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

import java.util.List;
import java.util.Optional;
/**
 * Implementación del servicio de gestión de solicitudes.
 */
@Service
public class SolicitudServiceImpl implements SolicitudService {

    @Autowired
    private SolicitudRepository solicitudRepository;

    @Autowired
    private GrupoService grupoService;

    /**
     * Elimina una solicitud de la base de datos.
     * @param solicitud La solicitud a eliminar.
     */
    @Override
    public void eliminarSolicitud(Solicitud solicitud) {
        solicitudRepository.delete(solicitud);
    }

    /**
     * Guarda una nueva solicitud en la base de datos si no existe una solicitud previa del mismo usuario para el mismo grupo.
     * @param solicitud La solicitud a guardar.
     */
    @Override
    @Transactional
    public void guardarSolicitud(Solicitud solicitud) {
        Optional<Solicitud> solicitudExistente = solicitudRepository.findByGrupoAndUsuario(solicitud.getGrupo(), solicitud.getUsuario());
        if (solicitudExistente.isEmpty()) {
            solicitudRepository.save(solicitud);
        }
    }

    /**
     * Busca todas las solicitudes de un grupo en particular.
     * @param grupo El grupo del cual se quieren buscar las solicitudes.
     * @return Una lista de todas las solicitudes del grupo.
     */
    @Override
    public List<Solicitud> buscarSolicitudesPorGrupo(Grupo grupo) {
        return solicitudRepository.findByGrupo(grupo);
    }

    /**
     * Busca una solicitud por su ID.
     * @param id El ID de la solicitud a buscar.
     * @return La solicitud encontrada.
     */
    @Override
    public Solicitud buscarSolicitudPorId(int id) {
        return solicitudRepository.findById(id);
    }

    /**
     * Acepta las solicitudes de ingreso de los usuarios al grupo.
     * @param grupoId El ID del grupo al cual se aceptarán las solicitudes.
     * @param solicitudIds Los IDs de las solicitudes a aceptar.
     */
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

    /**
     * Elimina las solicitudes pendientes de un grupo.
     * @param grupoId El ID del grupo del cual se eliminarán las solicitudes.
     * @param solicitudIds Los IDs de las solicitudes a eliminar.
     */
    @Override
    public void eliminarSolicitudes(String grupoId, List<Integer> solicitudIds) {
        for (Integer solicitudId : solicitudIds) {
            Solicitud solicitud = buscarSolicitudPorId(solicitudId);
            eliminarSolicitud(solicitud);
        }
    }

    /**
     * Busca una solicitud por grupo y usuario.
     * @param grupo El grupo al cual pertenece la solicitud.
     * @param usuario El usuario que ha enviado la solicitud.
     * @return La solicitud encontrada.
     */
    @Override
    public Optional<Solicitud> buscarSolicitudPorGrupoYUsuario(Grupo grupo, Usuario usuario) {
        return solicitudRepository.findByGrupoAndUsuario(grupo, usuario);
    }

    /**
     * Ingresa a un usuario a un grupo mediante una invitación.
     * @param usuario El usuario que recibió la invitación.
     * @param codigoInvitacion El código de invitación del grupo.
     * @throws SolicitudException Si ya se ha enviado una solicitud de ingreso para este grupo.
     * @throws GrupoException Si hay un error al intentar ingresar al grupo.
     * @throws UsuarioException Si el usuario no tiene una firma de autor configurada.
     */
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

        if (usuario.getFirmaAutor() == null || usuario.getFirmaAutor().isEmpty()) {
            throw new UsuarioException("Debes tener una firma de Autor para poder publicar tus obras antes de entrar a un grupo. Configúrala en tu perfil!");
        }

        Optional<Solicitud> solicitudOptional = this.buscarSolicitudPorGrupoYUsuario(grupo, usuario);

        if (solicitudOptional.isPresent()) {
            throw new SolicitudException("Ya has enviado una solicitud de ingreso para este grupo.");
        }

        Solicitud solicitud = new Solicitud();
        solicitud.setGrupo(grupo);
        solicitud.setUsuario(usuario);
        this.guardarSolicitud(solicitud);
    }

    /**
     * Verifica si existen solicitudes pendientes para un grupo.
     * @param grupo El grupo del cual se quiere verificar la existencia de solicitudes pendientes.
     * @return true si existen solicitudes pendientes, false en caso contrario.
     */
    @Override
    public boolean existenSolicitudesPendientes(Grupo grupo) {
        return solicitudRepository.existsSolicitudByGrupo(grupo);
    }
}