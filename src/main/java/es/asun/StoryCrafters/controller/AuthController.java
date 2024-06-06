package es.asun.StoryCrafters.controller;

import es.asun.StoryCrafters.entity.Usuario;
import es.asun.StoryCrafters.exceptions.AvatarNotFoundException;
import es.asun.StoryCrafters.model.UserRegisterDto;
import es.asun.StoryCrafters.model.UserUpdateDto;
import es.asun.StoryCrafters.service.UserService;
import es.asun.StoryCrafters.utils.AuthUtils;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
@SessionAttributes({"DataUser"})
public class AuthController {
    @Autowired
    private UserService userService;

    @GetMapping(value = {"/", "/index"})
    public String home(Model model) {
        Usuario usuario = AuthUtils.getAuthUser(userService);
        model.addAttribute("usuario", new UserUpdateDto(usuario));
        model.addAttribute("currentPage", "home");
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
    public ResponseEntity<String> registration(@Valid @ModelAttribute("user") UserRegisterDto userRegisterDto,
                                               BindingResult result,
                                               Model model) {
        Optional<Usuario> usuarioOptional = userService.findUserByEmail(userRegisterDto.getEmail());

        if (usuarioOptional.isPresent()) {
            Usuario usuario = usuarioOptional.get();

            if (usuario.getEmail() != null && !usuario.getEmail().isEmpty()) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body("There is already an account registered with the same email");
            }
        }

        if (result.hasErrors()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Validation error");
        }

        try {
            userService.saveUser(userRegisterDto);
        } catch (AvatarNotFoundException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error saving user");
        }

        return ResponseEntity.ok("Registration successful");
    }

    @GetMapping("/recuperar-password")
    public String showForgotPasswordPage() {
        return "recuperar-password";
    }

    @PostMapping("/recuperar-password")
    public String processForgotPassword(@RequestParam("email") String email, RedirectAttributes redirectAttributes) {
        boolean result = userService.processForgotPassword(email);
        if (result) {
            redirectAttributes.addAttribute("success", true);
        } else {
            redirectAttributes.addAttribute("error", true);
        }
        return "redirect:/recuperar-password";
    }


    @GetMapping("/restablecer-password")
    public String showResetPasswordPage(@RequestParam("token") String token, Model model) {
        model.addAttribute("token", token);
        return "restablecer-password";
    }

    @PostMapping("/restablecer-password")
    public String processResetPassword(@RequestParam("token") String token,
                                       @RequestParam("newPassword") String newPassword,
                                       RedirectAttributes redirectAttributes) {
        boolean result = userService.resetPassword(token, newPassword);
        if (result) {
            redirectAttributes.addAttribute("success", true);
        } else {
            redirectAttributes.addAttribute("error", true);
        }
        return "redirect:/restablecer-password";
    }
}


