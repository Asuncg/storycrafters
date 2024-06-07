package es.asun.StoryCrafters.service;
import es.asun.StoryCrafters.entity.*;
import es.asun.StoryCrafters.exceptions.*;
import es.asun.StoryCrafters.model.GrupoDto;
import es.asun.StoryCrafters.repository.GrupoRepository;
import es.asun.StoryCrafters.utils.AuthUtils;
import es.asun.StoryCrafters.utils.CodigoIngresoGenerator;
import es.asun.StoryCrafters.utils.Mappings;
import es.asun.StoryCrafters.utils.Validadores;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.util.*;
import java.util.stream.Collectors;

import static es.asun.StoryCrafters.utils.Constantes.*;

/**
 * Implementación de la interfaz `GrupoService` que gestiona la lógica de negocio relacionada con los grupos.
 */
@Service
public class GrupoServiceImpl implements GrupoService {

    @Autowired
    private UserService userService;

    @Autowired
    private GrupoRepository grupoRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private CategoriaService categoriaService;

    @Autowired
    private RelatoGrupoService relatoGrupoService;

    /**
     * Guarda un grupo en la base de datos.
     * @param grupo El grupo a guardar.
     */
    @Override
    public void guardarGrupo(Grupo grupo) {
        grupoRepository.save(grupo);
    }

    /**
     * Crea un nuevo grupo a partir de los datos proporcionados.
     * @param grupoDto Los datos del grupo a crear.
     */
    @Override
    public void crearGrupo(GrupoDto grupoDto) {
        try {
            Usuario usuario = AuthUtils.getAuthUser(userService);
            Grupo grupo;

            List<Usuario> listaUsuarios = new ArrayList<>();
            listaUsuarios.add(usuario);

            String codigoAcceso = CodigoIngresoGenerator.generarCodigoIngreso(this);

            grupo = Mappings.mapToGrupo(grupoDto);
            grupo.setUsuario(usuario);
            grupo.setUsuarios(listaUsuarios);
            grupo.setCodigoAcceso(codigoAcceso);
            grupoRepository.save(grupo);
        } catch (UsuarioException e) {
            throw new RuntimeException(e);
        }


    }

    /**
     * Actualiza los datos de un grupo existente.
     * @param idGrupo El ID del grupo a actualizar.
     * @param grupoDto Los nuevos datos del grupo.
     * @return La dirección de redirección después de actualizar el grupo.
     * @throws InvalidGrupoException Si el grupo es inválido o no se tiene acceso.
     * @throws UnauthorizedAccessException Si se intenta acceder de forma no autorizada.
     * @throws GrupoException Si ocurre un error relacionado con el grupo.
     */
    @Override
    public String actualizarGrupo(String idGrupo, GrupoDto grupoDto) throws InvalidGrupoException, UnauthorizedAccessException, GrupoException, UsuarioException {
        int id = Integer.parseInt(idGrupo);
        String redirect = "redirect:/grupos/";
        Usuario usuario = AuthUtils.getAuthUser(userService);

        Grupo grupo = this.findGrupoById(id);

        if (!Validadores.gestorValido(grupo, usuario)) {
            throw new InvalidGrupoException("Invalid group or unauthorized access");
        }

        grupo.setNombre(grupoDto.getNombre());
        grupo.setDescripcion(grupoDto.getDescripcion());
        this.guardarGrupo(grupo);

        return redirect;
    }

    /**
     * Recupera todos los grupos asociados a un usuario.
     * @param usuario El usuario del que se desean recuperar los grupos.
     * @return Una lista de todos los grupos asociados al usuario.
     */
    @Override
    public List<Grupo> findAllGruposByUsuario(Usuario usuario) {

        return grupoRepository.findAllByUsuario(usuario);
    }

    /**
     * Elimina un grupo por su ID.
     * @param idGrupo El ID del grupo a eliminar.
     */
    @Override
    public void deleteGrupoById(int idGrupo) {
        grupoRepository.deleteById(idGrupo);
    }

    /**
     * Busca un grupo por su ID.
     * @param grupoId El ID del grupo a buscar.
     * @return El grupo encontrado.
     * @throws GrupoException Si el grupo no se encuentra.
     */
    @Override
    public Grupo findGrupoById(int grupoId) throws GrupoException {
        Optional<Grupo> grupoOptional = grupoRepository.findById(grupoId);
        if (grupoOptional.isEmpty()) {
            throw new GrupoException("Grupo no encontrado");
        }
        return grupoOptional.get();
    }

    /**
     * Busca un grupo por su código de acceso.
     * @param codigoAcceso El código de acceso del grupo a buscar.
     * @return El grupo encontrado.
     */
    @Override
    public Optional<Grupo> findGrupoByCodigoAcceso(String codigoAcceso) {
        return grupoRepository.findByCodigoAcceso(codigoAcceso);
    }

    /**
     * Elimina un grupo por su ID.
     * @param id El ID del grupo a eliminar.
     */
    @Override
    public void eliminarGrupo(int id) {
        this.deleteGrupoById(id);
    }

    /**
     * Envía una invitación para unirse a un grupo.
     * @param idGrupo El ID del grupo al que se invita.
     * @param email El correo electrónico de la persona a invitar.
     * @throws GrupoException Si ocurre un error relacionado con el grupo.
     * @throws UserAlreadyExistsException Si el usuario ya es miembro del grupo.
     */
    @Override
    public void enviarInvitacion(int idGrupo, String email) throws GrupoException, UserAlreadyExistsException, UsuarioException {
        Usuario usuario = AuthUtils.getAuthUser(userService);
        Grupo grupo = this.findGrupoById(idGrupo);

        if (!Validadores.gestorValido(grupo, usuario)) {
            throw new UnauthorizedAccessException("Acceso Denegado");
        }

        Optional<Usuario> usuarioGrupoOptional = userService.findUserByEmail(email);

        if (usuarioGrupoOptional.isPresent()) {
            Optional<Grupo> grupoOptional = grupoRepository.findGrupoByIdAndUsuariosContains(grupo.getId(), usuarioGrupoOptional.get());

            if (grupoOptional.isPresent()) {
                throw new UserAlreadyExistsException("Este usuario ya existe en el grupo");
            }
        }
        emailService.enviarInvitacion(email, grupo.getCodigoAcceso(), grupo.getDescripcion(), grupo.getNombre(), usuario.getFirstName());
    }

    /**
     * Permite a un usuario abandonar un grupo.
     * @param usuario El usuario que desea abandonar el grupo.
     * @param grupoId El ID del grupo que se desea abandonar.
     * @throws GrupoException Si ocurre un error relacionado con el grupo.
     */
    @Override
    public void abandonarGrupo(Usuario usuario, String grupoId) throws GrupoException {
        int idGrupo = Integer.parseInt(grupoId);
        Grupo grupo = this.findGrupoById(idGrupo);

        if (!grupo.getUsuarios().contains(usuario)) {
            throw new GrupoException("El usuario no es miembro del grupo");
        }

        grupo.getUsuarios().remove(usuario);
        this.guardarGrupo(grupo);
    }

    /**
     * Muestra una vista específica dependiendo de la opción seleccionada.
     * @param grupo El grupo asociado a la vista.
     * @param opcion La opción seleccionada.
     * @param model El modelo de la vista.
     * @param usuario El usuario que realiza la acción.
     * @throws GrupoException Si la opción no es válida.
     */
    @Override
    public void mostrarVista(Grupo grupo, String opcion, Model model, Usuario usuario) throws GrupoException {
        List<RelatoGrupo> relatosAprobados;
        switch (opcion) {
            case "publicaciones":
                List<Categoria> listaCategorias = categoriaService.findAllCategories();
                model.addAttribute("listaCategorias", listaCategorias);

                relatosAprobados = relatoGrupoService.buscarRelatosGrupoPorGrupoYEstado(grupo, ESTADO_APROBADO);

                model.addAttribute("listaRelatosAprobados", relatosAprobados);
                model.addAttribute("content", "views/grupos/ver-grupo");
                break;
            case "usuarios":
                Map<Integer, Long> contadorRelatosPorUsuario = relatoGrupoService.contarRelatosAprobadosPorUsuarioEnGrupo(grupo);

                model.addAttribute("contadorRelatosPorUsuario", contadorRelatosPorUsuario);
                model.addAttribute("content", "views/grupos/grupo-usuarios");
                break;
            case "gestionar-relatos":
                List<RelatoGrupo> relatos = relatoGrupoService.findRelatoGrupoByGrupoIs(grupo);

                List<RelatoGrupo> relatosRechazados = new ArrayList<>();
                relatosAprobados = new ArrayList<>();
                List<RelatoGrupo> relatosPendientes = new ArrayList<>();

                for (RelatoGrupo relato : relatos) {
                    switch (relato.getEstado()) {
                        case ESTADO_RECHAZADO:
                            relatosRechazados.add(relato);
                            break;
                        case ESTADO_APROBADO:
                            relatosAprobados.add(relato);
                            break;
                        case ESTADO_PENDIENTE:
                            relatosPendientes.add(relato);
                            break;
                    }
                }

                model.addAttribute("listaRelatosPendientes", relatosPendientes);
                model.addAttribute("listaRelatosAprobados", relatosAprobados);
                model.addAttribute("listaRelatosRechazados", relatosRechazados);
                model.addAttribute("content", "views/grupos/gestionar-relatos-grupo");
                break;
            case "solicitudes":

                model.addAttribute("content", "views/grupos/grupo-solicitudes");
                break;
            default:
                throw new GrupoException("Opción no válida");
        }
    }

    /**
     * Muestra los relatos pertenecientes a un usuario en particular dentro de un grupo.
     * @param grupo El grupo al que pertenecen los relatos.
     * @param usuarioActual El usuario cuyos relatos se desean mostrar.
     * @param model El modelo de la vista.
     */
    @Override
    public void verMisRelatosGrupo(Grupo grupo, Usuario usuarioActual, Model model) {
        List<RelatoGrupo> listaRelatosGrupo = relatoGrupoService.findRelatoGrupoByGrupoIs(grupo);

        List<RelatoGrupo> listaRelatosUsuario = listaRelatosGrupo.stream()
                .filter(relato -> relato.getRelato().getUsuario().equals(usuarioActual))
                .collect(Collectors.toList());

        model.addAttribute("listaRelatosUsuario", listaRelatosUsuario);
        model.addAttribute("content", "views/grupos/grupo-mis-relatos");
    }

    /**
     * Verifica si un código de acceso para un grupo existe en la base de datos.
     * @param codigoAcceso El código de acceso a verificar.
     * @return `true` si el código de acceso existe, de lo contrario `false`.
     */
    @Override
    public boolean existeCodigoAcceso(String codigoAcceso) {
        return grupoRepository.existsByCodigoAcceso(codigoAcceso);
    }

    /**
     * Encuentra todos los grupos que contienen a un usuario específico.
     * @param usuario El usuario cuyos grupos se desean encontrar.
     * @return Una lista de todos los grupos que contienen al usuario.
     */
    @Override
    public List<Grupo> encontrarGruposContieneUsuario(Usuario usuario) {
        return grupoRepository.findByUsuariosContains(usuario);
    }

}
