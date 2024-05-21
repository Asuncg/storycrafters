package es.asun.StoryCrafters.controller;

import es.asun.StoryCrafters.entity.*;
import es.asun.StoryCrafters.model.EstadisticasDto;
import es.asun.StoryCrafters.model.GrupoDto;
import es.asun.StoryCrafters.model.RelatoGrupoDto;
import es.asun.StoryCrafters.model.RelatoGrupoGestionDto;
import es.asun.StoryCrafters.service.*;
import es.asun.StoryCrafters.utilidades.AuthUtils;
import es.asun.StoryCrafters.utilidades.CodigoIngresoGenerator;
import es.asun.StoryCrafters.utilidades.Constantes;
import es.asun.StoryCrafters.utilidades.Mappings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static es.asun.StoryCrafters.utilidades.Constantes.*;

@Controller
@RequestMapping("/grupos")
public class GruposController {

    @Autowired
    private GrupoService grupoService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private RelatoGrupoService relatoGrupoService;

    @Autowired
    private UserService userService;

    @Autowired
    private SolicitudService solicitudService;

    @Autowired
    private EstadisticasService estadisticasService;

    @Autowired
    private  CategoriaService categoriaService;

    private static final String ERROR_VIEW = "views/error/error";
    private static final String INDEX_VIEW = "index";

    @GetMapping("/mis-grupos")
    public String misGrupos(Model model) {
        Usuario usuario = AuthUtils.getAuthUser(userService);
        List<Grupo> grupos = grupoService.findAllGruposByUsuario(usuario);

        model.addAttribute("grupos", grupos);
        model.addAttribute("content", "views/grupos/mis-grupos");
        model.addAttribute("idUsuarioActual", usuario.getId());
        return INDEX_VIEW;
    }

    @GetMapping("/nuevo-grupo")
    public String nuevoGrupo(Model model) {
        model.addAttribute("content", "views/grupos/crear-grupo");
        model.addAttribute("grupoDto", new GrupoDto());
        return INDEX_VIEW;
    }

    @PostMapping("/crear-grupo")
    public String crearGrupo(@ModelAttribute("user") GrupoDto grupoDto) {
        Usuario usuario = AuthUtils.getAuthUser(userService);

        Grupo grupo;

        List<Usuario> listaUsuarios = new ArrayList<>();
        listaUsuarios.add(usuario);

        String codigoAcceso = CodigoIngresoGenerator.generarCodigoIngreso();

        grupo = Mappings.mapToGrupo(grupoDto);
        grupo.setUsuario(usuario);
        grupo.setUsuarios(listaUsuarios);
        grupo.setCodigoAcceso(codigoAcceso);

        grupoService.guardarGrupo(grupo);

        return "redirect:/grupos/mis-grupos";
    }


    @GetMapping("/eliminar-grupo/{id}")
    public String eliminarGrupo(Model model, @PathVariable String id) {
        int idGrupo = Integer.parseInt(id);
        Usuario usuario = AuthUtils.getAuthUser(userService);
        Optional<Grupo> grupoOptional = grupoService.findGrupoById(idGrupo);

        if (!grupoValido(grupoOptional, usuario)) {
            model.addAttribute("content", ERROR_VIEW);
            return INDEX_VIEW;
        }
        grupoService.deleteGrupoById(idGrupo);
        return "redirect:/grupos/mis-grupos";
    }


    @GetMapping("/editar-grupo/{id}")
    public String editarGrupo(Model model, @PathVariable String id) {
        int idGrupo = Integer.parseInt(id);
        Usuario usuario = AuthUtils.getAuthUser(userService);
        Optional<Grupo> grupoOptional = grupoService.findGrupoById(idGrupo);

        if (!grupoValido(grupoOptional, usuario)) {
            model.addAttribute("content", ERROR_VIEW);
            return INDEX_VIEW;
        }

        Grupo grupo = grupoOptional.get();

        GrupoDto grupoDto = new GrupoDto();
        model.addAttribute("grupo", grupo);
        model.addAttribute("grupoDto", grupoDto);
        model.addAttribute("content", "views/grupos/editar-grupo");

        return INDEX_VIEW;
    }

    @PostMapping("actualizar-grupo/{id}")
    public String actualizarGrupo(Model model, @PathVariable String id, GrupoDto grupoDto) {
        int idGrupo = Integer.parseInt(id);
        Usuario usuario = AuthUtils.getAuthUser(userService);
        Optional<Grupo> grupoOptional = grupoService.findGrupoById(idGrupo);

        if (!grupoValido(grupoOptional, usuario)) {
            model.addAttribute("content", ERROR_VIEW);
            return INDEX_VIEW;
        }

        Grupo grupo = grupoOptional.get();

        grupo.setNombre(grupoDto.getNombre());
        grupo.setDescripcion(grupoDto.getDescripcion());
        grupoService.guardarGrupo(grupo);

        return "redirect:/grupos/mis-grupos";

    }

    @PostMapping("/invitar-usuarios")
    @ResponseBody
    public String invitarUsuarios(Model model, @RequestBody Map<String, Object> request) {

        int idGrupo = Integer.parseInt(request.get("groupId").toString());
        List<String> emails = (List<String>) request.get("emails");

        Usuario usuario = AuthUtils.getAuthUser(userService);
        Optional<Grupo> grupoOptional = grupoService.findGrupoById(idGrupo);

        if (!grupoValido(grupoOptional, usuario)) {
            model.addAttribute("content", ERROR_VIEW);
            return INDEX_VIEW;
        }

        Grupo grupo = grupoOptional.get();

        for (String email : emails) {
            emailService.enviarInvitacion(email, grupo.getCodigoAcceso(), grupo.getDescripcion(), grupo.getNombre(), usuario.getFirstName());
        }

        return "Invitaciones enviadas correctamente.";
    }

    @PostMapping("/ingresar-invitacion")
    public String ingresarInvitacion(@RequestParam("codigoInvitacion") String codigoInvitacion, Model model) {
        Usuario usuario = AuthUtils.getAuthUser(userService);
        Optional<Grupo> grupoOptional = grupoService.findGrupoByCodigoAcceso(codigoInvitacion);

        if (grupoOptional.isEmpty()) {
            model.addAttribute("content", ERROR_VIEW);
            return INDEX_VIEW;
        }

        Grupo grupo = grupoOptional.get();

        if (grupo.getUsuarios().contains(usuario)) {
            model.addAttribute("mensaje", "Ya eres miembro de este grupo.");
            return "redirect:/grupos/mis-grupos";
        }

        Solicitud solicitud = new Solicitud();

        solicitud.setGrupo(grupo);
        solicitud.setUsuario(usuario);

        solicitudService.guardarSolicitud(solicitud);

        model.addAttribute("mensaje", "¡Te has unido al grupo con éxito!");
        return "redirect:/grupos/mis-grupos";
    }

    @PostMapping("/abandonar-grupo")
    public String abandonarGrupo(Model model, @RequestParam("grupoId") String grupoId) {
        int idGrupo = Integer.parseInt(grupoId);

        Usuario usuario = AuthUtils.getAuthUser(userService);

        Optional<Grupo> grupoOptional = grupoService.findGrupoById(idGrupo);
        if (grupoOptional.isEmpty()) {
            model.addAttribute("content", ERROR_VIEW);
            return INDEX_VIEW;
        }

        Grupo grupo = grupoOptional.get();

        if (!grupo.getUsuarios().contains(usuario)) {
            model.addAttribute("content", ERROR_VIEW);
            return INDEX_VIEW;
        }

        grupo.getUsuarios().remove(usuario);

        grupoService.guardarGrupo(grupo);

        return "redirect:/grupos/mis-grupos";
    }

    @GetMapping("/ver-grupo/{grupoId}/{opcion}")
    public String mostrarVista(@PathVariable("grupoId") String grupoId,
                               @PathVariable("opcion") String opcion,
                               Model model) {
        int idGrupo = Integer.parseInt(grupoId);
        Optional<Grupo> grupoOptional = grupoService.findGrupoById(idGrupo);
        if (grupoOptional.isEmpty()) {
            model.addAttribute("content", ERROR_VIEW);
            return INDEX_VIEW;
        }
        Grupo grupo = grupoOptional.get();
        prepararModelo(model, grupo);
        switch (opcion) {
            case "publicaciones":
                List<Categoria> listaCategorias = categoriaService.findAllCategories();
                model.addAttribute("listaCategorias", listaCategorias);
                model.addAttribute("content", "views/grupos/ver-grupo");
                break;
            case "usuarios":
                model.addAttribute("content", "views/grupos/grupo-usuarios");
                break;
            case "relatos-pendientes":
                model.addAttribute("content", "views/grupos/grupo-relatos-pendientes");
                break;
            case "solicitudes":
                model.addAttribute("content", "views/grupos/grupo-solicitudes");
                break;
            case "relatos-rechazados":
                model.addAttribute("content", "views/grupos/grupo-relatos-rechazados");
                break;
            default:
                model.addAttribute("content", ERROR_VIEW);
                break;
        }
        return INDEX_VIEW;
    }

    @GetMapping("/aprobar-relato/{id}")
    public String mostrarFormularioAprobacion(Model model, @PathVariable String id) {
        Usuario usuarioActual = AuthUtils.getAuthUser(userService);
        int idRelatoGrupo = Integer.parseInt(id);

        Optional<RelatoGrupo> relatoGrupoOptional = relatoGrupoService.findRelatoGrupoById(idRelatoGrupo);
        RelatoGrupo relatoGrupo;

        if (relatoGrupoOptional.isPresent()) {
            relatoGrupo = relatoGrupoOptional.get();
            Optional<Grupo> grupoOptional = grupoService.findGrupoById(relatoGrupo.getGrupo().getId());

            if (grupoOptional.isPresent()) {
                Grupo grupo = grupoOptional.get();
                RelatoGrupoGestionDto relatoGrupoGestionDto = new RelatoGrupoGestionDto();
                relatoGrupoGestionDto.setId(idRelatoGrupo);

                List<RelatoGrupo> listaRelatosGrupo = relatoGrupoService.findRelatoGrupoByGrupoIs(grupo);
                List<RelatoGrupo> relatosRechazados = listarRelatosEstado(listaRelatosGrupo, Constantes.ESTADO_RECHAZADO);
                List<RelatoGrupo> relatosPendientes = listarRelatosEstado(listaRelatosGrupo, ESTADO_PENDIENTE);

                model.addAttribute("listaRelatosPendientes", relatosPendientes);
                model.addAttribute("listaRelatosRechazados", relatosRechazados);
                model.addAttribute("content", "views/grupos/formulario-aprobacion");
                model.addAttribute("relatoGrupoDto", relatoGrupoGestionDto);
                model.addAttribute("relatoGrupo", relatoGrupo);
                model.addAttribute("grupo", grupo);
                model.addAttribute("idUsuarioActual", usuarioActual.getId());
                return INDEX_VIEW;
            } else {
                model.addAttribute("content", ERROR_VIEW);
                return INDEX_VIEW;
            }

        }
            model.addAttribute("content", ERROR_VIEW);
            return INDEX_VIEW;
    }

    @PostMapping("/gestionar-relato")
    public String gestionarRelatoGrupo(Model model, @ModelAttribute("relatoGrupo") RelatoGrupoGestionDto relatoGrupoGestionDto) {

        int idRelatoGrupo = relatoGrupoGestionDto.getId();

        Optional<RelatoGrupo> relatoGrupoOptional = relatoGrupoService.findRelatoGrupoById(idRelatoGrupo);

        if (relatoGrupoOptional.isPresent()) {
            RelatoGrupo relatoGrupo = relatoGrupoOptional.get();
            int idGrupo = relatoGrupo.getGrupo().getId();

            // Comprobar si se aprobó o se rechazó
            if (relatoGrupoGestionDto.isAprobado()) {
                relatoGrupo.setEstado(ESTADO_APROBADO);
            } else {
                relatoGrupo.setEstado(ESTADO_RECHAZADO);
            }

            relatoGrupo.setCalificacion(relatoGrupoGestionDto.getCalificacion());
            relatoGrupo.setFeedback(relatoGrupoGestionDto.getFeedback());

            relatoGrupoService.guardarRelatoGrupo(relatoGrupo);

            emailService.enviarNotificacion(relatoGrupo.getRelato().getUsuario().getEmail(), relatoGrupo.getFeedback(), relatoGrupo.getTitulo());

            return "redirect:/grupos/ver-grupo/" + idGrupo + "/relatos-pendientes";
        } else {
            model.addAttribute("content", ERROR_VIEW);
            return INDEX_VIEW;
        }
    }

    @GetMapping("/ver-relato-grupo/{id}")
    public String verRelatoGrupo(Model model, @PathVariable String id) {
        int idRelatoGrupo = Integer.parseInt(id);

        Usuario usuarioActual = AuthUtils.getAuthUser(userService);

        //Vlidar si existe el relato
        Optional<RelatoGrupo> relatoGrupoOptional = relatoGrupoService.findRelatoGrupoById(idRelatoGrupo);
        if (relatoGrupoOptional.isEmpty()) {
            model.addAttribute("content", ERROR_VIEW);
            return INDEX_VIEW;
        }

        RelatoGrupo relatoGrupo = relatoGrupoOptional.get();
        RelatoGrupoDto relatoGrupoDto = Mappings.mapToRelatoGrupoDto(relatoGrupo);

        //validar si existe el grupo
        Optional<Grupo> grupoOptional = grupoService.findGrupoById(relatoGrupo.getGrupo().getId());
        if (grupoOptional.isEmpty()) {
            model.addAttribute("content", ERROR_VIEW);
            return INDEX_VIEW;
        }
         Grupo grupo = grupoOptional.get();

        //validar que el usuario pertenezca al grupo para poder ver el relato
        if (!grupo.getUsuarios().contains(usuarioActual)) {
            model.addAttribute("content", ERROR_VIEW);
            return INDEX_VIEW;
        }
        List<RelatoGrupo> listaRelatosGrupo = relatoGrupoService.findRelatoGrupoByGrupoIs(grupo);
        List<RelatoGrupo> relatosRechazados = listarRelatosEstado(listaRelatosGrupo, Constantes.ESTADO_RECHAZADO);
        List<RelatoGrupo> relatosPendientes = listarRelatosEstado(listaRelatosGrupo, ESTADO_PENDIENTE);

        model.addAttribute("listaRelatosPendientes", relatosPendientes);
        model.addAttribute("listaRelatosRechazados", relatosRechazados);
        model.addAttribute("content", "views/grupos/vista-relato-grupo");
        model.addAttribute("relatoGrupo", relatoGrupoDto);
        model.addAttribute("grupo", grupo);
        model.addAttribute("idUsuarioActual", usuarioActual.getId());
        return INDEX_VIEW;
    }

    @GetMapping("/ver-relato-revisado/{relatoGrupoId}")
    public String verRelatoRevisado(@PathVariable("relatoGrupoId") String relatoGrupoId, Model model) {
        int idRelatoGrupo = Integer.parseInt(relatoGrupoId);

        Usuario usuario = AuthUtils.getAuthUser(userService);

        Optional<RelatoGrupo> relatoGrupoOptional = relatoGrupoService.findRelatoGrupoById(idRelatoGrupo);
        if (relatoGrupoOptional.isEmpty()) {
            model.addAttribute("content", ERROR_VIEW);
            return INDEX_VIEW;
        }
        RelatoGrupo relatoGrupo = relatoGrupoOptional.get();

        if (relatoGrupo.getRelato().getUsuario().getId() != usuario.getId()) {
            model.addAttribute("content", ERROR_VIEW);
            return INDEX_VIEW;
        }

        List<RelatoGrupo> listaRelatosGrupo = relatoGrupoService.findRelatoGrupoByGrupoIs(relatoGrupo.getGrupo());
        List<RelatoGrupo> relatosRechazados = listarRelatosEstado(listaRelatosGrupo, Constantes.ESTADO_RECHAZADO);
        List<RelatoGrupo> relatosPendientes = listarRelatosEstado(listaRelatosGrupo, ESTADO_PENDIENTE);

        model.addAttribute("listaRelatosPendientes", relatosPendientes);
        model.addAttribute("listaRelatosRechazados", relatosRechazados);
        model.addAttribute("relatoRevisado", relatoGrupo);
        model.addAttribute("content", "views/grupos/relato-revisado");
        return INDEX_VIEW;
    }

    @GetMapping("/aceptar-solicitud/{solicitudId}")
    public String aceptarSolicitudGrupo(@PathVariable String solicitudId) {

        Solicitud solicitud = solicitudService.buscarSolicitudPorId(Integer.parseInt(solicitudId));

        Usuario usuario = solicitud.getUsuario();

        Grupo grupo = solicitud.getGrupo();

        grupo.getUsuarios().add(usuario);

        grupoService.guardarGrupo(grupo);
        solicitudService.eliminarSolicitud(solicitud);

        return "redirect:/grupos/ver-grupo/" + grupo.getId() + "/solicitudes";
    }

    @GetMapping("/rechazar-solicitud/{solicitudId}")
    public String rechazarSolicitudGrupo(@PathVariable String solicitudId) {

        Solicitud solicitud = solicitudService.buscarSolicitudPorId(Integer.parseInt(solicitudId));
        solicitudService.eliminarSolicitud(solicitud);

        return "redirect:/grupos/ver-grupo/" + solicitud.getGrupo().getId() + "/solicitudes";
    }

    @PostMapping("/gestionar-solicitudes")
    public String gestionarSolicitudes(@RequestParam("grupoId") int grupoId,
                                       @RequestParam("accion") String accion,
                                       @RequestParam("solicitudIds") List<Integer> solicitudIds,
                                       Model model) {
        Usuario usuario = AuthUtils.getAuthUser(userService);

        Optional<Grupo> grupoOptional = grupoService.findGrupoById(grupoId);
        if (!grupoValido(grupoOptional, usuario)) {
            model.addAttribute("content", ERROR_VIEW);
            return INDEX_VIEW;
        }

        try {
            if ("aceptar".equals(accion)) {
                solicitudService.aceptarSolicitudes(String.valueOf(grupoId), solicitudIds);
            } else if ("rechazar".equals(accion)) {
                solicitudService.eliminarSolicitudes(String.valueOf(grupoId), solicitudIds);
            }
        } catch (Exception e) {
            model.addAttribute("content", ERROR_VIEW);
            return INDEX_VIEW;
        }

        return "redirect:/grupos/ver-grupo/" + grupoId + "/solicitudes";
    }

    @GetMapping("/estadisticas-grupo/{grupoId}")
    public String verEstadisticasGrupo(@PathVariable("grupoId") String grupoId, Model model) {
        Usuario usuario = AuthUtils.getAuthUser(userService);

        Optional<Grupo> grupoOptional = grupoService.findGrupoById(Integer.parseInt(grupoId));

        if (!grupoValido(grupoOptional, usuario)) {
            model.addAttribute("content", ERROR_VIEW);
            return INDEX_VIEW;
        }

        Grupo grupo = grupoOptional.get();
        List<RelatoGrupo> listaRelatosGrupo = relatoGrupoService.findRelatoGrupoByGrupoIs(grupo);
        List<RelatoGrupo> relatosAprobados = new ArrayList<>();
        for (RelatoGrupo relato : listaRelatosGrupo) {
            int estado = relato.getEstado();
            if (estado == ESTADO_APROBADO) {
                relatosAprobados.add(relato);
            }
        }

        EstadisticasDto estadisticasDto = estadisticasService.calcularEstadisticasGrupo(grupo, relatosAprobados);
        model.addAttribute("grupo", grupo);
        model.addAttribute("estadisticas", estadisticasDto);
        model.addAttribute("content", "views/grupos/estadisticas-grupo");
        return INDEX_VIEW;
    }

    @PostMapping("/eliminar-usuario")
    public String eliminarUsuarioDelGrupo(@RequestParam("grupoId") int grupoId,
                                          @RequestParam("usuarioId") int usuarioId,
                                          Model model) {
        Usuario usuarioGestor = AuthUtils.getAuthUser(userService);
        Optional<Grupo> grupoOptional = grupoService.findGrupoById(grupoId);

        if (!grupoValido(grupoOptional, usuarioGestor)) {
            model.addAttribute("content", "error");
            return "index";
        }

        Optional<Usuario> usuarioASacarOptional = userService.findUserById(usuarioId);
        if (usuarioASacarOptional.isEmpty()) {
            model.addAttribute("content", "error");
            return "index";
        }

        Grupo grupo = grupoOptional.get();
        Usuario usuarioASacar = usuarioASacarOptional.get();

        grupo.getUsuarios().remove(usuarioASacar);
        grupoService.guardarGrupo(grupo);

        return "redirect:/grupos/ver-grupo/" + grupoId + "/usuarios";
    }

    @GetMapping("/mis-relatos/{grupoId}")
    public String verMisRelatosGrupo(@PathVariable("grupoId") String grupoId, Model model) {
        Usuario usuarioActual = AuthUtils.getAuthUser(userService);

        Optional<Grupo> grupoOptional = grupoService.findGrupoById(Integer.parseInt(grupoId));
        Grupo grupo;

        if (grupoOptional.isPresent()) {

            grupo = grupoOptional.get();

            if (grupo.getUsuarios().contains(usuarioActual)) {
                List<RelatoGrupo> listaRelatosGrupo = relatoGrupoService.findRelatoGrupoByGrupoIs(grupo);

                List<RelatoGrupo> listaRelatosUsuario = listaRelatosGrupo.stream()
                        .filter(relato -> relato.getRelato().getUsuario().equals(usuarioActual))
                        .collect(Collectors.toList());

                List<RelatoGrupo> relatosRechazados = listarRelatosEstado(listaRelatosGrupo, Constantes.ESTADO_RECHAZADO);
                List<RelatoGrupo> relatosPendientes = listarRelatosEstado(listaRelatosGrupo, ESTADO_PENDIENTE);

                model.addAttribute("listaRelatosPendientes", relatosPendientes);
                model.addAttribute("listaRelatosRechazados", relatosRechazados);
                model.addAttribute("listaRelatosUsuario", listaRelatosUsuario);
                model.addAttribute("grupo", grupo);
                model.addAttribute("idUsuarioActual", usuarioActual.getId());
                model.addAttribute("content", "views/grupos/grupo-mis-relatos");
                return INDEX_VIEW;

            } else {
                model.addAttribute("content", ERROR_VIEW);
                return INDEX_VIEW;
            }
        } else {
            model.addAttribute("content", ERROR_VIEW);
            return INDEX_VIEW;
        }
    }

    @GetMapping("/relatos-usuario/{grupoId}/{usuarioId}/{opcion}")
    public String verRelatosUsuarioGrupo(@PathVariable("grupoId") String grupoId, @PathVariable("usuarioId") String usuarioId, @PathVariable("opcion") String opcion, Model model) {
        Usuario usuarioActual = AuthUtils.getAuthUser(userService);

        Optional<Grupo> grupoOptional = grupoService.findGrupoById(Integer.parseInt(grupoId));
        Grupo grupo;

        if (grupoOptional.isPresent()) {

            grupo = grupoOptional.get();

            if (grupo.getUsuarios().contains(usuarioActual)) {
                List<RelatoGrupo> listaRelatosGrupo = relatoGrupoService.findRelatoGrupoByGrupoIs(grupo);

                Optional<Usuario> usuarioOptional = userService.findUserById(Integer.parseInt(usuarioId));

                if (usuarioOptional.isEmpty() ) {
                    model.addAttribute("content", ERROR_VIEW);
                    return INDEX_VIEW;
                }

                Usuario usuario = usuarioOptional.get();

                List<RelatoGrupo> listaRelatosUsuario = listaRelatosGrupo.stream()
                        .filter(relato -> relato.getRelato().getUsuario().equals(usuario) &&
                                relato.getEstado() == ESTADO_APROBADO)
                        .collect(Collectors.toList());

                if (opcion.equals("relatos")) {
                    model.addAttribute("content", "views/grupos/grupo-relatos-usuario");
                } else if (opcion.equals("calificaciones")){
                    model.addAttribute("content", "views/grupos/grupo-calificaciones-usuario");
                } else {
                    model.addAttribute("content", ERROR_VIEW);
                }

                List<RelatoGrupo> relatosRechazados = listarRelatosEstado(listaRelatosGrupo, Constantes.ESTADO_RECHAZADO);
                List<RelatoGrupo> relatosPendientes = listarRelatosEstado(listaRelatosGrupo, ESTADO_PENDIENTE);

                model.addAttribute("listaRelatosPendientes", relatosPendientes);
                model.addAttribute("listaRelatosRechazados", relatosRechazados);
                model.addAttribute("listaRelatosUsuario", listaRelatosUsuario);
                model.addAttribute("usuario", usuario);
                model.addAttribute("grupo", grupo);
                model.addAttribute("idUsuarioActual", usuarioActual.getId());
                return INDEX_VIEW;

            } else {
                model.addAttribute("content", ERROR_VIEW);
                return INDEX_VIEW;
            }
        } else {
            model.addAttribute("content", ERROR_VIEW);
            return INDEX_VIEW;
        }
    }

    private Boolean grupoValido(Optional<Grupo> grupoOptional, Usuario usuario) {
        if (grupoOptional.isEmpty()) {
            return false;
        }
        Grupo grupo = grupoOptional.get();
        return usuario.getId() == grupo.getUsuario().getId();
    }

    private void prepararModelo(Model model, Grupo grupo) {
        Usuario usuario = AuthUtils.getAuthUser(userService);

        List<Solicitud> solicitudesPendientes = solicitudService.buscarSolicitudesPorGrupo(grupo);

        List<RelatoGrupo> listaRelatosGrupo = relatoGrupoService.findRelatoGrupoByGrupoIs(grupo);
        List<RelatoGrupo> relatosAprobados = listarRelatosEstado(listaRelatosGrupo, Constantes.ESTADO_APROBADO);
        List<RelatoGrupo> relatosRechazados = listarRelatosEstado(listaRelatosGrupo, Constantes.ESTADO_RECHAZADO);
        List<RelatoGrupo> relatosPendientes = listarRelatosEstado(listaRelatosGrupo, ESTADO_PENDIENTE);


        Map<Integer, Long> contadorRelatosPorUsuario = listaRelatosGrupo.stream()
                .filter(relato -> relato.getEstado() == Constantes.ESTADO_APROBADO)
                .collect(Collectors.groupingBy(relato -> relato.getRelato().getUsuario().getId(), Collectors.counting()));

        model.addAttribute("grupo", grupo);
        model.addAttribute("idUsuarioActual", usuario.getId());
        model.addAttribute("listaRelatosPendientes", relatosPendientes);
        model.addAttribute("listaRelatosAprobados", relatosAprobados);
        model.addAttribute("listaRelatosRechazados", relatosRechazados);
        model.addAttribute("solicitudesPendientes", solicitudesPendientes);
        model.addAttribute("contadorRelatosPorUsuario", contadorRelatosPorUsuario);

    }

    public List<RelatoGrupo> listarRelatosEstado(List<RelatoGrupo> listaRelatosGrupo, int estado) {
        List<RelatoGrupo> relatosFiltrados = new ArrayList<>();
        for (RelatoGrupo relato : listaRelatosGrupo) {
            if (relato.getEstado() == estado) {
                relatosFiltrados.add(relato);
            }
        }
        return relatosFiltrados;
    }

}

