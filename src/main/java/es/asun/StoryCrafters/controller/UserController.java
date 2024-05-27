package es.asun.StoryCrafters.controller;

import es.asun.StoryCrafters.entity.Avatar;
import es.asun.StoryCrafters.entity.Relato;
import es.asun.StoryCrafters.entity.Usuario;
import es.asun.StoryCrafters.exceptions.AvatarNotFoundException;
import es.asun.StoryCrafters.model.UserUpdateDto;
import es.asun.StoryCrafters.service.AvatarService;
import es.asun.StoryCrafters.service.RelatoService;
import es.asun.StoryCrafters.service.UserService;
import es.asun.StoryCrafters.utils.AuthUtils;
import es.asun.StoryCrafters.utils.Constantes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/user")
public class UserController {
    private final UserService userService;

    private final AvatarService avatarService;

    private final RelatoService relatoService;

    @Autowired
    public UserController(UserService userService, AvatarService avatarService, RelatoService relatoService) {
        this.userService = userService;
        this.avatarService = avatarService;
        this.relatoService = relatoService;
    }

    @GetMapping(value= {"/profile"})
    public String viewprofile(Model model) {
        Usuario usuario = AuthUtils.getAuthUser(userService);

        List<Relato> listaRelatos = relatoService.findAllRelatosByUsuarioAndNotArchivado(usuario);
        int numRelatos = listaRelatos.size();

        model.addAttribute("content", "views/profile");
        model.addAttribute("numRelatos", numRelatos);
        return "index";
    }

    @GetMapping("/edit")
    public String showEditProfileForm(Model model) {

        model.addAttribute("content", "views/edit-profile");
        return "index";
    }

    @PostMapping("/profile/edit")
    public String editProfile(@ModelAttribute("user") UserUpdateDto userDto) {
        userService.updateUser(userDto);

        return "redirect:/user/profile";
    }

    @GetMapping("/profile/edit-avatar")
    public String editAvatarProfile(@ModelAttribute("user") UserUpdateDto userDto, Model model) {
        List<Avatar> listaAvatares = avatarService.findAllAvatars();

        model.addAttribute("content", "views/galeria-avatar");
        model.addAttribute("listavatares", listaAvatares);
        return "index";
    }

    @PostMapping("/profile/update-avatar")
    public String updateAvatarProfile(Model model,@ModelAttribute("user") UserUpdateDto userDto, @RequestParam("selectedAvatarId") Integer selectedAvatarId) {
        Usuario usuario = AuthUtils.getAuthUser(userService);
        Avatar avatar;
        try {
            avatar  = avatarService.findAvatarById(Integer.parseInt(String.valueOf(selectedAvatarId)));
            usuario.setAvatar(avatar);
            UserUpdateDto userUpdateDto = new UserUpdateDto(usuario);

            userService.updateUser(userUpdateDto);

            return "redirect:/user/profile";
        } catch (AvatarNotFoundException e) {
            model.addAttribute("content", Constantes.ERROR_VIEW);
            return Constantes.INDEX_VIEW;
        }
    }
}
