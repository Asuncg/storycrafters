package es.asun.StoryCrafters.controller;

import es.asun.StoryCrafters.entity.Categoria;
import es.asun.StoryCrafters.entity.Imagenes;
import es.asun.StoryCrafters.entity.Relato;
import es.asun.StoryCrafters.entity.Usuario;
import es.asun.StoryCrafters.model.RelatoDto;
import es.asun.StoryCrafters.model.UserRegisterDto;
import es.asun.StoryCrafters.model.UserUpdateDto;
import es.asun.StoryCrafters.service.CategoriaService;
import es.asun.StoryCrafters.service.ImagenesService;
import es.asun.StoryCrafters.service.RelatoService;
import es.asun.StoryCrafters.service.UserService;
import es.asun.StoryCrafters.utilidades.Mappings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Date;
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

        // Ahora las categorías están cargadas con el relato
        List<Categoria> listaCategoriasRelato = vistaRelato.getCategorias();

        if (idUsuario != vistaRelato.getUsuario().getId()) {
            return "views/no-acceso";
        } else {
            model.addAttribute("relato", vistaRelato);
            return "views/vista-relato";
        }
    }

    @GetMapping("/nuevo-relato-imagen")
    public String nuevoRelatoImagen(Model model) {

        List<Imagenes> listaImagenes = imagenesService.findAllImagenes();

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

        Imagenes imagen = imagenesService.findImageById(idImagenSeleccionada);

        String urlImagen = imagen.getUrl();

        content = "views/nuevo-relato";

        model.addAttribute("content", content);
        model.addAttribute("categorias", listaCategorias);
        model.addAttribute("firma", firma);
        model.addAttribute("urlImagen", urlImagen);
        model.addAttribute("relato", new RelatoDto()); // Agregar un objeto RelatoDto vacío al modelo
        return "index";
    }

    @PostMapping("/guardar-relato")
    public String guardarRelato(@ModelAttribute("relato") RelatoDto relatoDto, @RequestParam("idsCategoriasSeleccionadas") String idsCategoriasSeleccionadasStr) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Usuario usuario = userService.findUserByEmail(username);

        Relato relato = Mappings.mapToRelato(relatoDto, usuario);

        // Convertir la cadena de IDs en una lista de enteros
        List<Integer> idsCategoriasSeleccionadas = Arrays.stream(idsCategoriasSeleccionadasStr.split(","))
                .map(Integer::valueOf)
                .collect(Collectors.toList());

        // Guardar el relato con las categorías
        relatoService.guardarRelato(relato, idsCategoriasSeleccionadas);

        return "redirect:/index"; // Redirigir a la página principal después de guardar el relato
    }


}