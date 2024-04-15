package es.asun.StoryCrafters.controller;

import es.asun.StoryCrafters.entity.Usuario;
import es.asun.StoryCrafters.model.UserRegisterDto;
import es.asun.StoryCrafters.repository.UserRepository;
import es.asun.StoryCrafters.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.csrf.CsrfToken;
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


    private UserRepository userRepo;

    @Autowired
    private UserService userService;

    @GetMapping(value = {"/", "/index"})
    public String home(Model model) {
        String content= "views/home";
        model.addAttribute("content", content);
        return "index";
    }

    @GetMapping(value = {"/login"})
    public String login() {
        return "login";
    }

    @GetMapping(value = {"/registro"})
    public String showRegistrationForm(Model model) {
        // create model object to store form data
        UserRegisterDto user = new UserRegisterDto();
        model.addAttribute("usuario", user);
        return "registro";
    }

    // handler method to handle user registration form submit request
    @PostMapping("/registro/save")
    public String registration(@Valid @ModelAttribute("user") UserRegisterDto userRegisterDto,
                               BindingResult result,
                               Model model,
                               CsrfToken csrfToken){
        Usuario existingUsuario = userService.findUserByEmail(userRegisterDto.getEmail());

        String csrfTokenValue = csrfToken.getToken();

        if(existingUsuario != null && existingUsuario.getEmail() != null && !existingUsuario.getEmail().isEmpty()){
            result.rejectValue("email", null,
                    "There is already an account registered with the same email");
        }

        if(result.hasErrors()){
            model.addAttribute("user", userRegisterDto);
            return "redirect:/registro?error&_csrf="+csrfTokenValue;
        }

        userService.saveUser(userRegisterDto);
        return "redirect:/registro?success&_csrf="+csrfTokenValue;
    }


}


