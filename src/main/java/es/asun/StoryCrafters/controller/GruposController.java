package es.asun.StoryCrafters.controller;

import es.asun.StoryCrafters.entity.Grupo;
import es.asun.StoryCrafters.entity.RelatoGrupo;
import es.asun.StoryCrafters.entity.Solicitud;
import es.asun.StoryCrafters.entity.Usuario;
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

import static es.asun.StoryCrafters.utilidades.Constantes.ESTADO_APROBADO;
import static es.asun.StoryCrafters.utilidades.Constantes.ESTADO_RECHAZADO;

@Controller
@RequestMapping("/grupos")
public class GruposController {

    @Autowired
    private GrupoService grupoService;

    @Autowired
    private EmailService emailService;

    @Autowired
    RelatoGrupoService relatoGrupoService;

    @Autowired
    UserService userService;

    @Autowired
    SolicitudService solicitudService;

    @Autowired EstadisticasService estadisticasService;

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
            emailService.enviarInvitacion(email, grupo.getCodigoAcceso());
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
                // Manejar caso de opción no válida
                break;
        }
        return INDEX_VIEW;
    }

    @GetMapping("/aprobar-relato/{id}")
    public String mostrarFormularioAprobacion(Model model, @PathVariable String id) {
        int idRelatoGrupo = Integer.parseInt(id);

        Optional<RelatoGrupo> relatoGrupoOptional = relatoGrupoService.findRelatoGrupoById(idRelatoGrupo);
        RelatoGrupo relatoGrupo;

        if (relatoGrupoOptional.isPresent()) {

            relatoGrupo = relatoGrupoOptional.get();
            RelatoGrupoGestionDto relatoGrupoGestionDto = new RelatoGrupoGestionDto();
            relatoGrupoGestionDto.setId(idRelatoGrupo);

            model.addAttribute("content", "views/grupos/formulario-aprobacion");
            model.addAttribute("relatoGrupoDto", relatoGrupoGestionDto);
            model.addAttribute("relatoGrupo", relatoGrupo);
            return INDEX_VIEW;
        } else {
            model.addAttribute("content", ERROR_VIEW);
            return INDEX_VIEW;
        }
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

            return "redirect:/grupos/ver-grupo-gestor/" + idGrupo;
        } else {
            model.addAttribute("content", ERROR_VIEW);
            return INDEX_VIEW;
        }
    }

    @GetMapping("/ver-relato-grupo/{id}")
    public String verRelatoGrupo(Model model, @PathVariable String id) {
        int idRelatoGrupo = Integer.parseInt(id);

        Optional<RelatoGrupo> relatoGrupoOptional = relatoGrupoService.findRelatoGrupoById(idRelatoGrupo);

        if (relatoGrupoOptional.isPresent()) {
            RelatoGrupo relatoGrupo = relatoGrupoOptional.get();
            RelatoGrupoDto relatoGrupoDto = Mappings.mapToRelatoGrupoDto(relatoGrupo);

            model.addAttribute("content", "views/grupos/vista-relato-grupo");
            model.addAttribute("relatoGrupo", relatoGrupoDto);
            return INDEX_VIEW;
        } else {
            model.addAttribute("content", ERROR_VIEW);
            return INDEX_VIEW;
        }
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

        return "redirect:/grupos/ver-grupo-gestor/" + grupo.getId();
    }

    @GetMapping("/rechazar-solicitud/{solicitudId}")
    public String rechazarSolicitudGrupo(@PathVariable String solicitudId) {

        Solicitud solicitud = solicitudService.buscarSolicitudPorId(Integer.parseInt(solicitudId));
        solicitudService.eliminarSolicitud(solicitud);

        return "redirect:/grupos/ver-grupo-gestor/" + solicitud.getGrupo().getId();
    }

    @PostMapping("/gestionar-solicitudes")
    @ResponseBody
    public ResponseEntity<?> gestionarSolicitudes(@RequestBody Map<String, Object> solicitudData) {
        String grupoId = solicitudData.get("grupoId").toString();
        String accion = solicitudData.get("accion").toString();
        List<Integer> solicitudIds = convertirIdsSolicitud(solicitudData.get("solicitudIds"));

        Usuario usuario = AuthUtils.getAuthUser(userService);

        Optional<Grupo> grupoOptional = grupoService.findGrupoById(Integer.parseInt(grupoId));
        if (!grupoValido(grupoOptional, usuario)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Acceso denegado");
        }

        try {
            if (accion.equals("aceptar")) {
                solicitudService.aceptarSolicitudes(grupoId, solicitudIds);
            } else {
                solicitudService.eliminarSolicitudes(grupoId, solicitudIds);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al gestionar las solicitudes");
        }

        return ResponseEntity.ok("Solicitudes gestionadas correctamente");
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
    public ResponseEntity<?> eliminarUsuarioDelGrupo(@RequestBody Map<String, Object> solicitudData) {
        String grupoId = solicitudData.get("grupoId").toString();
        String usuarioId = solicitudData.get("usuarioId").toString();

        Usuario usuarioGestor = AuthUtils.getAuthUser(userService);
        Optional<Grupo> grupoOptional = grupoService.findGrupoById(Integer.parseInt(grupoId));

        if (!grupoValido(grupoOptional, usuarioGestor)) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al eliminar el usuario");
        }

        Optional<Usuario> usuarioASacarOptional = userService.findUserById(Integer.parseInt(usuarioId));
        if (usuarioASacarOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado");
        }

        Grupo grupo = grupoOptional.get();
        Usuario usuarioASacar = usuarioASacarOptional.get();

        // Remover el usuario del grupo
        grupo.getUsuarios().remove(usuarioASacar);
        grupoService.guardarGrupo(grupo);
        return ResponseEntity.ok("Usuario eliminado del grupo exitosamente.");
    }

    private Boolean grupoValido(Optional<Grupo> grupoOptional, Usuario usuario) {
        if (grupoOptional.isEmpty()) {
            return false;
        }
        Grupo grupo = grupoOptional.get();
        return usuario.getId() == grupo.getUsuario().getId();
    }

    private List<Integer> convertirIdsSolicitud(Object solicitudIdsObj) {
        List<Integer> solicitudIds = new ArrayList<>();

        if (solicitudIdsObj instanceof List<?>) {
            for (Object id : (List<?>) solicitudIdsObj) {
                if (id instanceof Integer) {
                    solicitudIds.add((Integer) id);
                } else if (id instanceof String) {
                    solicitudIds.add(Integer.parseInt((String) id));
                }
            }
        }

        return solicitudIds;
    }
    private void prepararModelo(Model model, Grupo grupo) {
        Usuario usuario = AuthUtils.getAuthUser(userService);

        List<Solicitud> solicitudesPendientes = solicitudService.buscarSolicitudesPorGrupo(grupo);

        List<RelatoGrupo> listaRelatosGrupo = relatoGrupoService.findRelatoGrupoByGrupoIs(grupo);
        List<RelatoGrupo> relatosPendientes = new ArrayList<>();
        List<RelatoGrupo> relatosAprobados = new ArrayList<>();
        List<RelatoGrupo> relatosRechazados = new ArrayList<>();

        for (RelatoGrupo relato : listaRelatosGrupo) {
            int estado = relato.getEstado();
            if (estado == Constantes.ESTADO_PENDIENTE) {
                relatosPendientes.add(relato);
            } else if (estado == ESTADO_APROBADO) {
                relatosAprobados.add(relato);
            } else if (estado == Constantes.ESTADO_RECHAZADO) {
                relatosRechazados.add(relato);
            }
        }

        model.addAttribute("grupo", grupo);
        model.addAttribute("idUsuarioActual", usuario.getId());
        model.addAttribute("listaRelatosPendientes", relatosPendientes);
        model.addAttribute("listaRelatosAprobados", relatosAprobados);
        model.addAttribute("listaRelatosRechazados", relatosRechazados);
        model.addAttribute("solicitudesPendientes", solicitudesPendientes);
    }
}

