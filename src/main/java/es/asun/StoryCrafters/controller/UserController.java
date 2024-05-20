package es.asun.StoryCrafters.controller;

import es.asun.StoryCrafters.entity.Avatar;
import es.asun.StoryCrafters.entity.Relato;
import es.asun.StoryCrafters.entity.RelatoGrupo;
import es.asun.StoryCrafters.entity.Usuario;
import es.asun.StoryCrafters.model.UserRegisterDto;
import es.asun.StoryCrafters.model.UserUpdateDto;
import es.asun.StoryCrafters.service.AvatarService;
import es.asun.StoryCrafters.service.RelatoGrupoService;
import es.asun.StoryCrafters.service.RelatoService;
import es.asun.StoryCrafters.service.UserService;
import es.asun.StoryCrafters.utilidades.AuthUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

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
    RelatoGrupoService relatoGrupoService;

    @GetMapping(value= {"/profile"})
    public String viewprofile(Model model) {
        Usuario usuario = AuthUtils.getAuthUser(userService);

        List<Relato> listaRelatos = relatoService.findAllRelatosByUsuarioAndNotArchivado(usuario);
        int numRelatos = listaRelatos.size();

        model.addAttribute("content", "views/profile");
        model.addAttribute("usuario", usuario);
        model.addAttribute("numRelatos", numRelatos);
        model.addAttribute("userDto", new UserRegisterDto());

        return "index";
    }


    @GetMapping("/edit")
    public String showEditProfileForm(Model model) {
        Usuario usuario = AuthUtils.getAuthUser(userService);

        model.addAttribute("content", "views/edit-profile");
        model.addAttribute("userDto", new UserUpdateDto(usuario));

        return "index";
    }

    @PostMapping("/profile/edit")
    public String editProfile(@ModelAttribute("user") UserUpdateDto userDto) {
        userService.updateUser(userDto);

        return "redirect:/user/profile";
    }

    @GetMapping("/profile/edit-avatar")
    public String editAvatarProfile(@ModelAttribute("user") UserUpdateDto userDto, Model model) {
        Usuario usuario = AuthUtils.getAuthUser(userService);

        List<Avatar> listaAvatares = avatarService.findAllAvatars();

        model.addAttribute("content", "views/galeria-avatar");
        model.addAttribute("usuario", usuario);
        model.addAttribute("listavatares", listaAvatares);
        return "index";
    }
    @PostMapping("/profile/update-avatar")
    public String updateAvatarProfile(@ModelAttribute("user") UserUpdateDto userDto, @RequestParam("selectedAvatarId") Integer selectedAvatarId) {
        Usuario usuario = AuthUtils.getAuthUser(userService);
        Optional<Avatar> avatarOptional = avatarService.findAvatarById(Integer.parseInt(String.valueOf(selectedAvatarId)));

        if (avatarOptional.isEmpty()) {
            //poner un error
        }
        Avatar avatar = avatarOptional.get();

        usuario.setAvatar(avatar);
        UserUpdateDto userUpdateDto = new UserUpdateDto(usuario);

        userService.updateUser(userUpdateDto);

        return "redirect:/user/profile";
    }
}
