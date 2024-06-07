package es.asun.StoryCrafters.controller;

import es.asun.StoryCrafters.entity.*;
import es.asun.StoryCrafters.exceptions.AvatarNotFoundException;
import es.asun.StoryCrafters.exceptions.UsuarioException;
import es.asun.StoryCrafters.model.UserUpdateDto;
import es.asun.StoryCrafters.service.*;
import es.asun.StoryCrafters.utils.AuthUtils;
import es.asun.StoryCrafters.utils.Constantes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador para manejar las solicitudes relacionadas con los usuarios.
 */
@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private AvatarService avatarService;

    @Autowired
    private RelatoService relatoService;

    @Autowired
    private GrupoService grupoService;

    @Autowired
    private RelatoGrupoService relatoGrupoService;

    /**
     * Muestra el perfil del usuario.
     *
     * @param model el modelo para la vista
     * @return el nombre de la vista de perfil del usuario
     */
    @GetMapping(value = {"/profile"})
    public String viewProfile(Model model) {
        try {
            Usuario usuario = AuthUtils.getAuthUser(userService);
            List<Relato> listaRelatos = relatoService.findAllRelatosByUsuarioAndNotArchivado(usuario);
            int numRelatos = listaRelatos.size();

            List<Grupo> listaGrupos = grupoService.encontrarGruposContieneUsuario(usuario);
            int numGrupos = listaGrupos.size();

            List<RelatoGrupo> listaRelatosGrupo = relatoGrupoService.encontrarTodosRelatosGrupoPorUsuarioAprobados(usuario);
            int numRelatosGrupo = listaRelatosGrupo.size();

            model.addAttribute("content", "views/profile");
            model.addAttribute("numRelatos", numRelatos);
            model.addAttribute("numGrupos", numGrupos);
            model.addAttribute("numRelatosGrupo", numRelatosGrupo);
            return "index";
        } catch (UsuarioException e) {
            model.addAttribute("content", Constantes.ERROR_VIEW);
            return Constantes.INDEX_VIEW;        }
    }

    /**
     * Muestra el formulario para editar el perfil del usuario.
     *
     * @param model el modelo para la vista
     * @return el nombre de la vista de edición del perfil
     */
    @GetMapping("/edit")
    public String showEditProfileForm(Model model) {
        model.addAttribute("content", "views/edit-profile");
        return "index";
    }

    /**
     * Procesa la edición del perfil del usuario.
     *
     * @param userDto el DTO con los datos actualizados del usuario
     * @return la redirección a la vista de perfil del usuario
     */
    @PostMapping("/profile/edit")
    public String editProfile(@ModelAttribute("user") UserUpdateDto userDto) {
        userService.updateUser(userDto);
        return "redirect:/user/profile";
    }

    /**
     * Muestra el formulario para editar el avatar del perfil del usuario.
     *
     * @param userDto el DTO con los datos del usuario
     * @param model el modelo para la vista
     * @return el nombre de la vista de galería de avatares
     */
    @GetMapping("/profile/edit-avatar")
    public String editAvatarProfile(@ModelAttribute("user") UserUpdateDto userDto, Model model) {
        List<Avatar> listaAvatares = avatarService.findAllAvatars();
        model.addAttribute("content", "views/galeria-avatar");
        model.addAttribute("listavatares", listaAvatares);
        return "index";
    }

    /**
     * Procesa la actualización del avatar del perfil del usuario.
     *
     * @param model el modelo para la vista
     * @param userDto el DTO con los datos del usuario
     * @param selectedAvatarId el ID del avatar seleccionado
     * @return la redirección a la vista de perfil del usuario o la vista de error en caso de fallo
     */
    @PostMapping("/profile/update-avatar")
    public String updateAvatarProfile(Model model, @ModelAttribute("user") UserUpdateDto userDto, @RequestParam("selectedAvatarId") Integer selectedAvatarId) {
        try {
            Usuario usuario = AuthUtils.getAuthUser(userService);
            Avatar avatar = avatarService.findAvatarById(Integer.parseInt(String.valueOf(selectedAvatarId)));
            usuario.setAvatar(avatar);
            UserUpdateDto userUpdateDto = new UserUpdateDto(usuario);
            userService.updateUser(userUpdateDto);
            return "redirect:/user/profile";
        } catch (AvatarNotFoundException | UsuarioException e) {
            model.addAttribute("content", Constantes.ERROR_VIEW);
            return Constantes.INDEX_VIEW;
        }
    }
}
