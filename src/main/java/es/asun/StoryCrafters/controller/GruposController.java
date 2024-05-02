package es.asun.StoryCrafters.controller;

import es.asun.StoryCrafters.entity.Grupo;
import es.asun.StoryCrafters.entity.Usuario;
import es.asun.StoryCrafters.model.GrupoDto;
import es.asun.StoryCrafters.service.EmailService;
import es.asun.StoryCrafters.service.GrupoService;
import es.asun.StoryCrafters.service.UserService;
import es.asun.StoryCrafters.utilidades.CodigoIngresoGenerator;
import es.asun.StoryCrafters.utilidades.Mappings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/grupos")
public class GruposController {

    @Autowired
    private UserService userService;

    @Autowired
    private GrupoService grupoService;

    @Autowired
    private EmailService emailService;
    private String content = "";

    @GetMapping("/mis-grupos")
    public String misGrupos(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Usuario usuario = userService.findUserByEmail(username);
        List<Grupo> grupos = grupoService.findAllGruposByUsuario(usuario);

        content = "views/grupos/mis-grupos";

        model.addAttribute("grupos", grupos);
        model.addAttribute("content", content);
        model.addAttribute("idUsuarioActual", usuario.getId());
        return "index";
    }

    @GetMapping("/nuevo-grupo")
    public String nuevoGrupo(Model model) {

        content = "views/grupos/crear-grupo";

        model.addAttribute("content", content);
        model.addAttribute("grupoDto", new GrupoDto());
        return "index";
    }

    @PostMapping("/crear-grupo")
    public String crearGrupo(@ModelAttribute("user") GrupoDto grupoDto, Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Usuario usuario = userService.findUserByEmail(username);

        Grupo grupo;

        List<Usuario> listaUsuarios = new ArrayList<>();
        listaUsuarios.add(usuario);

        String codigoAcceso = CodigoIngresoGenerator.generarCodigoIngreso();

        grupo = Mappings.mapToGrupo(grupoDto);
        grupo.setUsuario(usuario);
        grupo.setUsuarios(listaUsuarios);
        grupo.setCodigoAcceso(codigoAcceso);

        String destinatario = "ansuncg@gmail.com";
        String asunto = "Asunto del correo";
        String mensaje = "Este es un ejemplo de mensaje.";

        emailService.sendEmail(destinatario, asunto, mensaje);
        grupoService.guardarGrupo(grupo);

        return "redirect:/grupos/mis-grupos";
    }


    @GetMapping("/eliminar-grupo/{id}")
    public String eliminarGrupo(Model model, @PathVariable String id) {
        int idGrupo = Integer.parseInt(id);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Usuario usuario = userService.findUserByEmail(username);

        Optional<Grupo> grupoOptional = grupoService.findGrupoById(idGrupo);
        if (grupoOptional.isEmpty()) {
            model.addAttribute("content", "views/no-acceso");
            return "index";
        }

        if (usuario.getId() != grupoOptional.get().getUsuario().getId()) {
            model.addAttribute("content", "views/no-acceso");
            return "index";
        }

        grupoService.deleteGrupoById(idGrupo);
        return "redirect:/grupos/mis-grupos";
    }


    @GetMapping("/editar-grupo/{id}")
    public String editarGrupo(Model model, @PathVariable String id) {
        int idGrupo = Integer.parseInt(id);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Usuario usuario = userService.findUserByEmail(username);

        Optional<Grupo> grupoOptional = grupoService.findGrupoById(idGrupo);
        if (grupoOptional.isEmpty()) {
            content = "views/no-acceso";
            return "index";
        }
        Grupo grupo = grupoOptional.get();

        if (usuario.getId() != grupo.getUsuario().getId()) {
            content = "views/no-acceso";
            return "index";
        }

        content = "views/grupos/editar-grupo";

        GrupoDto grupoDto = new GrupoDto();
        model.addAttribute("grupo", grupo);
        model.addAttribute("grupoDto", grupoDto);
        model.addAttribute("content", content);

        return "index";
    }

    @PostMapping("actualizar-grupo/{id}")
    public String actualizarGrupo(Model model, @PathVariable String id, GrupoDto grupoDto) {
        int idGrupo = Integer.parseInt(id);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Usuario usuario = userService.findUserByEmail(username);

        Optional<Grupo> grupoOptional = grupoService.findGrupoById(idGrupo);
        if (grupoOptional.isEmpty()) {
            content = "views/no-acceso";
            return "index";
        }
        Grupo grupo = grupoOptional.get();

        if (usuario.getId() != grupo.getUsuario().getId()) {
            content = "views/no-acceso";
            return "index";
        }

        grupo.setNombre(grupoDto.getNombre());
        grupo.setDescripcion(grupoDto.getDescripcion());
        grupoService.guardarGrupo(grupo);

        return "redirect:/grupos/mis-grupos";

    }

    @PostMapping("/invitar-usuarios")
    @ResponseBody
    public String invitarUsuarios(@RequestBody Map<String, Object> request) {
        // Obtener el ID del grupo y la lista de correos electrónicos desde la solicitud
        int idGrupo = Integer.parseInt(request.get("groupId").toString());
        List<String> emails = (List<String>) request.get("emails");

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Usuario usuario = userService.findUserByEmail(username);

        Optional<Grupo> grupoOptional = grupoService.findGrupoById(idGrupo);
        if (grupoOptional.isEmpty()) {
            // Si el grupo no existe, puedes devolver un mensaje de error o lanzar una excepción
            return "El grupo no existe.";
        }
        Grupo grupo = grupoOptional.get();

        if (usuario.getId() != grupo.getUsuario().getId()) {
            // Si el usuario no es el propietario del grupo, puedes devolver un mensaje de error o lanzar una excepción
            return "No tienes permiso para invitar usuarios a este grupo.";
        }

        for (String email : emails) {
            emailService.enviarInvitacion(email, grupo.getCodigoAcceso());
        }

        return "Invitaciones enviadas correctamente.";
    }

    @PostMapping("/ingresar-invitacion")
    public String ingresarInvitacion(@RequestParam("codigoInvitacion") String codigoInvitacion, Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Usuario usuario = userService.findUserByEmail(username);

        Optional<Grupo> grupoOptional = grupoService.findGrupoByCodigoAcceso(codigoInvitacion);
        if (grupoOptional.isEmpty()) {
            // Si el grupo no existe, puedes devolver un mensaje de error o lanzar una excepción
            return "El grupo no existe.";
        }

        Grupo grupo = grupoOptional.get();

        // Verificar si el usuario ya está en el grupo
        if (grupo.getUsuarios().contains(usuario)) {
            // Si el usuario ya está en el grupo, puedes devolver un mensaje de error o redirigirlo
            model.addAttribute("mensaje", "Ya eres miembro de este grupo.");
            return "redirect:/grupos/mis-grupos";
        }

        grupo.getUsuarios().add(usuario);

        grupoService.guardarGrupo(grupo);

        model.addAttribute("mensaje", "¡Te has unido al grupo con éxito!");
        return "redirect:/grupos/mis-grupos";
    }


    @PostMapping("/abandonar-grupo")
    public String abandonarGrupo(@RequestParam("grupoId") String grupoId) {
        int idGrupo = Integer.parseInt(grupoId);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Usuario usuario = userService.findUserByEmail(username);

        Optional<Grupo> grupoOptional = grupoService.findGrupoById(idGrupo);
        if (grupoOptional.isEmpty()) {
            // Si el grupo no existe, puedes devolver un mensaje de error o lanzar una excepción
            return "El grupo no existe.";
        }

        Grupo grupo = grupoOptional.get();

        // Verificar si el usuario está en el grupo
        if (!grupo.getUsuarios().contains(usuario)) {

            return "redirect:/grupos/mis-grupos";
        }

        // Eliminar al usuario del grupo
        grupo.getUsuarios().remove(usuario);

        grupoService.guardarGrupo(grupo);

        return "redirect:/grupos/mis-grupos";
    }

}

