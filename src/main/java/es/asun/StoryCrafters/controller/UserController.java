package es.asun.StoryCrafters.controller;

import es.asun.StoryCrafters.entity.Usuario;
import es.asun.StoryCrafters.model.UserRegisterDto;
import es.asun.StoryCrafters.model.UserUpdateDto;
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


    @GetMapping(value= {"/profile"})
    public String viewprofile(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        // Obtener el objeto Usuario del usuario autenticado (puedes ajustar esto según tu implementación)
        Usuario usuario = userService.findUserByEmail(username);

        // Pasar la información del usuario al modelo
        model.addAttribute("usuario", usuario);
        model.addAttribute("userDto", new UserRegisterDto()); // Añadir un UserRegisterDto vacío al modelo

        return "views/profile";
    }


    @GetMapping("/edit")
    public String showEditProfileForm(Model model) {
        // Obtener el usuario autenticado
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Usuario usuario = userService.findUserByEmail(username);

        // Pasar el usuario al modelo
        model.addAttribute("usuario", usuario);
        model.addAttribute("userDto", new UserRegisterDto(usuario)); // Pasar un UserUpdateDto con los datos del usuario al modelo

        // Retornar la vista de edición de perfil
        return "views/edit-profile";
    }

    @PostMapping("/profile/edit")
    public String editProfile(@ModelAttribute("user") UserUpdateDto userDto) {
        // Actualizar los datos del usuario
        userService.updateUser(userDto);

        // Redirigir a la página de perfil después de la edición
        return "redirect:/user/profile";
    }
}
