package es.asun.StoryCrafters.controller;

import es.asun.StoryCrafters.entity.Usuario;
import es.asun.StoryCrafters.model.UserRegisterDto;
import es.asun.StoryCrafters.model.UserUpdateDto;
import es.asun.StoryCrafters.service.UserService;
import es.asun.StoryCrafters.utilidades.AuthUtils;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.SessionAttributes;


@Controller
@SessionAttributes({"DataUser"})
public class AuthController {
    @Autowired
    private UserService userService;

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
                               Model model
    ) {
        Usuario existingUsuario = userService.findUserByEmail(userRegisterDto.getEmail());

        if (existingUsuario != null && existingUsuario.getEmail() != null && !existingUsuario.getEmail().isEmpty()) {
            result.rejectValue("email", null,
                    "There is already an account registered with the same email");
        }

        if (result.hasErrors()) {
            model.addAttribute("user", userRegisterDto);
            return "redirect:/registro?error";
        }

        userService.saveUser(userRegisterDto);
        return "redirect:/registro?success";
    }
}


