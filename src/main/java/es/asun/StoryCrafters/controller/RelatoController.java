package es.asun.StoryCrafters.controller;

import es.asun.StoryCrafters.entity.Relato;
import es.asun.StoryCrafters.entity.Usuario;
import es.asun.StoryCrafters.model.RelatoDto;
import es.asun.StoryCrafters.model.UserRegisterDto;
import es.asun.StoryCrafters.model.UserUpdateDto;
import es.asun.StoryCrafters.service.RelatoService;
import es.asun.StoryCrafters.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/relato")
public class RelatoController {

    @Autowired
    private UserService userService;

    @Autowired
    private RelatoService relatoService;

    @GetMapping("/nuevo-relato")
    public String nuevoRelato(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Usuario usuario = userService.findUserByEmail(username);
        String firma = usuario.getFirmaAutor();
        model.addAttribute("firma", firma);
        model.addAttribute("relato", new RelatoDto()); // Agregar un objeto RelatoDto vacío al modelo
        return "views/nuevo-relato";
    }

    @PostMapping("/guardar-relato")
    public String guardarRelato(@ModelAttribute("relato") RelatoDto relatoDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Usuario usuario = userService.findUserByEmail(username);

        // Mapear los datos del DTO al objeto Relato
        Relato relato = new Relato();
        relato.setUsuario(usuario);
        relato.setTitulo(relatoDto.getTitulo());
        relato.setTexto(relatoDto.getTexto());
        relato.setFirmaAutor(relatoDto.getFirmaAutor());

        // Guardar el relato utilizando el servicio
        relatoService.guardarRelato(relato);

        return "redirect:/index"; // Redirigir a la página principal después de guardar el relato
    }
}