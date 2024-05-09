package es.asun.StoryCrafters.controller;

import es.asun.StoryCrafters.entity.Usuario;
import es.asun.StoryCrafters.model.UserRegisterDto;
import es.asun.StoryCrafters.model.UserUpdateDto;
import es.asun.StoryCrafters.service.UserService;
import es.asun.StoryCrafters.utilidades.AuthUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping(value= {"/profile"})
    public String viewprofile(Model model) {
        Usuario usuario = AuthUtils.getAuthUser(userService);

        model.addAttribute("content", "views/profile");
        model.addAttribute("usuario", usuario);
        model.addAttribute("userDto", new UserRegisterDto()); // Añadir un UserRegisterDto vacío al modelo

        return "index";
    }


    @GetMapping("/edit")
    public String showEditProfileForm(Model model) {
        Usuario usuario = AuthUtils.getAuthUser(userService);

        model.addAttribute("content", "views/edit-profile");
        model.addAttribute("userDto", new UserUpdateDto(usuario)); // Pasar un UserUpdateDto con los datos del usuario al modelo

        return "index";
    }

    @PostMapping("/profile/edit")
    public String editProfile(@ModelAttribute("user") UserUpdateDto userDto, Model model) {
        userService.updateUser(userDto);

        return "redirect:/user/profile";
    }
}
