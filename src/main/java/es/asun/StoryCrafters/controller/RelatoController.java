package es.asun.StoryCrafters.controller;

import es.asun.StoryCrafters.entity.*;
import es.asun.StoryCrafters.exceptions.BusinessException;
import es.asun.StoryCrafters.exceptions.GrupoException;
import es.asun.StoryCrafters.exceptions.RelatoException;
import es.asun.StoryCrafters.model.RelatoDto;
import es.asun.StoryCrafters.model.RelatoPreviewDto;
import es.asun.StoryCrafters.service.*;
import es.asun.StoryCrafters.utils.AuthUtils;
import es.asun.StoryCrafters.utils.Mappings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Controller
@RequestMapping("/relato")
public class RelatoController {

    private final UserService userService;
    private final RelatoService relatoService;
    private final CategoriaService categoriaService;
    private final ImagenesService imagenesService;
    private final GrupoService grupoService;
    private final RelatoGrupoService relatoGrupoService;

    private static final String ERROR_VIEW = "views/error/error";
    private static final String INDEX_VIEW = "index";

    @Autowired
    public RelatoController(UserService userService, RelatoService relatoService, CategoriaService categoriaService,
                            ImagenesService imagenesService, GrupoService grupoService, RelatoGrupoService relatoGrupoService) {
        this.userService = userService;
        this.relatoService = relatoService;
        this.categoriaService = categoriaService;
        this.imagenesService = imagenesService;
        this.grupoService = grupoService;
        this.relatoGrupoService = relatoGrupoService;
    }

    @GetMapping("/mis-relatos")
    public String misRelatos(Model model) {
        Usuario usuario = AuthUtils.getAuthUser(userService);

        List<RelatoPreviewDto> relatosDto = relatoService.findAllRelatoByUsuarioOrderByFecha(usuario);

        List<Categoria> listaCategorias = categoriaService.findAllCategories();

        model.addAttribute("content", "views/relatos/mis-relatos");
        model.addAttribute("listaCategorias", listaCategorias);
        model.addAttribute("relatos", relatosDto);
        return INDEX_VIEW;
    }

    @GetMapping("/relatos/{id}")
    public String verRelato(Model model, @PathVariable String id) {
        int idRelato = Integer.parseInt(id);
        Relato relato;
        try {
            relato = relatoService.findRelatoByIdAndNotArchivado(idRelato);
        } catch (RelatoException e) {
            model.addAttribute("content", ERROR_VIEW);
            return INDEX_VIEW;
        }

        Usuario usuario = AuthUtils.getAuthUser(userService);

        if (usuario.getId() != relato.getUsuario().getId()) {
            model.addAttribute("content", ERROR_VIEW);
            return INDEX_VIEW;
        }

        model.addAttribute("relato", relato);
        model.addAttribute("content", "views/relatos/vista-relato");
        return INDEX_VIEW;
    }

    @GetMapping("/nuevo-relato-imagen")
    public String nuevoRelatoImagen(Model model) {

        List<Imagen> listaImagenes = imagenesService.findAllImagenes();

        model.addAttribute("content", "views/relatos/nuevo-relato-imagen");
        model.addAttribute("imagenes", listaImagenes);
        return INDEX_VIEW;
    }

    @GetMapping("/nuevo-relato")
    public String nuevoRelato(Model model, @RequestParam("idImagenSeleccionada") int idImagenSeleccionada) {
        Usuario usuario = AuthUtils.getAuthUser(userService);

        String firma = usuario.getFirmaAutor();
        List<Categoria> listaCategorias = categoriaService.findAllCategories();

        Imagen imagen = imagenesService.findImageById(idImagenSeleccionada);

        if (imagen.getId() == 01) {
            imagen.setUrl("");
        }

        model.addAttribute("content", "views/relatos/nuevo-relato");
        model.addAttribute("categorias", listaCategorias);
        model.addAttribute("firma", firma);
        model.addAttribute("imagen", imagen);
        model.addAttribute("relato", new RelatoDto());
        return INDEX_VIEW;
    }

    @GetMapping("/editar-relato/{id}")
    public String editarRelato(Model model, @PathVariable String id) {
        int idRelato = Integer.parseInt(id);
        Relato relato;
        try {
            relato = relatoService.findRelatoByIdAndNotArchivado(idRelato);
        } catch (RelatoException e) {
            model.addAttribute("content", ERROR_VIEW);
            return INDEX_VIEW;
        }

        Usuario usuario = AuthUtils.getAuthUser(userService);

        if (usuario.getId() != relato.getUsuario().getId()) {
            model.addAttribute("content", ERROR_VIEW);
            return INDEX_VIEW;
        }

        model.addAttribute("firma", usuario.getFirmaAutor());
        model.addAttribute("relato", relato);
        model.addAttribute("categorias", categoriaService.findAllCategories());
        model.addAttribute("content", "views/relatos/editar-relato");
        return INDEX_VIEW;
    }

    @PostMapping("/guardar-relato")
    public ResponseEntity<Integer> guardarRelato(@RequestBody RelatoDto relatoDto) {
        Usuario usuario = AuthUtils.getAuthUser(userService);

        Relato relato;

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
            try {
                relato = relatoService.findRelatoByIdAndNotArchivado(relatoDto.getId());
            } catch (RelatoException e) {
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }

            if (relato.getUsuario().getId() != usuario.getId()) {
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }

            relato.setTitulo(relatoDto.getTitulo());
            relato.setTexto(relatoDto.getTexto());
            relato.setFechaActualizacion(new Date());
            relato.setCategorias(categorias);
        }

        int idRelato = relatoService.guardarRelato(relato);
        return new ResponseEntity<>(idRelato, HttpStatus.OK);
    }


    @GetMapping("/eliminar-relato/{id}")
    public String eliminarRelato(Model model, @PathVariable String id) {
        int idRelato = Integer.parseInt(id);

        Usuario usuario = AuthUtils.getAuthUser(userService);

        Relato relato;
        try {
            relato = relatoService.findRelatoByIdAndNotArchivado(idRelato);

            if (usuario.getId() != relato.getUsuario().getId()) {
                model.addAttribute("content", ERROR_VIEW);
                return INDEX_VIEW;
            }

            relato.setArchivado(true);

            relatoService.guardarRelato(relato);
            return "redirect:/relato/mis-relatos";
        } catch (RelatoException e) {
            model.addAttribute("content", ERROR_VIEW);
            return INDEX_VIEW;
        }
    }

    @PostMapping("/publicar-relato")
    public ResponseEntity<Integer> publicarRelato(@RequestBody Map<String, Object> request) {
        int idRelato = Integer.parseInt(request.get("idRelato").toString());
        int idGrupo = Integer.parseInt(request.get("idGrupo").toString());


        Grupo grupo = null;
        try {
            grupo = grupoService.findGrupoById(idGrupo);

            Relato relato = relatoService.findRelatoByIdAndNotArchivado(idRelato);

            RelatoGrupo relatoGrupo = new RelatoGrupo();

            Optional<RelatoGrupo> relatoGrupoOptional = relatoGrupoService.findRelatoGrupoByRelatoAndGrupo(relato, grupo);

            if (relatoGrupoOptional.isPresent()) {
                relatoGrupo = relatoGrupoOptional.get();

                Usuario usuario = AuthUtils.getAuthUser(userService);

                //Si existe, comprobar el estado: Aprobado o pendiente, y si es el autor o no del relato
                if (relatoGrupo.getEstado() == 1 || relatoGrupo.getEstado() == 0 || usuario.getId() != relato.getUsuario().getId()) {
                    return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
                }

                relatoGrupo.setRelato(relato);
                relatoGrupo.setTitulo(relato.getTitulo());
                relatoGrupo.setTexto(relato.getTexto());
                relatoGrupo.setFirmaAutor(relato.getFirmaAutor());
                relatoGrupo.setImagen(relato.getImagen());
                relatoGrupo.setEstado(1);
                relatoGrupo.setFeedback("");
                relatoGrupo.setFechaModificacion(new Date());

                // Crear nuevas instancias de las categorías y asociarlas al RelatoGrupo
                List<Categoria> categorias = new ArrayList<>();
                for (Categoria categoria : relato.getCategorias()) {
                    Categoria nuevaCategoria = new Categoria();
                    nuevaCategoria.setId(categoria.getId());
                    nuevaCategoria.setNombre(categoria.getNombre());
                    categorias.add(nuevaCategoria);
                }
                relatoGrupo.setCategorias(categorias);

                relatoGrupoService.guardarRelatoGrupo(relatoGrupo);

                return new ResponseEntity<>(HttpStatus.OK);

            }

            relatoGrupo.setRelato(relato);
            relatoGrupo.setGrupo(grupo);
            relatoGrupo.setTitulo(relato.getTitulo());
            relatoGrupo.setTexto(relato.getTexto());
            relatoGrupo.setFirmaAutor(relato.getFirmaAutor());
            relatoGrupo.setImagen(relato.getImagen());
            relatoGrupo.setFechaModificacion(new Date());

            // Crear nuevas instancias de las categorías y asociarlas al RelatoGrupo
            List<Categoria> categorias = new ArrayList<>();
            for (Categoria categoria : relato.getCategorias()) {
                Categoria nuevaCategoria = new Categoria();
                nuevaCategoria.setId(categoria.getId());
                nuevaCategoria.setNombre(categoria.getNombre());
                categorias.add(nuevaCategoria);
            }
            relatoGrupo.setCategorias(categorias);

            relatoGrupoService.guardarRelatoGrupo(relatoGrupo);

            return new ResponseEntity<>(HttpStatus.OK);

        } catch (GrupoException | RelatoException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}