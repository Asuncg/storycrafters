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

import java.util.*;
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
        int idRelato = Integer.parseInt(id);
        Optional<Relato> relatoOptional = relatoService.findRelatoById(idRelato);

        if (relatoOptional.isEmpty()) {
            model.addAttribute("content", "views/no-acceso");
            return "index";
        }
        Relato relato = relatoOptional.get();

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Usuario usuario = userService.findUserByEmail(username);

        if (usuario.getId() != relato.getUsuario().getId()) {
            model.addAttribute("content", "views/no-acceso");
            return "index";
        }

        model.addAttribute("relato", relato);
        content = "views/vista-relato";

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

        if (imagen.getId() == 01) {
            imagen.setUrl("");
        }

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
        int idRelato = Integer.parseInt(id);
        Optional<Relato> relatoOptional = relatoService.findRelatoById(idRelato);

        if (relatoOptional.isEmpty()) {
            model.addAttribute("content", "views/no-acceso");
            return "index";
        }

        Relato relato = relatoOptional.get();

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Usuario usuario = userService.findUserByEmail(username);

        if (usuario.getId() != relato.getUsuario().getId()) {
            model.addAttribute("content", "views/no-acceso");
            return "index";
        }

        model.addAttribute("firma", usuario.getFirmaAutor());
        model.addAttribute("relato", relato);
        model.addAttribute("categorias", categoriaService.findAllCategories());

        model.addAttribute("content", "views/editar-relato");
        return "index";
    }

    @PostMapping("/guardar-relato")
    public ResponseEntity<Integer> guardarRelato(@RequestBody RelatoDto relatoDto) {

        if (!validarRelatoDTO(relatoDto)) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        Relato relato;

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Usuario usuario = userService.findUserByEmail(username);

        // Obtener las categorías asociadas con los IDs proporcionados
        List<Categoria> categorias = new ArrayList<>();
        for (Integer idCategoria : relatoDto.getCategorias()) {
            Optional<Categoria> categoriaOptional = categoriaService.findById(idCategoria);
            categoriaOptional.ifPresent(categorias::add);
        }

        if (relatoDto.getId() == 0) {
            Imagen imagen = imagenesService.findImageById(relatoDto.getIdImagen());

            relato = Mappings.mapToRelato(relatoDto, imagen);

            relato.setUsuario(usuario);
            relato.setCategorias(categorias);
            relato.setFechaCreacion(new Date());
        } else {
            Optional<Relato> relatoOptional = relatoService.findRelatoById(relatoDto.getId());

            if (relatoOptional.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

            if (relatoOptional.get().getUsuario().getId() != usuario.getId()) {
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }

            relato = relatoOptional.get();

            // Actualizar los campos del relato con los datos del relato actualizado
            relato.setTitulo(relatoDto.getTitulo());
            relato.setTexto(relatoDto.getTexto());
            relato.setFechaActualizacion(new Date());
            relato.setCategorias(categorias);
        }

        int idRelato = relatoService.guardarRelato(relato);
        return new ResponseEntity<>(idRelato, HttpStatus.OK);
    }

    private boolean validarRelatoDTO(RelatoDto relatoDto) {
        return true;
    }

}