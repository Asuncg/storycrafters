package es.asun.StoryCrafters.controller;

import es.asun.StoryCrafters.entity.Usuario;
import es.asun.StoryCrafters.exceptions.AvatarNotFoundException;
import es.asun.StoryCrafters.exceptions.UsuarioException;
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

/**
 * Controlador de autenticación para manejar el registro, inicio de sesión y recuperación de contraseñas de usuarios.
 */
@Controller
@SessionAttributes({"DataUser"})
public class AuthController {

    @Autowired
    private UserService userService;

    /**
     * Muestra la página de inicio.
     *
     * @param model el modelo para la vista
     * @return el nombre de la vista de inicio
     */
    @GetMapping(value = {"/", "/index"})
    public String home(Model model) {
        try {
            Usuario usuario = AuthUtils.getAuthUser(userService);
            model.addAttribute("usuario", new UserUpdateDto(usuario));
            model.addAttribute("currentPage", "home");
            model.addAttribute("content", "views/home");
            return "index";
        } catch (UsuarioException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Muestra la página de inicio de sesión.
     *
     * @return el nombre de la vista de inicio de sesión
     */
    @GetMapping(value = {"/login"})
    public String login() {
        return "login";
    }

    /**
     * Muestra el formulario de registro de usuarios.
     *
     * @param model el modelo para la vista
     * @return el nombre de la vista de registro
     */
    @GetMapping(value = {"/registro"})
    public String showRegistrationForm(Model model) {
        UserRegisterDto user = new UserRegisterDto();
        model.addAttribute("usuario", user);
        return "registro";
    }

    /**
     * Procesa el registro de un nuevo usuario.
     *
     * @param userRegisterDto el DTO con los datos del usuario a registrar
     * @param result          los resultados de la validación
     * @return una respuesta HTTP indicando el resultado del registro
     */
    @PostMapping("/registro/save")
    public ResponseEntity<String> registration(@Valid @ModelAttribute("user") UserRegisterDto userRegisterDto,
                                               BindingResult result) {
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

    /**
     * Muestra la página para recuperar la contraseña.
     *
     * @return el nombre de la vista de recuperación de contraseña
     */
    @GetMapping("/recuperar-password")
    public String showForgotPasswordPage() {
        return "recuperar-password";
    }

    /**
     * Procesa la solicitud de recuperación de contraseña.
     *
     * @param email              el email del usuario que quiere recuperar la contraseña
     * @param redirectAttributes los atributos para redireccionar
     * @return la redirección a la página de recuperación de contraseña
     */
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

    /**
     * Muestra la página para restablecer la contraseña.
     *
     * @param token el token para restablecer la contraseña
     * @param model el modelo para la vista
     * @return el nombre de la vista de restablecimiento de contraseña
     */
    @GetMapping("/restablecer-password")
    public String showResetPasswordPage(@RequestParam("token") String token, Model model) {
        model.addAttribute("token", token);
        return "restablecer-password";
    }

    /**
     * Procesa el restablecimiento de la contraseña.
     *
     * @param token              el token para restablecer la contraseña
     * @param newPassword        la nueva contraseña
     * @param redirectAttributes los atributos para redireccionar
     * @return la redirección a la página de restablecimiento de contraseña
     */
    @PostMapping("/restablecer-password")
    public String processResetPassword(@RequestParam("token") String token,
                                       @RequestParam("newPassword") String newPassword,
                                       RedirectAttributes redirectAttributes) {
        boolean result = userService.resetPassword(token, newPassword);
        if (result) {
            redirectAttributes.addFlashAttribute("passwordSuccess", true);
        } else {
            redirectAttributes.addFlashAttribute("passwordError", true);
        }
        return "redirect:/login";
    }
}
