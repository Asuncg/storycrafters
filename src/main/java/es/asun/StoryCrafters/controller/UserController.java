package es.asun.StoryCrafters.controller;

import es.asun.StoryCrafters.entity.User;
import es.asun.StoryCrafters.model.UserDto;
import es.asun.StoryCrafters.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    // handler method to handle list of users
    @GetMapping("/listusers")
    public String users(Model model){
        List<UserDto> users = userService.findAllUsers();
        model.addAttribute("users", users);
        return "users";
    }

    @GetMapping(value= {"/profile"})
    public String viewprofile(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        // Obtener el objeto User del usuario autenticado (puedes ajustar esto según tu implementación)
        User user = userService.findUserByEmail(username);

        // Pasar la información del usuario al modelo
        model.addAttribute("user", user);

        return "views/profile";
    }


    @GetMapping("/edit")
    public String showEditProfileForm(Model model) {
        // Obtener el usuario autenticado
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userService.findUserByEmail(username);

        // Pasar el usuario al modelo
        model.addAttribute("user", user);

        // Retornar la vista de edición de perfil
        return "edit-profile";
    }

    @PostMapping("/profile/edit")
    public String editProfile(@ModelAttribute("user") UserDto userDto) {
        // Actualizar los datos del usuario
        userService.updateUser(userDto);

        // Redirigir a la página de perfil después de la edición
        return "redirect:/profile";
    }
}
