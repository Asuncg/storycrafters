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
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

import static es.asun.StoryCrafters.utilidades.Constantes.*;

@Controller
@RequestMapping("/grupos")
public class GruposController {

    private final GrupoService grupoService;
    private final EmailService emailService;
    private final RelatoGrupoService relatoGrupoService;
    private final UserService userService;
    private final SolicitudService solicitudService;
    private final EstadisticasService estadisticasService;
    private final CategoriaService categoriaService;

    private static final String ERROR_VIEW = "views/error/error";
    private static final String INDEX_VIEW = "index";

    @Autowired
    public GruposController(GrupoService grupoService, EmailService emailService, RelatoGrupoService relatoGrupoService,
                            UserService userService, SolicitudService solicitudService, EstadisticasService estadisticasService,
                            CategoriaService categoriaService) {
        this.grupoService = grupoService;
        this.emailService = emailService;
        this.relatoGrupoService = relatoGrupoService;
        this.userService = userService;
        this.solicitudService = solicitudService;
        this.estadisticasService = estadisticasService;
        this.categoriaService = categoriaService;
    }

    @GetMapping("/")
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

        return "redirect:/grupos/";
    }

    @GetMapping("/eliminar-grupo/{id}")
    public String eliminarGrupo(Model model, @PathVariable String id) {
        int idGrupo = Integer.parseInt(id);
        Usuario usuario = AuthUtils.getAuthUser(userService);
        Optional<Grupo> grupoOptional = grupoService.findGrupoById(idGrupo);

        if (!grupoYGestorValido(grupoOptional, usuario)) {
            model.addAttribute("content", ERROR_VIEW);
            return INDEX_VIEW;
        }
        grupoService.deleteGrupoById(idGrupo);
        return "redirect:/grupos/";
    }

    @GetMapping("/editar-grupo/{id}")
    public String editarGrupo(Model model, @PathVariable String id) {
        int idGrupo = Integer.parseInt(id);
        Usuario usuario = AuthUtils.getAuthUser(userService);
        Optional<Grupo> grupoOptional = grupoService.findGrupoById(idGrupo);

        if (!grupoYGestorValido(grupoOptional, usuario)) {
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

        if (!grupoYGestorValido(grupoOptional, usuario)) {
            model.addAttribute("content", ERROR_VIEW);
            return INDEX_VIEW;
        }

        Grupo grupo = grupoOptional.get();

        grupo.setNombre(grupoDto.getNombre());
        grupo.setDescripcion(grupoDto.getDescripcion());
        grupoService.guardarGrupo(grupo);

        return "redirect:/grupos/";

    }

    @PostMapping("/invitar-usuarios")
    @ResponseBody
    public String invitarUsuarios(Model model, @RequestBody Map<String, Object> request) {

        int idGrupo = Integer.parseInt(request.get("groupId").toString());
        List<String> emails = (List<String>) request.get("emails");

        Usuario usuario = AuthUtils.getAuthUser(userService);
        Optional<Grupo> grupoOptional = grupoService.findGrupoById(idGrupo);

        if (!grupoYGestorValido(grupoOptional, usuario)) {
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
    @ResponseBody
    public ResponseEntity<Map<String, String>> ingresarInvitacion(@RequestParam("codigoInvitacion") String codigoInvitacion) {
        Map<String, String> response = new HashMap<>();
        Usuario usuario = AuthUtils.getAuthUser(userService);
        Optional<Grupo> grupoOptional = grupoService.findGrupoByCodigoAcceso(codigoInvitacion);

        if (grupoOptional.isEmpty()) {
            response.put("status", "error");
            response.put("message", "Hubo un error al intentar ingresar al grupo.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        Grupo grupo = grupoOptional.get();

        if (grupo.getUsuarios().contains(usuario)) {
            response.put("status", "error");
            response.put("message", "Ya eres miembro de este grupo.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        if (usuario.getFirmaAutor().isEmpty()) {
            response.put("status", "error");
            response.put("message", "Debes tener una firma de Autor para poder publicar tus obras antes de entrar a un grupo. Configúrala en tu perfil!");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        Optional<Solicitud> solicitudOptional = solicitudService.buscarSolicitud(grupo, usuario);

        if (solicitudOptional.isPresent()) {
            response.put("status", "error");
            response.put("message", "Ya has enviado una solicitud de ingreso para este grupo.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        Solicitud solicitud = new Solicitud();
        solicitud.setGrupo(grupo);
        solicitud.setUsuario(usuario);
        solicitudService.guardarSolicitud(solicitud);

        response.put("status", "success");
        response.put("message", "¡Te has unido al grupo con éxito! Espera a que el gestor acepte la solicitud.");
        return ResponseEntity.ok(response);
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

        return "redirect:/grupos/";
    }

    @GetMapping("/{grupoId}/{opcion}")
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
        prepararModeloBase(model, grupo);
        List<RelatoGrupo> listaRelatosGrupo;
        List<RelatoGrupo> relatosAprobados;
        switch (opcion) {
            case "publicaciones":
                List<Categoria> listaCategorias = categoriaService.findAllCategories();
                model.addAttribute("listaCategorias", listaCategorias);

                relatosAprobados = relatoGrupoService.buscarRelatosGrupo(grupo, ESTADO_APROBADO);

                model.addAttribute("listaRelatosAprobados", relatosAprobados);
                model.addAttribute("content", "views/grupos/ver-grupo");
                break;
            case "usuarios":
                listaRelatosGrupo = relatoGrupoService.findRelatoGrupoByGrupoIs(grupo);
                Map<Integer, Long> contadorRelatosPorUsuario = listaRelatosGrupo.stream()
                        .filter(relato -> relato.getEstado() == Constantes.ESTADO_APROBADO)
                        .collect(Collectors.groupingBy(relato -> relato.getRelato().getUsuario().getId(), Collectors.counting()));

                model.addAttribute("contadorRelatosPorUsuario", contadorRelatosPorUsuario);
                model.addAttribute("content", "views/grupos/grupo-usuarios");
                break;
            case "gestionar-relatos":
                List<RelatoGrupo> relatosRechazados = relatoGrupoService.buscarRelatosGrupo(grupo, ESTADO_RECHAZADO);
                relatosAprobados = relatoGrupoService.buscarRelatosGrupo(grupo, ESTADO_APROBADO);

                model.addAttribute("listaRelatosAprobados", relatosAprobados);
                model.addAttribute("listaRelatosRechazados", relatosRechazados);
                model.addAttribute("content", "views/grupos/gestionar-relatos-grupo");
                break;
            case "solicitudes":
                model.addAttribute("content", "views/grupos/grupo-solicitudes");
                break;
            default:
                model.addAttribute("content", ERROR_VIEW);
                break;
        }
        return INDEX_VIEW;
    }

    @GetMapping("/{grupoId}/mis-relatos")
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

                prepararModeloBase(model, grupo);

                model.addAttribute("listaRelatosUsuario", listaRelatosUsuario);
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

    @GetMapping("/{grupoId}/revisar-relato/{id}")
    public String mostrarFormularioAprobacion(Model model, @PathVariable String id, @PathVariable String grupoId) {
        int idRelatoGrupo = Integer.parseInt(id);

        Optional<RelatoGrupo> relatoGrupoOptional = relatoGrupoService.findRelatoGrupoById(idRelatoGrupo);
        RelatoGrupo relatoGrupo;

        if (relatoGrupoOptional.isPresent()) {
            relatoGrupo = relatoGrupoOptional.get();
            Optional<Grupo> grupoOptional = grupoService.findGrupoById(Integer.parseInt(grupoId));

            if (grupoOptional.isPresent()) {
                Grupo grupo = grupoOptional.get();
                RelatoGrupoGestionDto relatoGrupoGestionDto = new RelatoGrupoGestionDto();
                relatoGrupoGestionDto.setId(idRelatoGrupo);

                prepararModeloBase(model, grupo);

                model.addAttribute("content", "views/grupos/formulario-aprobacion");
                model.addAttribute("relatoGrupoDto", relatoGrupoGestionDto);
                model.addAttribute("relatoGrupo", relatoGrupo);
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
            relatoGrupo.setFechaModificacion(new Date());
            relatoGrupoService.guardarRelatoGrupo(relatoGrupo);

            emailService.enviarNotificacion(relatoGrupo.getRelato().getUsuario().getEmail(), relatoGrupo.getFeedback(), relatoGrupo.getTitulo());

            return "redirect:/grupos/" + idGrupo + "/gestionar-relatos";
        } else {
            model.addAttribute("content", ERROR_VIEW);
            return INDEX_VIEW;
        }
    }

    @GetMapping("/{grupoId}/relato/{id}")
    public String verRelatoGrupo(Model model, @PathVariable String id,  @PathVariable String grupoId) {
        int idRelatoGrupo = Integer.parseInt(id);

        Usuario usuarioActual = AuthUtils.getAuthUser(userService);

        Optional<RelatoGrupo> relatoGrupoOptional = relatoGrupoService.findRelatoGrupoById(idRelatoGrupo);
        if (relatoGrupoOptional.isEmpty()) {
            model.addAttribute("content", ERROR_VIEW);
            return INDEX_VIEW;
        }

        RelatoGrupo relatoGrupo = relatoGrupoOptional.get();
        RelatoGrupoDto relatoGrupoDto = Mappings.mapToRelatoGrupoDto(relatoGrupo);

        Optional<Grupo> grupoOptional = grupoService.findGrupoById(Integer.parseInt(grupoId));
        if (grupoOptional.isEmpty()) {
            model.addAttribute("content", ERROR_VIEW);
            return INDEX_VIEW;
        }

        Grupo grupo = grupoOptional.get();

        if (!grupo.getUsuarios().contains(usuarioActual)) {
            model.addAttribute("content", ERROR_VIEW);
            return INDEX_VIEW;
        }

        prepararModeloBase(model, grupo);

        model.addAttribute("content", "views/grupos/vista-relato-grupo");
        model.addAttribute("relatoGrupo", relatoGrupoDto);
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

        Optional<Grupo> grupoOptional = grupoService.findGrupoById(relatoGrupo.getGrupo().getId());
        if (grupoOptional.isEmpty()) {
            model.addAttribute("content", ERROR_VIEW);
            return INDEX_VIEW;
        }
        Grupo grupo = grupoOptional.get();

        prepararModeloBase(model, grupo);

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

        return "redirect:/grupos/" + grupo.getId() + "/solicitudes";
    }

    @GetMapping("/rechazar-solicitud/{solicitudId}")
    public String rechazarSolicitudGrupo(@PathVariable String solicitudId) {

        Solicitud solicitud = solicitudService.buscarSolicitudPorId(Integer.parseInt(solicitudId));
        solicitudService.eliminarSolicitud(solicitud);

        return "redirect:/grupos/" + solicitud.getGrupo().getId() + "/solicitudes";
    }

    @PostMapping("/gestionar-solicitudes")
    public String gestionarSolicitudes(@RequestParam("grupoId") int grupoId,
                                       @RequestParam("accion") String accion,
                                       @RequestParam("solicitudIds") List<Integer> solicitudIds,
                                       Model model) {
        Usuario usuario = AuthUtils.getAuthUser(userService);

        Optional<Grupo> grupoOptional = grupoService.findGrupoById(grupoId);
        if (!grupoYGestorValido(grupoOptional, usuario)) {
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

        return "redirect:/grupos/" + grupoId + "/solicitudes";
    }

    @GetMapping("/estadisticas-grupo/{grupoId}")
    public String verEstadisticasGrupo(@PathVariable("grupoId") String grupoId, Model model) {
        Usuario usuario = AuthUtils.getAuthUser(userService);

        Optional<Grupo> grupoOptional = grupoService.findGrupoById(Integer.parseInt(grupoId));

        if (!grupoYGestorValido(grupoOptional, usuario)) {
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

        if (!grupoYGestorValido(grupoOptional, usuarioGestor)) {
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

        return "redirect:/grupos/" + grupoId + "/usuarios";
    }



    @GetMapping("/{grupoId}/usuario/{usuarioId}/{opcion}")
    public String verRelatosUsuarioGrupo(@PathVariable("grupoId") String grupoId, @PathVariable("usuarioId") String usuarioId, @PathVariable("opcion") String opcion, Model model) {
        Usuario usuarioActual = AuthUtils.getAuthUser(userService);

        Optional<Grupo> grupoOptional = grupoService.findGrupoById(Integer.parseInt(grupoId));
        Grupo grupo;

        if (grupoOptional.isPresent()) {

            grupo = grupoOptional.get();

            if (grupo.getUsuarios().contains(usuarioActual)) {
                List<RelatoGrupo> listaRelatosGrupo = relatoGrupoService.findRelatoGrupoByGrupoIs(grupo);

                Optional<Usuario> usuarioOptional = userService.findUserById(Integer.parseInt(usuarioId));

                if (usuarioOptional.isEmpty()) {
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
                } else if (opcion.equals("calificaciones")) {
                    model.addAttribute("content", "views/grupos/grupo-calificaciones-usuario");
                } else {
                    model.addAttribute("content", ERROR_VIEW);
                }

                prepararModeloBase(model, grupo);

                model.addAttribute("listaRelatosUsuario", listaRelatosUsuario);
                model.addAttribute("usuario", usuario);
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

    @GetMapping("/{grupoId}/eliminar-relato-grupo/{id}/{vista}")
    public String eliminarRelatoGrupo(Model model, @PathVariable String id, @PathVariable String vista) {
        Usuario usuario = AuthUtils.getAuthUser(userService);

        Optional<RelatoGrupo> relatoGrupoOptional = relatoGrupoService.findRelatoGrupoById(Integer.parseInt(id));

        if (relatoGrupoOptional.isEmpty()) {
            model.addAttribute("content", ERROR_VIEW);
            return INDEX_VIEW;
        }

        RelatoGrupo relatoGrupo = relatoGrupoOptional.get();

        if (!relatoGrupo.getRelato().getUsuario().equals(usuario) && !relatoGrupo.getGrupo().getUsuario().equals(usuario)) {
            model.addAttribute("content", ERROR_VIEW);
            return INDEX_VIEW;
        }


        relatoGrupoService.eliminarRelatoGrupo(Integer.parseInt(id));

        if (vista.equals("mis-relatos")) {
            return "redirect:/grupos/mis-relatos/" + relatoGrupo.getGrupo().getId();
        } else if (vista.equals("gestionar-relatos")) {
            return "redirect:/grupos/" + relatoGrupo.getGrupo().getId() + "/gestionar-relatos";
        } else {
            model.addAttribute("content", ERROR_VIEW);
            return INDEX_VIEW;
        }
    }


    private Boolean grupoYGestorValido(Optional<Grupo> grupoOptional, Usuario usuario) {
        if (grupoOptional.isEmpty()) {
            return false;
        }
        Grupo grupo = grupoOptional.get();
        return usuario.getId() == grupo.getUsuario().getId();
    }

    private void prepararModeloBase(Model model, Grupo grupo) {
        Usuario usuario = AuthUtils.getAuthUser(userService);

        List<Solicitud> solicitudesPendientes = solicitudService.buscarSolicitudesPorGrupo(grupo);
        List<RelatoGrupo> relatosPendientes = relatoGrupoService.buscarRelatosGrupo(grupo, ESTADO_PENDIENTE);

        model.addAttribute("grupo", grupo);
        model.addAttribute("idUsuarioActual", usuario.getId());
        model.addAttribute("listaRelatosPendientes", relatosPendientes);
        model.addAttribute("solicitudesPendientes", solicitudesPendientes);
    }
}

