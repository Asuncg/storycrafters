package es.asun.StoryCrafters.controller;

import es.asun.StoryCrafters.entity.*;
import es.asun.StoryCrafters.exceptions.CategoriaNotFoundException;
import es.asun.StoryCrafters.exceptions.GrupoException;
import es.asun.StoryCrafters.exceptions.RelatoException;
import es.asun.StoryCrafters.exceptions.UsuarioException;
import es.asun.StoryCrafters.model.RelatoDto;
import es.asun.StoryCrafters.model.RelatoPreviewDto;
import es.asun.StoryCrafters.service.*;
import es.asun.StoryCrafters.utils.AuthUtils;
import es.asun.StoryCrafters.utils.Constantes;
import es.asun.StoryCrafters.utils.Validadores;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

import static es.asun.StoryCrafters.utils.Constantes.ERROR_VIEW;

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

    @GetMapping("/mis-relatos")
    public String misRelatos(Model model) {
        try {
            Usuario usuario = AuthUtils.getAuthUser(userService);
            List<RelatoPreviewDto> relatosDto = relatoService.findAllRelatoByUsuarioOrderByFecha(usuario);

            List<Categoria> listaCategorias = categoriaService.findAllCategories();

            model.addAttribute("content", "views/relatos/mis-relatos");
            model.addAttribute("currentPage", "misRelatos");
            model.addAttribute("listaCategorias", listaCategorias);
            model.addAttribute("relatos", relatosDto);
            return Constantes.INDEX_VIEW;
        } catch (UsuarioException e) {
            model.addAttribute("content", ERROR_VIEW);
            return Constantes.INDEX_VIEW;        }
    }

    @GetMapping("/relatos/{id}")
    public String verRelato(Model model, @PathVariable String id) {
        if (!Validadores.validateId(id)) {
            model.addAttribute("content", ERROR_VIEW);
            return Constantes.INDEX_VIEW;
        }

        try {
            Relato relato = relatoService.findRelatoByIdAndNotArchivado(Integer.parseInt(id));

            Usuario usuario = AuthUtils.getAuthUser(userService);

            if (usuario.getId() != relato.getUsuario().getId()) {
                model.addAttribute("content", ERROR_VIEW);
                return Constantes.INDEX_VIEW;
            }

            model.addAttribute("relato", relato);
            model.addAttribute("content", "views/relatos/vista-relato");
            return Constantes.INDEX_VIEW;
        } catch (RelatoException | UsuarioException e) {
            model.addAttribute("content", ERROR_VIEW);
            return Constantes.INDEX_VIEW;
        }
    }

    @GetMapping("/nuevo-relato-imagen")
    public String nuevoRelatoImagen(Model model) {

        List<Imagen> listaImagenes = imagenesService.findAllImagenes();

        model.addAttribute("content", "views/relatos/nuevo-relato-imagen");
        model.addAttribute("currentPage", "nuevoRelato");
        model.addAttribute("imagenes", listaImagenes);
        return Constantes.INDEX_VIEW;
    }

    @GetMapping("/nuevo-relato")
    public String nuevoRelato(Model model, @RequestParam("idImagenSeleccionada") int idImagenSeleccionada) {
        try {
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
            return Constantes.INDEX_VIEW;
        } catch (UsuarioException e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping("/editar-relato/{id}")
    public String editarRelato(Model model, @PathVariable String id) {
        if (!Validadores.validateId(id)) {
            model.addAttribute("content", ERROR_VIEW);
            return Constantes.INDEX_VIEW;
        }

        try {
            Relato relato = relatoService.findRelatoByIdAndNotArchivado(Integer.parseInt(id));

            Usuario usuario = AuthUtils.getAuthUser(userService);

            if (usuario.getId() != relato.getUsuario().getId()) {
                model.addAttribute("content", ERROR_VIEW);
                return Constantes.INDEX_VIEW;
            }

            model.addAttribute("firma", usuario.getFirmaAutor());
            model.addAttribute("relato", relato);
            model.addAttribute("categorias", categoriaService.findAllCategories());
            model.addAttribute("content", "views/relatos/editar-relato");
            return Constantes.INDEX_VIEW;
        } catch (RelatoException | UsuarioException e) {
            model.addAttribute("content", ERROR_VIEW);
            return Constantes.INDEX_VIEW;
        }
    }

    @PostMapping("/guardar-relato")
    @ResponseBody
    public ResponseEntity<Integer> guardarRelato(@RequestBody RelatoDto relatoDto) {
        try {
            if (relatoDto.getId() == 0) {
                int idRelato = relatoService.guardarNuevoRelato(relatoDto);
                return new ResponseEntity<>(idRelato, HttpStatus.OK);
            } else {
                relatoService.actualizarRelato(relatoDto);
                return new ResponseEntity<>(relatoDto.getId(), HttpStatus.OK);
            }
        } catch (CategoriaNotFoundException | RelatoException | UsuarioException e) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }


    @GetMapping("/eliminar-relato/{id}")
    public String eliminarRelato(Model model, @PathVariable String id) {
        if (!Validadores.validateId(id)) {
            model.addAttribute("content", ERROR_VIEW);
            return Constantes.INDEX_VIEW;
        }

        try {
            relatoService.archivarRelato(Integer.parseInt(id));
            return "redirect:/relato/mis-relatos";
        } catch (RelatoException | UsuarioException e) {
            model.addAttribute("content", ERROR_VIEW);
            return Constantes.INDEX_VIEW;
        }
    }

    @PostMapping("/publicar-relato")
    @ResponseBody
    public ResponseEntity<Integer> publicarRelato(@RequestBody Map<String, Object> request) {
        String idRelato = request.get("idRelato").toString();
        String idGrupo = request.get("idGrupo").toString();

        if (!Validadores.validateId(idRelato) || !Validadores.validateId(idGrupo)) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        try {
            Grupo grupo = grupoService.findGrupoById(Integer.parseInt(idGrupo));

            Relato relato = relatoService.findRelatoByIdAndNotArchivado(Integer.parseInt(idRelato));

            if (relato.getFirmaAutor() == null || relato.getFirmaAutor().isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

            Optional<RelatoGrupo> relatoGrupoOptional = relatoGrupoService.findRelatoGrupoByRelatoAndGrupo(relato, grupo);

            if (relatoGrupoOptional.isPresent()) {
                RelatoGrupo relatoGrupo = relatoGrupoOptional.get();
                Usuario usuario = AuthUtils.getAuthUser(userService);

                if (relatoGrupo.getEstado() == 1 || relatoGrupo.getEstado() == 0 || usuario.getId() != relato.getUsuario().getId()) {
                    return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
                }

                relatoGrupoService.actualizarRelatoGrupoEnviado(relatoGrupo, relato);
            } else {
                RelatoGrupo relatoGrupo = new RelatoGrupo();


                relatoGrupoService.enviarNuevoRelatoGrupo(relatoGrupo, relato, grupo);
            }

            return new ResponseEntity<>(HttpStatus.OK);
        } catch (GrupoException | RelatoException | UsuarioException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}