package es.asun.StoryCrafters.controller;

import es.asun.StoryCrafters.entity.Usuario;
import es.asun.StoryCrafters.exceptions.UsuarioException;
import es.asun.StoryCrafters.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

/**
 * Controlador de asesoramiento global para proporcionar datos comunes a todas las vistas.
 */
@ControllerAdvice
public class GlobalControllerAdvice {

    @Autowired
    private UserService userService;

    /**
     * Obtiene el usuario actualmente autenticado y lo agrega como atributo del modelo.
     *
     * @return el usuario actualmente autenticado
     * @throws UsuarioException si el usuario no se encuentra
     */
    @ModelAttribute("currentUser")
    public Usuario getCurrentUser() throws UsuarioException {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            String email = ((UserDetails) principal).getUsername();
            Optional<Usuario> usuarioOptional = userService.findUserByEmail(email);

            if (usuarioOptional.isEmpty()) {
                throw new UsuarioException("ERROR. Usuario no encontrado");
            }
            return usuarioOptional.get();
        }
        return null;
    }
}

