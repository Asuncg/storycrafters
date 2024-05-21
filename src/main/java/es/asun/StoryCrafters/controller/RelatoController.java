package es.asun.StoryCrafters.controller;

import es.asun.StoryCrafters.entity.*;
import es.asun.StoryCrafters.exceptions.BusinessException;
import es.asun.StoryCrafters.model.GrupoDto;
import es.asun.StoryCrafters.model.RelatoDto;
import es.asun.StoryCrafters.model.RelatoPreviewDto;
import es.asun.StoryCrafters.service.*;
import es.asun.StoryCrafters.utilidades.AuthUtils;
import es.asun.StoryCrafters.utilidades.Mappings;
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

    @Autowired
    private UserService userService;

    @Autowired
    private RelatoService relatoService;

    @Autowired
    private CategoriaService categoriaService;

    @Autowired
    private ImagenesService imagenesService;

    @Autowired
    private GrupoService grupoService;

    @Autowired
    private RelatoGrupoService relatoGrupoService;

    private static final String ERROR_VIEW = "views/error/error";
    private static final String INDEX_VIEW = "index";

    @GetMapping("/mis-relatos")
    public String misRelatos(Model model) {
        Usuario usuario = AuthUtils.getAuthUser(userService);

        List<Relato> relatos = relatoService.findAllRelatoByUsuarioOrderByFecha(usuario);
        List<RelatoPreviewDto> relatosDto = new ArrayList<>();

        for (Relato relato : relatos) {
            RelatoPreviewDto relatoDto = Mappings.mapToRelatoVistaDto(relato);
            relatosDto.add(relatoDto);
        }
        List<Categoria> listaCategorias = categoriaService.findAllCategories();

        model.addAttribute("content", "views/relatos/mis-relatos");
        model.addAttribute("listaCategorias", listaCategorias);
        model.addAttribute("relatos", relatosDto);
        return INDEX_VIEW;
    }

    @GetMapping("/relatos/{id}")
    public String verRelato(Model model, @PathVariable String id) {
        int idRelato = Integer.parseInt(id);
        Optional<Relato> relatoOptional = relatoService.findRelatoByIdAndNotArchivado(idRelato);

        if (relatoOptional.isEmpty()) {
            model.addAttribute("content", ERROR_VIEW);
            return INDEX_VIEW;
        }
        Relato relato = relatoOptional.get();

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
        Optional<Relato> relatoOptional = relatoService.findRelatoByIdAndNotArchivado(idRelato);

        if (relatoOptional.isEmpty()) {
            model.addAttribute("content", ERROR_VIEW);
            return INDEX_VIEW;
        }

        Relato relato = relatoOptional.get();

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
            Optional<Relato> relatoOptional = relatoService.findRelatoByIdAndNotArchivado(relatoDto.getId());

            if (relatoOptional.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

            if (relatoOptional.get().getUsuario().getId() != usuario.getId()) {
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }

            relato = relatoOptional.get();

            relato.setTitulo(relatoDto.getTitulo());
            relato.setTexto(relatoDto.getTexto());
            relato.setFechaActualizacion(new Date());
            relato.setCategorias(categorias);
        }

        int idRelato = relatoService.guardarRelato(relato);
        return new ResponseEntity<>(idRelato, HttpStatus.OK);
    }


    @GetMapping("/eliminar-relato/{id}")
    public String eliminarGrupo(Model model, @PathVariable String id) {
        int idRelato = Integer.parseInt(id);

        Usuario usuario = AuthUtils.getAuthUser(userService);

        Optional<Relato> relatoOptional = relatoService.findRelatoByIdAndNotArchivado(idRelato);
        if (relatoOptional.isEmpty()) {
            model.addAttribute("content", ERROR_VIEW);
            return INDEX_VIEW;
        }

        if (usuario.getId() != relatoOptional.get().getUsuario().getId()) {
            model.addAttribute("content", ERROR_VIEW);
            return INDEX_VIEW;
        }

        Relato relato = relatoOptional.get();
        relato.setArchivado(true);

        relatoService.guardarRelato(relato);
        return "redirect:/relato/mis-relatos";
    }

    @PostMapping("/publicar-relato")
    public ResponseEntity<Integer> publicarRelato(@RequestBody Map<String, Object> request) throws BusinessException {
        int idRelato = Integer.parseInt(request.get("idRelato").toString());
        int idGrupo = Integer.parseInt(request.get("idGrupo").toString());

        //Comprobar si el relato y el grupo existen
        Optional<Relato> relatoOptional = relatoService.findRelatoByIdAndNotArchivado(idRelato);
        Optional<Grupo> grupoOptional = grupoService.findGrupoById(idGrupo);

        if (relatoOptional.isEmpty() || grupoOptional.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        Relato relato = relatoOptional.get();
        Grupo grupo = grupoOptional.get();
        RelatoGrupo relatoGrupo = new RelatoGrupo();

        //Comprobar si ya hay una copia del relato en enviada a ese grupo
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
            relatoGrupo.setFechaEnvio(new Date());

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
        relatoGrupo.setFechaEnvio(new Date());

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
}