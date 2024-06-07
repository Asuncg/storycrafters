package es.asun.StoryCrafters.controller;

import es.asun.StoryCrafters.entity.*;
import es.asun.StoryCrafters.exceptions.*;
import es.asun.StoryCrafters.model.EstadisticasDto;
import es.asun.StoryCrafters.model.GrupoDto;
import es.asun.StoryCrafters.model.RelatoGrupoDto;
import es.asun.StoryCrafters.model.RelatoGrupoGestionDto;
import es.asun.StoryCrafters.service.*;
import es.asun.StoryCrafters.utils.AuthUtils;
import es.asun.StoryCrafters.utils.Validadores;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

import static es.asun.StoryCrafters.utils.Constantes.*;

@Controller
@RequestMapping("/grupos")
public class GruposController {

    @Autowired
    private GrupoService grupoService;

    @Autowired
    private RelatoGrupoService relatoGrupoService;

    @Autowired
    private UserService userService;

    @Autowired
    private SolicitudService solicitudService;

    @Autowired
    private EstadisticasService estadisticasService;

    @GetMapping("/")
    public String misGrupos(Model model) {
        try {
            Usuario usuario = AuthUtils.getAuthUser(userService);
            List<Grupo> grupos = grupoService.findAllGruposByUsuario(usuario);

            model.addAttribute("grupos", grupos);
            model.addAttribute("content", "views/grupos/mis-grupos");
            model.addAttribute("currentPage", "grupos");
            model.addAttribute("idUsuarioActual", usuario.getId());
            return INDEX_VIEW;

        } catch (UsuarioException e) {
            model.addAttribute("content", ERROR_VIEW);
            return INDEX_VIEW;
        }


    }

    @GetMapping("/nuevo-grupo")
    public String nuevoGrupo(Model model) {
        model.addAttribute("content", "views/grupos/crear-grupo");
        model.addAttribute("grupoDto", new GrupoDto());
        return INDEX_VIEW;
    }

    @PostMapping("/crear-grupo")
    public String crearGrupo(@ModelAttribute("user") GrupoDto grupoDto) {
        grupoService.crearGrupo(grupoDto);
        return "redirect:/grupos/";
    }

    @GetMapping("/eliminar-grupo/{id}")
    public String eliminarGrupo(Model model, @PathVariable String id) {
        if (!Validadores.validateId(id)) {
            model.addAttribute("content", ERROR_VIEW);
            return INDEX_VIEW;
        }

        try {
            Usuario usuario = AuthUtils.getAuthUser(userService);
            Grupo grupo = grupoService.findGrupoById(Integer.parseInt(id));

            if (!Validadores.gestorValido(grupo, usuario)) {
                model.addAttribute("content", "views/error/error");
                return INDEX_VIEW;
            }

            grupoService.eliminarGrupo(Integer.parseInt(id));

            return "redirect:/grupos/";
        } catch (GrupoException | UsuarioException e) {
            model.addAttribute("content", ERROR_VIEW);
            return INDEX_VIEW;
        }
    }

    @GetMapping("/editar-grupo/{id}")
    public String editarGrupo(Model model, @PathVariable String id) {
        if (!Validadores.validateId(id)) {
            model.addAttribute("content", ERROR_VIEW);
            return INDEX_VIEW;
        }

        try {
            Grupo grupo = grupoService.findGrupoById(Integer.parseInt(id));
            Usuario usuario = AuthUtils.getAuthUser(userService);

            if (!Validadores.gestorValido(grupo, usuario)) {
                model.addAttribute("content", ERROR_VIEW);
                return INDEX_VIEW;
            }

            GrupoDto grupoDto = new GrupoDto();
            model.addAttribute("grupo", grupo);
            model.addAttribute("grupoDto", grupoDto);
            model.addAttribute("content", "views/grupos/editar-grupo");

            return INDEX_VIEW;
        } catch (NumberFormatException | GrupoException | UsuarioException e) {
            model.addAttribute("content", ERROR_VIEW);
            return INDEX_VIEW;
        }
    }

    @PostMapping("actualizar-grupo/{id}")
    public String actualizarGrupo(Model model, @PathVariable String id, GrupoDto grupoDto) {
        if (!Validadores.validateId(id)) {
            model.addAttribute("content", ERROR_VIEW);
            return INDEX_VIEW;
        }

        try {
            grupoService.actualizarGrupo(id, grupoDto);
            return "redirect:/grupos/";
        } catch (InvalidGrupoException | UnauthorizedAccessException | GrupoException | UsuarioException e) {
            model.addAttribute("content", ERROR_VIEW);
            return INDEX_VIEW;
        }
    }

    @PostMapping("/invitar-usuarios")
    @ResponseBody
    public ResponseEntity<String> invitarUsuario(@RequestParam("groupId") int groupId, @RequestParam("email") String email) {
        if (!Validadores.isValidEmail(email)) {
            return new ResponseEntity<>("Formato de correo electrónico no válido", HttpStatus.BAD_REQUEST);
        }

        try {
            grupoService.enviarInvitacion(groupId, email);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (UserAlreadyExistsException e) {
            return new ResponseEntity<>("Ya hay un usuario con este correo en el grupo", HttpStatus.CONFLICT);
        } catch (UnauthorizedAccessException | GrupoException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (UsuarioException e) {
            throw new RuntimeException(e);
        }
    }


    @PostMapping("/ingresar-invitacion")
    @ResponseBody
    public ResponseEntity<Map<String, String>> ingresarInvitacion(@RequestParam("codigoInvitacion") String codigoInvitacion) {
        try {
            Usuario usuario = AuthUtils.getAuthUser(userService);

            solicitudService.ingresarInvitacion(usuario, codigoInvitacion);

            return new ResponseEntity<>(HttpStatus.OK);
        } catch (SolicitudException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (UsuarioException e) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        } catch (GrupoException e) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }

    @PostMapping("/abandonar-grupo")
    public String abandonarGrupo(Model model, @RequestParam("grupoId") String grupoId) {
        if (!Validadores.validateId(grupoId)) {
            model.addAttribute("content", ERROR_VIEW);
            return INDEX_VIEW;
        }

        try {
            Usuario usuario = AuthUtils.getAuthUser(userService);
            grupoService.abandonarGrupo(usuario, grupoId);
        } catch (GrupoException | UsuarioException e) {
            model.addAttribute("content", ERROR_VIEW);
            return INDEX_VIEW;
        }
        return "redirect:/grupos/";
    }

    @PostMapping("/eliminar-usuario-grupo")
    public String eliminarUsuarioGrupo(Model model, @RequestParam("grupoId") String grupoId, @RequestParam("usuarioId") String usuarioId) {
        if (!Validadores.validateId(grupoId) || !Validadores.validateId(usuarioId)) {
            model.addAttribute("content", ERROR_VIEW);
            return INDEX_VIEW;
        }

        try {
            Usuario usuario = userService.findUserById(Integer.parseInt(usuarioId));
            grupoService.abandonarGrupo(usuario, grupoId);
        } catch (GrupoException | UsuarioException e) {
            model.addAttribute("content", ERROR_VIEW);
            return INDEX_VIEW;
        }
        return "redirect:/grupos/" + grupoId + "/usuarios";    }

    @GetMapping("/{grupoId}/{opcion}")
    public String mostrarVista(@PathVariable("grupoId") String grupoId, @PathVariable("opcion") String opcion, Model model) {
        if (!Validadores.validateId(grupoId)) {
            model.addAttribute("content", ERROR_VIEW);
            return INDEX_VIEW;
        }

        try {
            Grupo grupo = grupoService.findGrupoById(Integer.parseInt(grupoId));

            Usuario usuario = AuthUtils.getAuthUser(userService);

            prepararModeloBase(model, grupo);

            if (opcion.equals("solicitudes")) {
                List<Solicitud> solicitudesPendientes = solicitudService.buscarSolicitudesPorGrupo(grupo);

                model.addAttribute("solicitudesPendientes", solicitudesPendientes);
            }

            grupoService.mostrarVista(grupo, opcion, model, usuario);
        } catch (NumberFormatException | GrupoException | UsuarioException e) {
            model.addAttribute("content", ERROR_VIEW);
            return INDEX_VIEW;
        }
        return INDEX_VIEW;
    }

    @GetMapping("/{grupoId}/mis-relatos")
    public String verMisRelatosGrupo(@PathVariable("grupoId") String grupoId, Model model) {
        if (!Validadores.validateId(grupoId)) {
            model.addAttribute("content", ERROR_VIEW);
            return INDEX_VIEW;
        }

        try {
            Usuario usuarioActual = AuthUtils.getAuthUser(userService);
            Grupo grupo = grupoService.findGrupoById(Integer.parseInt(grupoId));

            if (grupo.getUsuarios().contains(usuarioActual)) {
                prepararModeloBase(model, grupo);
                grupoService.verMisRelatosGrupo(grupo, usuarioActual, model);
                return INDEX_VIEW;
            } else {
                model.addAttribute("content", ERROR_VIEW);
                return INDEX_VIEW;
            }
        } catch (GrupoException | UsuarioException e) {
            model.addAttribute("content", ERROR_VIEW);
            return INDEX_VIEW;
        }
    }

    @GetMapping("/{grupoId}/revisar-relato/{id}")
    public String mostrarFormularioAprobacion(Model model, @PathVariable String id, @PathVariable String grupoId) {
        if (!Validadores.validateId(id) || !Validadores.validateId(grupoId)) {
            model.addAttribute("content", ERROR_VIEW);
            return INDEX_VIEW;
        }

        try {
            Optional<RelatoGrupo> relatoGrupoOptional = relatoGrupoService.findRelatoGrupoById(Integer.parseInt(id));
            if (relatoGrupoOptional.isPresent()) {
                RelatoGrupo relatoGrupo = relatoGrupoOptional.get();
                Grupo grupo = grupoService.findGrupoById(Integer.parseInt(grupoId));

                RelatoGrupoGestionDto relatoGrupoGestionDto = new RelatoGrupoGestionDto();
                relatoGrupoGestionDto.setId(Integer.parseInt(id));

                prepararModeloBase(model, grupo);

                model.addAttribute("content", "views/grupos/formulario-aprobacion");
                model.addAttribute("relatoGrupoDto", relatoGrupoGestionDto);
                model.addAttribute("relatoGrupo", relatoGrupo);
            } else {
                model.addAttribute("content", ERROR_VIEW);
            }
            return INDEX_VIEW;
        } catch (NumberFormatException | GrupoException | UsuarioException e) {
            model.addAttribute("content", ERROR_VIEW);
            return INDEX_VIEW;
        }
    }

    @PostMapping("/gestionar-relato")
    public String gestionarRelatoGrupo(Model model, @ModelAttribute("relatoGrupo") RelatoGrupoGestionDto relatoGrupoGestionDto) {
        try {
            relatoGrupoService.gestionarRelato(relatoGrupoGestionDto);

            return "redirect:/grupos/" + relatoGrupoGestionDto.getIdGrupo() + "/gestionar-relatos";
        } catch (RelatoGrupoException e) {
            model.addAttribute("content", ERROR_VIEW);
            return INDEX_VIEW;
        }
    }

    @GetMapping("/{grupoId}/relato/{id}")
    public String verRelatoGrupo(Model model, @PathVariable String id, @PathVariable String grupoId) {
        if (!Validadores.validateId(id) || !Validadores.validateId(grupoId)) {
            model.addAttribute("content", ERROR_VIEW);
            return INDEX_VIEW;
        }

        try {
            RelatoGrupoDto relatoGrupoDto = relatoGrupoService.encontrarRelatoGrupoPorId(Integer.parseInt(id));
            Grupo grupo = grupoService.findGrupoById(Integer.parseInt(grupoId));

            if (!grupo.getUsuarios().contains(AuthUtils.getAuthUser(userService))) {
                model.addAttribute("content", ERROR_VIEW);
                return INDEX_VIEW;
            }

            prepararModeloBase(model, grupo);

            model.addAttribute("content", "views/grupos/vista-relato-grupo");
            model.addAttribute("relatoGrupo", relatoGrupoDto);
            return INDEX_VIEW;
        } catch (NumberFormatException | GrupoException | UsuarioException e) {
            model.addAttribute("content", ERROR_VIEW);
            return INDEX_VIEW;
        }
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
    public String rechazarSolicitudGrupo(Model model, @PathVariable String solicitudId) {
        if (!Validadores.validateId(solicitudId)) {
            model.addAttribute("content", ERROR_VIEW);
            return INDEX_VIEW;
        }

        Solicitud solicitud = solicitudService.buscarSolicitudPorId(Integer.parseInt(solicitudId));
        solicitudService.eliminarSolicitud(solicitud);

        return "redirect:/grupos/" + solicitud.getGrupo().getId() + "/solicitudes";
    }

    @PostMapping("/gestionar-solicitudes")
    public String gestionarSolicitudes(@RequestParam("grupoId") int grupoId, @RequestParam("accion") String accion, @RequestParam("solicitudIds") List<Integer> solicitudIds,
                                       Model model) {
        try {
            Grupo grupo = grupoService.findGrupoById(grupoId);

            if (!Validadores.gestorValido(grupo, AuthUtils.getAuthUser(userService))) {
                model.addAttribute("content", ERROR_VIEW);
                return INDEX_VIEW;
            }

            if ("aceptar".equals(accion)) {
                solicitudService.aceptarSolicitudes(String.valueOf(grupoId), solicitudIds);
            } else if ("rechazar".equals(accion)) {
                solicitudService.eliminarSolicitudes(String.valueOf(grupoId), solicitudIds);
            }

        } catch (GrupoException | UsuarioException e) {
            model.addAttribute("content", ERROR_VIEW);
            return INDEX_VIEW;
        }

        return "redirect:/grupos/" + grupoId + "/solicitudes";
    }


    @GetMapping("/estadisticas-grupo/{grupoId}")
    public String verEstadisticasGrupo(@PathVariable("grupoId") String grupoId, Model model) {
        if (!Validadores.validateId(grupoId)) {
            model.addAttribute("content", ERROR_VIEW);
            return INDEX_VIEW;
        }

        try {
            Grupo grupo = grupoService.findGrupoById(Integer.parseInt(grupoId));

            if (!Validadores.gestorValido(grupo, AuthUtils.getAuthUser(userService))) {
                model.addAttribute("content", ERROR_VIEW);
                return INDEX_VIEW;
            }

            EstadisticasDto estadisticasDto = estadisticasService.calcularEstadisticasGrupo(grupo);

            model.addAttribute("grupo", grupo);
            model.addAttribute("estadisticas", estadisticasDto);
            model.addAttribute("content", "views/grupos/estadisticas-grupo");
            return INDEX_VIEW;
        } catch (NumberFormatException | GrupoException | UsuarioException e) {
            model.addAttribute("content", ERROR_VIEW);
            return INDEX_VIEW;
        }
    }

    @PostMapping("/eliminar-usuario")
    public String eliminarUsuarioDelGrupo(@RequestParam("grupoId") int grupoId,
                                          @RequestParam("usuarioId") int usuarioId,
                                          Model model) {
        try {
            Grupo grupo = grupoService.findGrupoById(grupoId);

            if (!Validadores.gestorValido(grupo, AuthUtils.getAuthUser(userService))) {
                model.addAttribute("content", "error");
                return "index";
            }

            grupo.getUsuarios().remove(userService.findUserById(usuarioId));
            grupoService.guardarGrupo(grupo);

            return "redirect:/grupos/" + grupoId + "/usuarios";
        } catch (GrupoException | UsuarioException e) {
            model.addAttribute("content", ERROR_VIEW);
            return INDEX_VIEW;
        }
    }

    @GetMapping("/{grupoId}/usuario/{usuarioId}/{opcion}")
    public String verRelatosUsuarioGrupo(@PathVariable("grupoId") String grupoId, @PathVariable("usuarioId") String usuarioId, @PathVariable("opcion") String opcion, Model model) {
        try {
            if (!Validadores.validateId(usuarioId) || !Validadores.validateId(grupoId)) {
                model.addAttribute("content", ERROR_VIEW);
                return INDEX_VIEW;
            }

            Grupo grupo = grupoService.findGrupoById(Integer.parseInt(grupoId));
            if (!grupo.getUsuarios().contains(AuthUtils.getAuthUser(userService))) {
                model.addAttribute("content", ERROR_VIEW);
                return INDEX_VIEW;
            }

            Usuario usuario = userService.findUserById(Integer.parseInt(usuarioId));

            List<RelatoGrupo> listaRelatosUsuario = relatoGrupoService.encontrarRelatosGrupoUsuario(grupo, Integer.parseInt(usuarioId));

            String content;
            if (opcion.equals("relatos")) {
                content = "views/grupos/grupo-relatos-usuario";
            } else if (opcion.equals("calificaciones")) {
                content = "views/grupos/grupo-calificaciones-usuario";
            } else {
                model.addAttribute("content", ERROR_VIEW);
                return INDEX_VIEW;
            }

            prepararModeloBase(model, grupo);
            model.addAttribute("listaRelatosUsuario", listaRelatosUsuario);
            model.addAttribute("usuario", usuario);
            model.addAttribute("content", content);
            return INDEX_VIEW;

        } catch (NumberFormatException | GrupoException | UsuarioException e) {
            model.addAttribute("content", ERROR_VIEW);
            return INDEX_VIEW;
        }
    }

    @GetMapping("/{grupoId}/eliminar-relato-grupo/{id}/{vista}")
    public String eliminarRelatoGrupo(Model model, @PathVariable String id, @PathVariable String vista) {
        try {
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
                return "redirect:/grupos/" + relatoGrupo.getGrupo().getId() + "/mis-relatos";
            } else if (vista.equals("gestionar-relatos")) {
                return "redirect:/grupos/" + relatoGrupo.getGrupo().getId() + "/gestionar-relatos";
            } else {
                model.addAttribute("content", ERROR_VIEW);
                return INDEX_VIEW;
            }
        } catch (UsuarioException e) {
            model.addAttribute("content", ERROR_VIEW);
            return INDEX_VIEW;
        }
    }

    private void prepararModeloBase(Model model, Grupo grupo) throws UsuarioException {
        Usuario usuario = AuthUtils.getAuthUser(userService);

        boolean existenSolicitudesPendientes = solicitudService.existenSolicitudesPendientes(grupo);
        boolean existenRelatosGruposPendientes = relatoGrupoService.existenRelatosGrupoPorEstado(grupo, ESTADO_PENDIENTE);

        model.addAttribute("grupo", grupo);
        model.addAttribute("idUsuarioActual", usuario.getId());
        model.addAttribute("existenRelatosGruposPendientes", existenRelatosGruposPendientes);
        model.addAttribute("existenSolicitudesPendientes", existenSolicitudesPendientes);
    }
}

