package es.asun.StoryCrafters.controller;

import es.asun.StoryCrafters.entity.Categoria;
import es.asun.StoryCrafters.entity.Relato;
import es.asun.StoryCrafters.entity.Usuario;
import es.asun.StoryCrafters.model.RelatoDto;
import es.asun.StoryCrafters.model.UserRegisterDto;
import es.asun.StoryCrafters.model.UserUpdateDto;
import es.asun.StoryCrafters.service.CategoriaService;
import es.asun.StoryCrafters.service.RelatoService;
import es.asun.StoryCrafters.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/relato")
public class RelatoController {

    @Autowired
    private UserService userService;

    @Autowired
    private RelatoService relatoService;

    @Autowired
    private CategoriaService categoriaService;

    @GetMapping("/mis-relatos")
    public String misRelatos(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Usuario usuario = userService.findUserByEmail(username);
        List<Relato> relatos = relatoService.findAllRelatosByUsuario(usuario);
        model.addAttribute("relatos", relatos);
        return "views/mis-relatos";
    }

    @GetMapping("/ver-relato/{id}")
    public String verRelato(Model model,  @PathVariable String id) {

        int idRelato = Integer.parseInt(id);
        Relato vistaRelato = relatoService.findRelatoById(idRelato);

        model.addAttribute("relato", vistaRelato);
        return "views/vista-relato";
    }


    @GetMapping("/nuevo-relato")
    public String nuevoRelato(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Usuario usuario = userService.findUserByEmail(username);
        String firma = usuario.getFirmaAutor();
        List<Categoria> listaCategorias = categoriaService.findAllCategories();

        model.addAttribute("categorias", listaCategorias);
        model.addAttribute("firma", firma);
        model.addAttribute("relato", new RelatoDto()); // Agregar un objeto RelatoDto vacío al modelo
        return "views/nuevo-relato";
    }

    @PostMapping("/guardar-relato")
    public String guardarRelato(@ModelAttribute("relato") RelatoDto relatoDto, @RequestParam("idsCategoriasSeleccionadas") String idsCategoriasSeleccionadasStr) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Usuario usuario = userService.findUserByEmail(username);

        // Mapear los datos del DTO al objeto Relato
        Relato relato = new Relato();
        relato.setUsuario(usuario);
        relato.setTitulo(relatoDto.getTitulo());
        relato.setTexto(relatoDto.getTexto());
        relato.setFirmaAutor(relatoDto.getFirmaAutor());

        // Convertir la cadena de IDs en una lista de enteros
        List<Integer> idsCategoriasSeleccionadas = Arrays.stream(idsCategoriasSeleccionadasStr.split(","))
                .map(Integer::valueOf)
                .collect(Collectors.toList());

        // Guardar el relato con las categorías
        relatoService.guardarRelato(relato, idsCategoriasSeleccionadas);

        return "redirect:/index"; // Redirigir a la página principal después de guardar el relato
    }


}