package es.asun.StoryCrafters.controller;

import es.asun.StoryCrafters.entity.Categoria;
import es.asun.StoryCrafters.entity.Imagen;
import es.asun.StoryCrafters.entity.Relato;
import es.asun.StoryCrafters.entity.Usuario;
import es.asun.StoryCrafters.model.RelatoDto;
import es.asun.StoryCrafters.service.CategoriaService;
import es.asun.StoryCrafters.service.ImagenesService;
import es.asun.StoryCrafters.service.RelatoService;
import es.asun.StoryCrafters.service.UserService;
import es.asun.StoryCrafters.utilidades.Mappings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    @Autowired
    private ImagenesService imagenesService;

    private String content = "";

    @GetMapping("/mis-relatos")
    public String misRelatos(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Usuario usuario = userService.findUserByEmail(username);
        List<Relato> relatos = relatoService.findAllRelatosByUsuario(usuario);
        List<Categoria> listaCategorias = categoriaService.findAllCategories();
        content = "views/mis-relatos";

        model.addAttribute("content", content);
        model.addAttribute("listaCategorias", listaCategorias);
        model.addAttribute("relatos", relatos);
        return "index";
    }

    @GetMapping("/relatos/{id}")
    public String verRelato(Model model, @PathVariable String id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Usuario usuario = userService.findUserByEmail(username);
        int idUsuario = usuario.getId();

        int idRelato = Integer.parseInt(id);
        Relato vistaRelato = relatoService.findRelatoById(idRelato);

        if (idUsuario != vistaRelato.getUsuario().getId()) {
            content = "views/no-acceso";
        } else {
            model.addAttribute("relato", vistaRelato);
            content = "views/vista-relato";

        }
        model.addAttribute("content", content);
        return "index";
    }

    @GetMapping("/nuevo-relato-imagen")
    public String nuevoRelatoImagen(Model model) {

        List<Imagen> listaImagenes = imagenesService.findAllImagenes();

        content = "views/nuevo-relato-imagen";

        model.addAttribute("content", content);
        model.addAttribute("imagenes", listaImagenes);
        return "index";
    }

    @GetMapping("/nuevo-relato")
    public String nuevoRelato(Model model, @RequestParam("idImagenSeleccionada") int idImagenSeleccionada) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Usuario usuario = userService.findUserByEmail(username);
        String firma = usuario.getFirmaAutor();
        List<Categoria> listaCategorias = categoriaService.findAllCategories();

        Imagen imagen = imagenesService.findImageById(idImagenSeleccionada);

        content = "views/nuevo-relato";

        model.addAttribute("content", content);
        model.addAttribute("categorias", listaCategorias);
        model.addAttribute("firma", firma);
        model.addAttribute("imagen", imagen);
        model.addAttribute("relato", new RelatoDto()); // Agregar un objeto RelatoDto vacío al modelo
        return "index";
    }

    @GetMapping("/editar-relato/{id}")
    public String editarRelato(Model model, @PathVariable String id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Usuario usuario = userService.findUserByEmail(username);
        String firma = usuario.getFirmaAutor();
        List<Categoria> listaCategorias = categoriaService.findAllCategories();

        int idUsuario = usuario.getId();

        int idRelato = Integer.parseInt(id);
        Relato vistaRelato = relatoService.findRelatoById(idRelato);

        if (idUsuario != vistaRelato.getUsuario().getId()) {
            content = "views/no-acceso";
        } else {
            content = "views/editar-relato";
            model.addAttribute("firma", firma);
            model.addAttribute("relato", vistaRelato);
            model.addAttribute("categorias", listaCategorias);
        }

        model.addAttribute("content", content);
        return "index";
    }


    @PostMapping("/guardar-relato")
    public ResponseEntity<Integer> guardarRelato(@RequestBody RelatoDto relatoDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Usuario usuario = userService.findUserByEmail(username);
        Imagen imagen = imagenesService.findImageById(relatoDto.getIdImagen());
        relatoDto.setFirmaAutor(usuario.getFirmaAutor());

        Relato relato = Mappings.mapToRelato(relatoDto, usuario, imagen);
        // Guardar el relato con las categorías y obtener su ID
        int idRelato = relatoService.guardarRelato(relato, relatoDto.getCategorias());

        return new ResponseEntity<>(idRelato, HttpStatus.CREATED);
    }

}