package es.asun.StoryCrafters.controller;

import es.asun.StoryCrafters.entity.Categoria;
import es.asun.StoryCrafters.entity.Grupo;
import es.asun.StoryCrafters.entity.Relato;
import es.asun.StoryCrafters.entity.Usuario;
import es.asun.StoryCrafters.model.GrupoDto;
import es.asun.StoryCrafters.model.UserUpdateDto;
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

        Grupo grupo = new Grupo();

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
        content = "views/grupos/mis-grupos";
        grupoService.deleteGrupoById(idGrupo);
        return "redirect:/index";
    }
}
