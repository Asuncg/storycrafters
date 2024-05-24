package es.asun.StoryCrafters.controller;

import es.asun.StoryCrafters.entity.Usuario;
import es.asun.StoryCrafters.exceptions.AvatarNotFoundException;
import es.asun.StoryCrafters.model.UserRegisterDto;
import es.asun.StoryCrafters.model.UserUpdateDto;
import es.asun.StoryCrafters.service.AvatarService;
import es.asun.StoryCrafters.service.UserService;
import es.asun.StoryCrafters.utils.AuthUtils;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.SessionAttributes;

import java.util.Optional;

@Controller
@SessionAttributes({"DataUser"})
public class AuthController {
    @Autowired
    private UserService userService;
    @Autowired
    private AvatarService avatarService;

    @GetMapping(value = {"/", "/index"})
    public String home(Model model) {
        Usuario usuario = AuthUtils.getAuthUser(userService);
        model.addAttribute("usuario", new UserUpdateDto(usuario));

        model.addAttribute("content", "views/home");
        return "index";
    }

    @GetMapping(value = {"/login"})
    public String login() {
        return "login";
    }

    @GetMapping(value = {"/registro"})
    public String showRegistrationForm(Model model) {
        UserRegisterDto user = new UserRegisterDto();
        model.addAttribute("usuario", user);
        return "registro";
    }

    @PostMapping("/registro/save")
    public String registration(@Valid @ModelAttribute("user") UserRegisterDto userRegisterDto,
                               BindingResult result,
                               Model model) {
        Optional<Usuario> usuarioOptional = userService.findUserByEmail(userRegisterDto.getEmail());

        if (usuarioOptional.isPresent()) {
            Usuario usuario = usuarioOptional.get();

            if (usuario.getEmail() != null && !usuario.getEmail().isEmpty()) {
                result.rejectValue("email", null,
                        "There is already an account registered with the same email");
            }
        }

        if (result.hasErrors()) {
            model.addAttribute("user", userRegisterDto);
            return "redirect:/registro?error";
        }

        try {
            userService.saveUser(userRegisterDto);
        } catch (AvatarNotFoundException e) {
            model.addAttribute("user", userRegisterDto);
            return "redirect:/registro?error";
        }

        return "redirect:/registro?success";

    }
}


